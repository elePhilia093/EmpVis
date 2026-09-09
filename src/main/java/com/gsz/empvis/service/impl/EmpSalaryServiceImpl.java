package com.gsz.empvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gsz.empvis.dto.salary.SalaryAddDTO;
import com.gsz.empvis.dto.salary.SalaryQueryDTO;
import com.gsz.empvis.dto.salary.SalaryUpdateDTO;
import com.gsz.empvis.entity.EmpEmployee;
import com.gsz.empvis.entity.EmpSalary;
import com.gsz.empvis.entity.SysRole;
import com.gsz.empvis.entity.SysUser;
import com.gsz.empvis.entity.SysUserRole;
import com.gsz.empvis.exception.BusinessException;
import com.gsz.empvis.mapper.EmployeeMapper;
import com.gsz.empvis.mapper.EmpSalaryMapper;
import com.gsz.empvis.mapper.SysRoleMapper;
import com.gsz.empvis.mapper.SysUserMapper;
import com.gsz.empvis.mapper.SysUserRoleMapper;
import com.gsz.empvis.service.EmpSalaryService;
import com.gsz.empvis.vo.salary.SalaryVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmpSalaryServiceImpl implements EmpSalaryService {

    /**
     * 管理员角色编码
     */
    private static final String ADMIN_ROLE_CODE = "ADMIN";

    /**
     * 主管角色编码
     */
    private static final String MANAGER_ROLE_CODE = "MANAGER";

    /**
     * 薪资月份格式
     */
    private static final DateTimeFormatter MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM");

    private final EmpSalaryMapper empSalaryMapper;
    private final EmployeeMapper employeeMapper;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;

    public EmpSalaryServiceImpl(
            EmpSalaryMapper empSalaryMapper,
            EmployeeMapper employeeMapper,
            SysUserMapper sysUserMapper,
            SysUserRoleMapper sysUserRoleMapper,
            SysRoleMapper sysRoleMapper) {

        this.empSalaryMapper = empSalaryMapper;
        this.employeeMapper = employeeMapper;
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    /**
     * 分页查询薪资
     */
    @Override
    public IPage<SalaryVO> page(
            SalaryQueryDTO queryDTO,
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

        Page<EmpSalary> page =
                new Page<>(
                        queryDTO.getCurrent(),
                        queryDTO.getSize()
                );

        LambdaQueryWrapper<EmpSalary> wrapper =
                new LambdaQueryWrapper<>();

        /*
         * 管理员：查询全部
         */
        if (admin) {

            if (queryDTO.getEmployeeId() != null) {

                wrapper.eq(
                        EmpSalary::getEmployeeId,
                        queryDTO.getEmployeeId()
                );
            }

            /*
             * 主管：查询本部门
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

            if (employeeIds.isEmpty()) {

                wrapper.eq(
                        EmpSalary::getEmployeeId,
                        -1L
                );

            } else if (queryDTO.getEmployeeId() != null) {

                /*
                 * 指定员工时必须属于主管所在部门
                 */
                if (!employeeIds.contains(
                        queryDTO.getEmployeeId())) {

                    throw new BusinessException(
                            "无权查询其他部门员工的薪资"
                    );
                }

                wrapper.eq(
                        EmpSalary::getEmployeeId,
                        queryDTO.getEmployeeId()
                );

            } else {

                wrapper.in(
                        EmpSalary::getEmployeeId,
                        employeeIds
                );
            }

            /*
             * 普通员工：只能看自己的
             */
        } else {

            wrapper.eq(
                    EmpSalary::getEmployeeId,
                    currentEmployee.getId()
            );
        }

        /*
         * 按月份查询
         */
        if (queryDTO.getSalaryMonth() != null
                && !queryDTO.getSalaryMonth().isBlank()) {

            LocalDate salaryMonth =
                    parseSalaryMonth(
                            queryDTO.getSalaryMonth()
                    );

            wrapper.eq(
                    EmpSalary::getSalaryMonth,
                    salaryMonth
            );
        }

        /*
         * 发放状态
         */
        wrapper.eq(
                queryDTO.getPaymentStatus() != null,
                EmpSalary::getPaymentStatus,
                queryDTO.getPaymentStatus()
        );

        /*
         * 最新月份优先
         */
        wrapper.orderByDesc(
                EmpSalary::getSalaryMonth
        );

        wrapper.orderByDesc(
                EmpSalary::getId
        );

        IPage<EmpSalary> result =
                empSalaryMapper.selectPage(
                        page,
                        wrapper
                );

        List<SalaryVO> records =
                result.getRecords()
                        .stream()
                        .map(this::toVO)
                        .toList();

        fillEmployeeName(records);

        Page<SalaryVO> voPage =
                new Page<>(
                        result.getCurrent(),
                        result.getSize(),
                        result.getTotal()
                );

        voPage.setRecords(records);

        return voPage;
    }

    /**
     * 新增薪资
     */
    @Override
    @Transactional
    public void add(SalaryAddDTO addDTO) {

        /*
         * 检查员工
         */
        EmpEmployee employee =
                employeeMapper.selectById(
                        addDTO.getEmployeeId()
                );

        if (employee == null) {

            throw new BusinessException(
                    "员工不存在"
            );
        }

        LocalDate salaryMonth =
                parseSalaryMonth(
                        addDTO.getSalaryMonth()
                );

        /*
         * 检查员工+月份是否已经存在
         */
        checkSalaryExists(
                addDTO.getEmployeeId(),
                salaryMonth,
                null
        );

        /*
         * 计算应发工资
         */
        BigDecimal grossSalary =
                calculateGrossSalary(
                        addDTO.getBaseSalary(),
                        addDTO.getPerformanceSalary(),
                        addDTO.getAllowance(),
                        addDTO.getBonus()
                );

        /*
         * 计算实发工资
         */
        BigDecimal netSalary =
                calculateNetSalary(
                        grossSalary,
                        addDTO.getOtherDeduction(),
                        addDTO.getIncomeTax()
                );

        EmpSalary salary =
                new EmpSalary();

        salary.setEmployeeId(
                addDTO.getEmployeeId()
        );

        salary.setSalaryMonth(
                salaryMonth
        );

        salary.setBaseSalary(
                addDTO.getBaseSalary()
        );

        salary.setPerformanceSalary(
                addDTO.getPerformanceSalary()
        );

        salary.setAllowance(
                addDTO.getAllowance()
        );

        salary.setBonus(
                addDTO.getBonus()
        );

        salary.setGrossSalary(
                grossSalary
        );

        salary.setOtherDeduction(
                addDTO.getOtherDeduction()
        );

        salary.setIncomeTax(
                addDTO.getIncomeTax()
        );

        salary.setNetSalary(
                netSalary
        );

        salary.setPaymentStatus(
                addDTO.getPaymentStatus()
        );

        /*
         * 已发放时记录发放时间
         */
        if (Objects.equals(
                addDTO.getPaymentStatus(),
                1)) {

            salary.setPaymentTime(
                    LocalDateTime.now()
            );

        } else {

            salary.setPaymentTime(null);
        }

        salary.setRemark(
                addDTO.getRemark()
        );

        salary.setCreateTime(
                LocalDateTime.now()
        );

        salary.setUpdateTime(
                LocalDateTime.now()
        );

        empSalaryMapper.insert(salary);
    }

    /**
     * 修改薪资
     */
    @Override
    @Transactional
    public void update(
            SalaryUpdateDTO updateDTO) {

        EmpSalary salary =
                empSalaryMapper.selectById(
                        updateDTO.getId()
                );

        if (salary == null) {

            throw new BusinessException(
                    "薪资记录不存在"
            );
        }

        /*
         * 检查员工
         */
        EmpEmployee employee =
                employeeMapper.selectById(
                        updateDTO.getEmployeeId()
                );

        if (employee == null) {

            throw new BusinessException(
                    "员工不存在"
            );
        }

        LocalDate salaryMonth =
                parseSalaryMonth(
                        updateDTO.getSalaryMonth()
                );

        /*
         * 修改时排除当前记录
         */
        checkSalaryExists(
                updateDTO.getEmployeeId(),
                salaryMonth,
                updateDTO.getId()
        );

        /*
         * 重新计算应发工资
         */
        BigDecimal grossSalary =
                calculateGrossSalary(
                        updateDTO.getBaseSalary(),
                        updateDTO.getPerformanceSalary(),
                        updateDTO.getAllowance(),
                        updateDTO.getBonus()
                );

        /*
         * 重新计算实发工资
         */
        BigDecimal netSalary =
                calculateNetSalary(
                        grossSalary,
                        updateDTO.getOtherDeduction(),
                        updateDTO.getIncomeTax()
                );

        salary.setEmployeeId(
                updateDTO.getEmployeeId()
        );

        salary.setSalaryMonth(
                salaryMonth
        );

        salary.setBaseSalary(
                updateDTO.getBaseSalary()
        );

        salary.setPerformanceSalary(
                updateDTO.getPerformanceSalary()
        );

        salary.setAllowance(
                updateDTO.getAllowance()
        );

        salary.setBonus(
                updateDTO.getBonus()
        );

        salary.setGrossSalary(
                grossSalary
        );

        salary.setOtherDeduction(
                updateDTO.getOtherDeduction()
        );

        salary.setIncomeTax(
                updateDTO.getIncomeTax()
        );

        salary.setNetSalary(
                netSalary
        );

        salary.setPaymentStatus(
                updateDTO.getPaymentStatus()
        );

        /*
         * 发放状态发生变化时同步处理发放时间
         */
        if (Objects.equals(
                updateDTO.getPaymentStatus(),
                1)) {

            if (salary.getPaymentTime() == null) {

                salary.setPaymentTime(
                        LocalDateTime.now()
                );
            }

        } else {

            salary.setPaymentTime(null);
        }

        salary.setRemark(
                updateDTO.getRemark()
        );

        salary.setUpdateTime(
                LocalDateTime.now()
        );

        empSalaryMapper.updateById(salary);
    }

    /**
     * 删除薪资
     */
    @Override
    @Transactional
    public void delete(Long id) {

        EmpSalary salary =
                empSalaryMapper.selectById(id);

        if (salary == null) {

            throw new BusinessException(
                    "薪资记录不存在"
            );
        }

        empSalaryMapper.deleteById(id);
    }

    /**
     * 检查薪资记录是否重复
     */
    private void checkSalaryExists(
            Long employeeId,
            LocalDate salaryMonth,
            Long excludeId) {

        LambdaQueryWrapper<EmpSalary> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(
                EmpSalary::getEmployeeId,
                employeeId
        );

        wrapper.eq(
                EmpSalary::getSalaryMonth,
                salaryMonth
        );

        if (excludeId != null) {

            wrapper.ne(
                    EmpSalary::getId,
                    excludeId
            );
        }

        Long count =
                empSalaryMapper.selectCount(wrapper);

        if (count > 0) {

            throw new BusinessException(
                    "该员工该月份已有薪资记录"
            );
        }
    }

    /**
     * 计算应发工资
     */
    private BigDecimal calculateGrossSalary(
            BigDecimal baseSalary,
            BigDecimal performanceSalary,
            BigDecimal allowance,
            BigDecimal bonus) {

        return baseSalary
                .add(performanceSalary)
                .add(allowance)
                .add(bonus);
    }

    /**
     * 计算实发工资
     */
    private BigDecimal calculateNetSalary(
            BigDecimal grossSalary,
            BigDecimal otherDeduction,
            BigDecimal incomeTax) {

        BigDecimal netSalary =
                grossSalary
                        .subtract(otherDeduction)
                        .subtract(incomeTax);

        /*
         * 防止实发工资出现负数
         */
        if (netSalary.compareTo(
                BigDecimal.ZERO) < 0) {

            return BigDecimal.ZERO;
        }

        return netSalary;
    }

    /**
     * yyyy-MM 转 LocalDate
     *
     * 统一保存为该月份第一天
     * 例如 2026-08 → 2026-08-01
     */
    private LocalDate parseSalaryMonth(
            String salaryMonth) {

        try {

            return LocalDate.parse(
                    salaryMonth + "-01",
                    DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd"
                    )
            );

        } catch (Exception e) {

            throw new BusinessException(
                    "薪资月份格式不正确"
            );
        }
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
     * 判断用户是否为系统管理员
     */
    private boolean isAdmin(Long userId) {

        return hasRole(
                userId,
                ADMIN_ROLE_CODE
        );
    }

    /**
     * 判断用户是否为主管
     */
    private boolean isManager(Long userId) {

        return hasRole(
                userId,
                MANAGER_ROLE_CODE
        );
    }

    /**
     * 根据角色编码判断用户是否拥有角色
     */
    private boolean hasRole(
            Long userId,
            String roleCode) {

        SysRole role =
                sysRoleMapper.selectOne(
                        new LambdaQueryWrapper<SysRole>()
                                .eq(
                                        SysRole::getRoleCode,
                                        roleCode
                                )
                                .eq(
                                        SysRole::getStatus,
                                        1
                                )
                );

        if (role == null) {
            return false;
        }

        Long count =
                sysUserRoleMapper.selectCount(
                        new LambdaQueryWrapper<SysUserRole>()
                                .eq(
                                        SysUserRole::getUserId,
                                        userId
                                )
                                .eq(
                                        SysUserRole::getRoleId,
                                        role.getId()
                                )
                );

        return count > 0;
    }

    /**
     * Entity 转 VO
     */
    private SalaryVO toVO(
            EmpSalary salary) {

        SalaryVO vo =
                new SalaryVO();

        vo.setId(
                salary.getId()
        );

        vo.setEmployeeId(
                salary.getEmployeeId()
        );

        vo.setSalaryMonth(
                salary.getSalaryMonth()
        );

        vo.setBaseSalary(
                salary.getBaseSalary()
        );

        vo.setPerformanceSalary(
                salary.getPerformanceSalary()
        );

        vo.setAllowance(
                salary.getAllowance()
        );

        vo.setBonus(
                salary.getBonus()
        );

        vo.setGrossSalary(
                salary.getGrossSalary()
        );

        vo.setOtherDeduction(
                salary.getOtherDeduction()
        );

        vo.setIncomeTax(
                salary.getIncomeTax()
        );

        vo.setNetSalary(
                salary.getNetSalary()
        );

        vo.setPaymentStatus(
                salary.getPaymentStatus()
        );

        vo.setPaymentTime(
                salary.getPaymentTime()
        );

        vo.setRemark(
                salary.getRemark()
        );

        vo.setCreateTime(
                salary.getCreateTime()
        );

        vo.setUpdateTime(
                salary.getUpdateTime()
        );

        return vo;
    }

    /**
     * 批量补充员工姓名
     */
    private void fillEmployeeName(
            List<SalaryVO> voList) {

        if (voList.isEmpty()) {
            return;
        }

        List<Long> employeeIds =
                voList.stream()
                        .map(SalaryVO::getEmployeeId)
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

        for (SalaryVO vo : voList) {

            vo.setEmployeeName(
                    nameMap.get(
                            vo.getEmployeeId()
                    )
            );
        }
    }
}