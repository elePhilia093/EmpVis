package com.gsz.empvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gsz.empvis.dto.attendance.AttendanceQueryDTO;
import com.gsz.empvis.entity.*;
import com.gsz.empvis.exception.BusinessException;
import com.gsz.empvis.mapper.*;
import com.gsz.empvis.service.EmpAttendanceService;
import com.gsz.empvis.vo.attendance.AttendanceVO;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmpAttendanceServiceImpl implements EmpAttendanceService {
    private static final String ADMIN_ROLE_CODE = "ADMIN";
    private static final String MANAGER_ROLE_CODE = "MANAGER";
    private final SysRoleMapper sysRoleMapper;

    /**
     * 上班时间
     */
    private static final LocalTime WORK_START =
            LocalTime.of(9, 0);

    /**
     * 下班时间
     */
    private static final LocalTime WORK_END =
            LocalTime.of(18, 0);

    private final EmpAttendanceMapper empAttendanceMapper;
    private final EmpEmployeeMapper empEmployeeMapper;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    public EmpAttendanceServiceImpl(
            EmpAttendanceMapper empAttendanceMapper,
            EmpEmployeeMapper empEmployeeMapper,
            SysUserMapper sysUserMapper,
            SysRoleMapper sysRoleMapper,
            SysUserRoleMapper sysUserRoleMapper) {

        this.empAttendanceMapper = empAttendanceMapper;
        this.empEmployeeMapper = empEmployeeMapper;
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    /**
     * 分页查询考勤记录
     */
    @Override
    public IPage<AttendanceVO> page(
            AttendanceQueryDTO queryDTO,
            Long userId) {

        boolean admin = isAdmin(userId);
        boolean manager = isManager(userId);

        EmpEmployee currentEmployee = null;

        /*
         * 管理员可以不绑定员工
         * 主管和普通员工需要获取自己的员工信息
         */
        if (!admin) {
            currentEmployee =
                    getEmployeeByUserId(userId);
        }

        Page<EmpAttendance> page =
                new Page<>(
                        queryDTO.getCurrent(),
                        queryDTO.getSize()
                );

        LambdaQueryWrapper<EmpAttendance> wrapper =
                new LambdaQueryWrapper<>();

        /*
         * 系统管理员：
         * 可以查询全部考勤记录
         */
        if (admin) {

            if (queryDTO.getEmployeeId() != null) {

                wrapper.eq(
                        EmpAttendance::getEmployeeId,
                        queryDTO.getEmployeeId()
                );
            }

            /*
             * 主管：
             * 只能查询本部门员工的考勤
             */
        } else if (manager) {

            List<EmpEmployee> employees =
                    empEmployeeMapper.selectList(
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
             * 当前部门没有员工时返回空结果
             */
            if (employeeIds.isEmpty()) {

                wrapper.eq(
                        EmpAttendance::getEmployeeId,
                        -1L
                );

            } else if (queryDTO.getEmployeeId() != null) {

                /*
                 * 指定员工查询时，
                 * 必须确认员工属于当前主管所在部门
                 */
                if (!employeeIds.contains(
                        queryDTO.getEmployeeId())) {

                    throw new BusinessException(
                            "无权查询其他部门员工的考勤记录"
                    );
                }

                wrapper.eq(
                        EmpAttendance::getEmployeeId,
                        queryDTO.getEmployeeId()
                );

            } else {

                wrapper.in(
                        EmpAttendance::getEmployeeId,
                        employeeIds
                );
            }

            /*
             * 普通员工：
             * 只能查询自己的考勤
             */
        } else {

            wrapper.eq(
                    EmpAttendance::getEmployeeId,
                    currentEmployee.getId()
            );
        }

        /*
         * 考勤日期
         */
        wrapper.eq(
                queryDTO.getAttendanceDate() != null,
                EmpAttendance::getAttendanceDate,
                queryDTO.getAttendanceDate()
        );

        /*
         * 考勤状态
         */
        wrapper.eq(
                queryDTO.getAttendanceStatus() != null,
                EmpAttendance::getAttendanceStatus,
                queryDTO.getAttendanceStatus()
        );

        /*
         * 按日期倒序
         */
        wrapper.orderByDesc(
                EmpAttendance::getAttendanceDate
        );

        wrapper.orderByDesc(
                EmpAttendance::getId
        );

        IPage<EmpAttendance> result =
                empAttendanceMapper.selectPage(
                        page,
                        wrapper
                );

        List<AttendanceVO> records =
                result.getRecords()
                        .stream()
                        .map(this::toVO)
                        .toList();

        fillEmployeeName(records);

        Page<AttendanceVO> voPage =
                new Page<>(
                        result.getCurrent(),
                        result.getSize(),
                        result.getTotal()
                );

        voPage.setRecords(records);

        return voPage;
    }

    /**
     * 上班打卡
     */
    @Override
    public void checkIn(Long userId) {

        EmpEmployee employee =
                getEmployeeByUserId(userId);

        LocalDate today =
                LocalDate.now();

        LocalTime now =
                LocalTime.now();

        EmpAttendance attendance =
                getTodayAttendance(
                        employee.getId(),
                        today
                );

        /*
         * 已经上班打卡
         */
        if (attendance != null
                && attendance.getCheckInTime() != null) {

            throw new BusinessException(
                    "今日已完成上班打卡"
            );
        }

        /*
         * 今天还没有考勤记录
         */
        if (attendance == null) {

            attendance =
                    new EmpAttendance();

            attendance.setEmployeeId(
                    employee.getId()
            );

            attendance.setAttendanceDate(
                    today
            );

            attendance.setCheckInTime(
                    now
            );

            attendance.setCheckOutTime(
                    null
            );

            int lateMinutes =
                    calculateLateMinutes(now);

            attendance.setLateMinutes(
                    lateMinutes
            );

            attendance.setEarlyLeaveMinutes(
                    0
            );

            attendance.setAttendanceStatus(
                    calculateStatus(
                            lateMinutes,
                            0
                    )
            );

            attendance.setCreateTime(
                    LocalDateTime.now()
            );

            attendance.setUpdateTime(
                    LocalDateTime.now()
            );

            empAttendanceMapper.insert(
                    attendance
            );

        } else {

            /*
             * 存在记录但还没有上班打卡
             */
            int lateMinutes =
                    calculateLateMinutes(now);

            attendance.setCheckInTime(now);

            attendance.setLateMinutes(
                    lateMinutes
            );

            attendance.setAttendanceStatus(
                    calculateStatus(
                            lateMinutes,
                            attendance.getEarlyLeaveMinutes()
                    )
            );

            attendance.setUpdateTime(
                    LocalDateTime.now()
            );

            empAttendanceMapper.updateById(
                    attendance
            );
        }
    }

    /**
     * 下班打卡
     */
    @Override
    public void checkOut(Long userId) {

        EmpEmployee employee =
                getEmployeeByUserId(userId);

        LocalDate today =
                LocalDate.now();

        LocalTime now =
                LocalTime.now();

        EmpAttendance attendance =
                getTodayAttendance(
                        employee.getId(),
                        today
                );

        /*
         * 没有上班打卡记录
         */
        if (attendance == null
                || attendance.getCheckInTime() == null) {

            throw new BusinessException(
                    "请先完成上班打卡"
            );
        }

        int earlyLeaveMinutes =
                calculateEarlyLeaveMinutes(now);

        /*
         * 每次下班打卡都更新为最新时间
         */
        attendance.setCheckOutTime(now);

        attendance.setEarlyLeaveMinutes(
                earlyLeaveMinutes
        );

        attendance.setAttendanceStatus(
                calculateStatus(
                        attendance.getLateMinutes(),
                        earlyLeaveMinutes
                )
        );

        attendance.setUpdateTime(
                LocalDateTime.now()
        );

        empAttendanceMapper.updateById(
                attendance
        );
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
                empEmployeeMapper.selectById(
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
     * 查询当天考勤记录
     */
    private EmpAttendance getTodayAttendance(
            Long employeeId,
            LocalDate date) {

        LambdaQueryWrapper<EmpAttendance> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(
                EmpAttendance::getEmployeeId,
                employeeId
        );

        wrapper.eq(
                EmpAttendance::getAttendanceDate,
                date
        );

        return empAttendanceMapper.selectOne(
                wrapper
        );
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
     * 计算迟到分钟数
     */
    private Integer calculateLateMinutes(
            LocalTime checkInTime) {

        if (!checkInTime.isAfter(WORK_START)) {
            return 0;
        }

        return (int) Duration.between(
                WORK_START,
                checkInTime
        ).toMinutes();
    }

    /**
     * 计算早退分钟数
     */
    private Integer calculateEarlyLeaveMinutes(
            LocalTime checkOutTime) {

        if (!checkOutTime.isBefore(WORK_END)) {
            return 0;
        }

        return (int) Duration.between(
                checkOutTime,
                WORK_END
        ).toMinutes();
    }

    /**
     * 根据迟到和早退情况计算考勤状态
     *
     * 0-正常
     * 1-迟到
     * 2-早退
     * 3-迟到且早退
     */
    private Integer calculateStatus(
            Integer lateMinutes,
            Integer earlyLeaveMinutes) {

        boolean late =
                lateMinutes != null
                        && lateMinutes > 0;

        boolean earlyLeave =
                earlyLeaveMinutes != null
                        && earlyLeaveMinutes > 0;

        if (late && earlyLeave) {
            return 3;
        }

        if (late) {
            return 1;
        }

        if (earlyLeave) {
            return 2;
        }

        return 0;
    }

    /**
     * Entity 转 VO
     */
    private AttendanceVO toVO(
            EmpAttendance attendance) {

        AttendanceVO vo =
                new AttendanceVO();

        vo.setId(
                attendance.getId()
        );

        vo.setEmployeeId(
                attendance.getEmployeeId()
        );

        vo.setAttendanceDate(
                attendance.getAttendanceDate()
        );

        vo.setCheckInTime(
                attendance.getCheckInTime()
        );

        vo.setCheckOutTime(
                attendance.getCheckOutTime()
        );

        vo.setAttendanceStatus(
                attendance.getAttendanceStatus()
        );

        vo.setLateMinutes(
                attendance.getLateMinutes()
        );

        vo.setEarlyLeaveMinutes(
                attendance.getEarlyLeaveMinutes()
        );

        vo.setRemark(
                attendance.getRemark()
        );

        vo.setCreateTime(
                attendance.getCreateTime()
        );

        vo.setUpdateTime(
                attendance.getUpdateTime()
        );

        return vo;
    }

    /**
     * 批量填充员工姓名
     */
    private void fillEmployeeName(
            List<AttendanceVO> voList) {

        if (voList.isEmpty()) {
            return;
        }

        List<Long> employeeIds =
                voList.stream()
                        .map(AttendanceVO::getEmployeeId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

        if (employeeIds.isEmpty()) {
            return;
        }

        List<EmpEmployee> employees =
                empEmployeeMapper.selectBatchIds(
                        employeeIds
                );

        Map<Long, String> employeeNameMap =
                employees.stream()
                        .collect(Collectors.toMap(
                                EmpEmployee::getId,
                                EmpEmployee::getEmployeeName
                        ));

        for (AttendanceVO vo : voList) {

            vo.setEmployeeName(
                    employeeNameMap.get(
                            vo.getEmployeeId()
                    )
            );
        }
    }
}