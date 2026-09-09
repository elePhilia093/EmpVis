package com.gsz.empvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gsz.empvis.dto.leave.LeaveAddDTO;
import com.gsz.empvis.dto.leave.LeaveAuditDTO;
import com.gsz.empvis.dto.leave.LeaveQueryDTO;
import com.gsz.empvis.entity.*;
import com.gsz.empvis.exception.BusinessException;
import com.gsz.empvis.mapper.*;
import com.gsz.empvis.service.EmpLeaveService;
import com.gsz.empvis.vo.leave.LeaveVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmpLeaveServiceImpl implements EmpLeaveService {
    private static final String ADMIN_ROLE_CODE = "ADMIN";
    private static final String MANAGER_ROLE_CODE = "MANAGER";
    private final SysRoleMapper sysRoleMapper;


    private final EmpLeaveMapper empLeaveMapper;
    private final EmployeeMapper employeeMapper;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    public EmpLeaveServiceImpl(
            EmpLeaveMapper empLeaveMapper,
            EmployeeMapper employeeMapper,
            SysUserMapper sysUserMapper,
            SysRoleMapper sysRoleMapper,
            SysUserRoleMapper sysUserRoleMapper) {

        this.empLeaveMapper = empLeaveMapper;
        this.employeeMapper = employeeMapper;
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    /**
     * 分页查询请假记录
     */
    @Override
    public IPage<LeaveVO> page(
            LeaveQueryDTO queryDTO,
            Long userId) {

        boolean admin = isAdmin(userId);
        boolean manager = isManager(userId);

        EmpEmployee currentEmployee = null;

        /*
         * 管理员不需要绑定员工
         * 普通员工和主管需要获取自己的员工信息
         */
        if (!admin) {
            currentEmployee = getEmployeeByUserId(userId);
        }

        Page<EmpLeave> page =
                new Page<>(
                        queryDTO.getCurrent(),
                        queryDTO.getSize()
                );

        LambdaQueryWrapper<EmpLeave> wrapper =
                new LambdaQueryWrapper<>();

        /*
         * 系统管理员：可以查询全部记录
         */
        if (admin) {

            if (queryDTO.getEmployeeId() != null) {
                wrapper.eq(
                        EmpLeave::getEmployeeId,
                        queryDTO.getEmployeeId()
                );
            }

            /*
             * 主管：只能查询本部门员工
             */
        } else if (manager) {

            List<EmpEmployee> employees =
                    employeeMapper.selectList(
                            new LambdaQueryWrapper<EmpEmployee>()
                                    .eq(
                                            EmpEmployee::getDeptId,
                                            currentEmployee.getDeptId()
                                    )
                    );

            List<Long> employeeIds =
                    employees.stream()
                            .map(EmpEmployee::getId)
                            .toList();

            /*
             * 当前部门没有员工时，
             * 返回空结果
             */
            if (employeeIds.isEmpty()) {

                wrapper.eq(
                        EmpLeave::getEmployeeId,
                        -1L
                );

            } else {

                /*
                 * 如果主管指定员工查询，
                 * 仍然必须保证该员工属于当前部门
                 */
                if (queryDTO.getEmployeeId() != null) {

                    if (!employeeIds.contains(
                            queryDTO.getEmployeeId())) {

                        throw new BusinessException(
                                "无权查询其他部门员工的请假记录"
                        );
                    }

                    wrapper.eq(
                            EmpLeave::getEmployeeId,
                            queryDTO.getEmployeeId()
                    );

                } else {

                    wrapper.in(
                            EmpLeave::getEmployeeId,
                            employeeIds
                    );
                }
            }

            /*
             * 普通员工：只能查询自己的记录
             */
        } else {

            wrapper.eq(
                    EmpLeave::getEmployeeId,
                    currentEmployee.getId()
            );
        }

        /*
         * 审批状态
         */
        wrapper.eq(
                queryDTO.getApprovalStatus() != null,
                EmpLeave::getApprovalStatus,
                queryDTO.getApprovalStatus()
        );

        /*
         * 开始日期
         */
        if (queryDTO.getStartDate() != null) {

            wrapper.ge(
                    EmpLeave::getStartTime,
                    queryDTO.getStartDate()
                            .atStartOfDay()
            );
        }

        /*
         * 结束日期
         */
        if (queryDTO.getEndDate() != null) {

            wrapper.le(
                    EmpLeave::getStartTime,
                    queryDTO.getEndDate()
                            .atTime(23, 59, 59)
            );
        }

        wrapper.orderByDesc(
                EmpLeave::getCreateTime
        );

        IPage<EmpLeave> result =
                empLeaveMapper.selectPage(
                        page,
                        wrapper
                );

        List<LeaveVO> records =
                result.getRecords()
                        .stream()
                        .map(this::toVO)
                        .toList();

        fillEmployeeName(records);
        fillApproverName(records);

        Page<LeaveVO> voPage =
                new Page<>(
                        result.getCurrent(),
                        result.getSize(),
                        result.getTotal()
                );

        voPage.setRecords(records);

        return voPage;
    }

    /**
     * 提交请假申请
     */
    @Override
    @Transactional
    public void add(
            LeaveAddDTO addDTO,
            Long userId) {

        EmpEmployee employee =
                getEmployeeByUserId(userId);

        LocalDateTime startTime =
                addDTO.getStartTime();

        LocalDateTime endTime =
                addDTO.getEndTime();

        /*
         * 检查时间先后
         */
        if (endTime.isBefore(startTime)) {

            throw new BusinessException(
                    "请假结束时间不能早于开始时间"
            );
        }

        /*
         * 按自然日计算请假天数
         * 同一天按1天计算
         */
        long days =
                ChronoUnit.DAYS.between(
                        startTime.toLocalDate(),
                        endTime.toLocalDate()
                ) + 1;

        if (days <= 0) {

            throw new BusinessException(
                    "请假时间无效"
            );
        }

        EmpLeave leave =
                new EmpLeave();

        leave.setEmployeeId(
                employee.getId()
        );

        leave.setLeaveType(
                addDTO.getLeaveType()
        );

        leave.setStartTime(startTime);
        leave.setEndTime(endTime);

        leave.setLeaveDays(
                BigDecimal.valueOf(days)
        );

        leave.setReason(
                addDTO.getReason()
        );

        /*
         * 新申请默认待审批
         */
        leave.setApprovalStatus(0);

        leave.setApproverId(null);
        leave.setApprovalTime(null);
        leave.setApprovalComment(null);

        leave.setCreateTime(
                LocalDateTime.now()
        );

        leave.setUpdateTime(
                LocalDateTime.now()
        );

        empLeaveMapper.insert(leave);
    }

    /**
     * 审批请假
     */
    @Override
    @Transactional
    public void audit(
            LeaveAuditDTO auditDTO,
            Long approverUserId) {

        EmpLeave leave =
                empLeaveMapper.selectById(
                        auditDTO.getId()
                );

        if (leave == null) {

            throw new BusinessException(
                    "请假记录不存在"
            );
        }

        /*
         * 只能审批待审批记录
         */
        if (!Objects.equals(
                leave.getApprovalStatus(),
                0)) {

            throw new BusinessException(
                    "该请假申请已经处理"
            );
        }

        boolean admin =
                isAdmin(approverUserId);

        boolean manager =
                isManager(approverUserId);

        /*
         * 只有系统管理员和主管可以审批
         */
        if (!admin && !manager) {

            throw new BusinessException(
                    "当前用户无权审批请假申请"
            );
        }

        /*
         * 系统管理员：
         * 可以审批全部员工
         */
        if (!admin) {

            EmpEmployee approverEmployee =
                    getEmployeeByUserId(
                            approverUserId
                    );

            EmpEmployee applicant =
                    employeeMapper.selectById(
                            leave.getEmployeeId()
                    );

            if (applicant == null) {

                throw new BusinessException(
                        "申请员工不存在"
                );
            }

            if (!Objects.equals(
                    applicant.getDeptId(),
                    approverEmployee.getDeptId())) {

                throw new BusinessException(
                        "无权审批其他部门员工的请假申请"
                );
            }
        }

        Integer status =
                auditDTO.getApprovalStatus();

        /*
         * 1：通过
         * 2：驳回
         */
        if (status == null
                || (status != 1 && status != 2)) {

            throw new BusinessException(
                    "审批状态无效"
            );
        }

        leave.setApprovalStatus(status);

        leave.setApproverId(
                approverUserId
        );

        leave.setApprovalTime(
                LocalDateTime.now()
        );

        leave.setApprovalComment(
                auditDTO.getApprovalComment()
        );

        leave.setUpdateTime(
                LocalDateTime.now()
        );

        empLeaveMapper.updateById(leave);
    }

    /**
     * 根据用户ID获取员工
     */
    private EmpEmployee getEmployeeByUserId(
            Long userId) {

        SysUser user =
                sysUserMapper.selectById(userId);

        if (user == null) {

            throw new BusinessException(
                    "用户不存在"
            );
        }

        if (user.getEmployeeId() == null) {

            throw new BusinessException(
                    "当前用户未绑定员工"
            );
        }

        EmpEmployee employee =
                employeeMapper.selectById(
                        user.getEmployeeId()
                );

        if (employee == null) {

            throw new BusinessException(
                    "关联员工不存在"
            );
        }

        return employee;
    }

    /**
     * 判断是否为系统管理员
     */
    private boolean isAdmin(Long userId) {

        SysRole role = sysRoleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>()
                        .eq(
                                SysRole::getRoleCode,
                                ADMIN_ROLE_CODE
                        )
        );

        if (role == null) {
            return false;
        }

        return sysUserRoleMapper.selectCount(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(
                                SysUserRole::getUserId,
                                userId
                        )
                        .eq(
                                SysUserRole::getRoleId,
                                role.getId()
                        )
        ) > 0;
    }

    /**
     * 判断是否为主管
     */
    private boolean isManager(Long userId) {

        SysRole role = sysRoleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>()
                        .eq(
                                SysRole::getRoleCode,
                                MANAGER_ROLE_CODE
                        )
        );

        if (role == null) {
            return false;
        }

        return sysUserRoleMapper.selectCount(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(
                                SysUserRole::getUserId,
                                userId
                        )
                        .eq(
                                SysUserRole::getRoleId,
                                role.getId()
                        )
        ) > 0;
    }

    /**
     * Entity 转 VO
     */
    private LeaveVO toVO(
            EmpLeave leave) {

        LeaveVO vo =
                new LeaveVO();

        vo.setId(
                leave.getId()
        );

        vo.setEmployeeId(
                leave.getEmployeeId()
        );

        vo.setLeaveType(
                leave.getLeaveType()
        );

        vo.setStartTime(
                leave.getStartTime()
        );

        vo.setEndTime(
                leave.getEndTime()
        );

        vo.setLeaveDays(
                leave.getLeaveDays()
        );

        vo.setReason(
                leave.getReason()
        );

        vo.setApprovalStatus(
                leave.getApprovalStatus()
        );

        vo.setApproverId(
                leave.getApproverId()
        );

        vo.setApprovalTime(
                leave.getApprovalTime()
        );

        vo.setApprovalComment(
                leave.getApprovalComment()
        );

        vo.setCreateTime(
                leave.getCreateTime()
        );

        vo.setUpdateTime(
                leave.getUpdateTime()
        );

        return vo;
    }

    /**
     * 批量填充员工姓名
     */
    private void fillEmployeeName(
            List<LeaveVO> voList) {

        if (voList.isEmpty()) {
            return;
        }

        List<Long> employeeIds =
                voList.stream()
                        .map(LeaveVO::getEmployeeId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

        if (employeeIds.isEmpty()) {
            return;
        }

        List<EmpEmployee> employees =
                employeeMapper.selectBatchIds(
                        employeeIds
                );

        Map<Long, String> nameMap =
                employees.stream()
                        .collect(Collectors.toMap(
                                EmpEmployee::getId,
                                EmpEmployee::getEmployeeName
                        ));

        for (LeaveVO vo : voList) {

            vo.setEmployeeName(
                    nameMap.get(
                            vo.getEmployeeId()
                    )
            );
        }
    }

    /**
     * 批量填充审批人姓名
     */
    private void fillApproverName(
            List<LeaveVO> voList) {

        if (voList.isEmpty()) {
            return;
        }

        List<Long> userIds =
                voList.stream()
                        .map(LeaveVO::getApproverId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

        if (userIds.isEmpty()) {
            return;
        }

        List<SysUser> users =
                sysUserMapper.selectBatchIds(
                        userIds
                );

        Map<Long, String> nameMap =
                users.stream()
                        .collect(Collectors.toMap(
                                SysUser::getId,
                                SysUser::getUsername
                        ));

        for (LeaveVO vo : voList) {

            vo.setApproverName(
                    nameMap.get(
                            vo.getApproverId()
                    )
            );
        }
    }
}