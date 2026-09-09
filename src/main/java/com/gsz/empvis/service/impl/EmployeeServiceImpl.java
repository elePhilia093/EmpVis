package com.gsz.empvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gsz.empvis.dto.employee.EmployeeAddDTO;
import com.gsz.empvis.dto.employee.EmployeeQueryDTO;
import com.gsz.empvis.dto.employee.EmployeeUpdateDTO;
import com.gsz.empvis.entity.EmpEmployee;
import com.gsz.empvis.entity.SysDept;
import com.gsz.empvis.entity.SysUser;
import com.gsz.empvis.exception.BusinessException;
import com.gsz.empvis.mapper.EmployeeMapper;
import com.gsz.empvis.mapper.SysDeptMapper;
import com.gsz.empvis.mapper.SysUserMapper;
import com.gsz.empvis.service.EmployeeService;
import com.gsz.empvis.vo.employee.EmployeeVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysUserMapper sysUserMapper;

    public EmployeeServiceImpl(
            EmployeeMapper employeeMapper,
            SysDeptMapper sysDeptMapper,
            SysUserMapper sysUserMapper
            ) {

        this.employeeMapper = employeeMapper;
        this.sysDeptMapper = sysDeptMapper;
        this.sysUserMapper = sysUserMapper;
    }

    /**
     * 分页查询员工
     */
    @Override
    public IPage<EmployeeVO> page(EmployeeQueryDTO queryDTO) {

        Page<EmpEmployee> page =
                new Page<>(
                        queryDTO.getCurrent(),
                        queryDTO.getSize()
                );

        LambdaQueryWrapper<EmpEmployee> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.like(
                StringUtils.hasText(queryDTO.getEmployeeNo()),
                EmpEmployee::getEmployeeNo,
                queryDTO.getEmployeeNo()
        );

        wrapper.like(
                StringUtils.hasText(queryDTO.getEmployeeName()),
                EmpEmployee::getEmployeeName,
                queryDTO.getEmployeeName()
        );

        wrapper.eq(
                queryDTO.getDeptId() != null,
                EmpEmployee::getDeptId,
                queryDTO.getDeptId()
        );

        wrapper.eq(
                queryDTO.getGender() != null,
                EmpEmployee::getGender,
                queryDTO.getGender()
        );

        wrapper.orderByAsc(EmpEmployee::getId);

        IPage<EmpEmployee> employeePage =
                employeeMapper.selectPage(page, wrapper);

        List<EmpEmployee> employees =
                employeePage.getRecords();

        List<EmployeeVO> voList =
                employees.stream()
                        .map(this::toVO)
                        .toList();

        // 查询员工所属部门名称
        fillDeptName(voList);

        IPage<EmployeeVO> result =
                new Page<>(
                        employeePage.getCurrent(),
                        employeePage.getSize(),
                        employeePage.getTotal()
                );

        result.setRecords(voList);

        return result;
    }

    /**
     * 新增员工
     */
    @Override
    public void add(EmployeeAddDTO addDTO) {

        // 检查员工编号是否重复
        LambdaQueryWrapper<EmpEmployee> noWrapper =
                new LambdaQueryWrapper<>();

        noWrapper.eq(
                EmpEmployee::getEmployeeNo,
                addDTO.getEmployeeNo()
        );

        Long count =
                employeeMapper.selectCount(noWrapper);

        if (count > 0) {
            throw new BusinessException("员工编号已存在");
        }

        // 检查部门是否存在
        checkDeptExists(addDTO.getDeptId());

        EmpEmployee employee = new EmpEmployee();

        employee.setEmployeeNo(addDTO.getEmployeeNo());
        employee.setEmployeeName(addDTO.getEmployeeName());
        employee.setGender(addDTO.getGender());
        employee.setBirthDate(addDTO.getBirthDate());
        employee.setPhone(addDTO.getPhone());
        employee.setEmail(addDTO.getEmail());
        employee.setDeptId(addDTO.getDeptId());
        employee.setPositionName(addDTO.getPositionName());

        employeeMapper.insert(employee);
    }

    /**
     * 修改员工
     */
    @Override
    public void update(EmployeeUpdateDTO updateDTO) {

        EmpEmployee employee =
                employeeMapper.selectById(updateDTO.getId());

        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        // 检查员工编号是否与其他员工重复
        LambdaQueryWrapper<EmpEmployee> noWrapper =
                new LambdaQueryWrapper<>();

        noWrapper.eq(
                EmpEmployee::getEmployeeNo,
                updateDTO.getEmployeeNo()
        );

        noWrapper.ne(
                EmpEmployee::getId,
                updateDTO.getId()
        );

        Long count =
                employeeMapper.selectCount(noWrapper);

        if (count > 0) {
            throw new BusinessException("员工编号已存在");
        }

        // 检查部门是否存在
        checkDeptExists(updateDTO.getDeptId());

        employee.setEmployeeNo(updateDTO.getEmployeeNo());
        employee.setEmployeeName(updateDTO.getEmployeeName());
        employee.setGender(updateDTO.getGender());
        employee.setBirthDate(updateDTO.getBirthDate());
        employee.setPhone(updateDTO.getPhone());
        employee.setEmail(updateDTO.getEmail());
        employee.setDeptId(updateDTO.getDeptId());
        employee.setPositionName(updateDTO.getPositionName());

        employeeMapper.updateById(employee);
    }

    /**
     * 删除员工
     */
    @Override
    public void delete(Long id) {

        EmpEmployee employee =
                employeeMapper.selectById(id);

        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        LambdaQueryWrapper<SysUser> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(
                SysUser::getEmployeeId,
                id
        );

        Long count =
                sysUserMapper.selectCount(wrapper);

        if (count > 0) {
            throw new BusinessException(
                    "该员工已绑定用户账号，不能删除"
            );
        }

        employeeMapper.deleteById(id);
    }

    /**
     * 检查部门是否存在
     */
    private void checkDeptExists(Long deptId) {

        SysDept dept =
                sysDeptMapper.selectById(deptId);

        if (dept == null) {
            throw new BusinessException("所属部门不存在");
        }
    }

    /**
     * Entity 转 VO
     */
    private EmployeeVO toVO(EmpEmployee employee) {

        EmployeeVO vo = new EmployeeVO();

        vo.setId(employee.getId());
        vo.setEmployeeNo(employee.getEmployeeNo());
        vo.setEmployeeName(employee.getEmployeeName());
        vo.setGender(employee.getGender());
        vo.setBirthDate(employee.getBirthDate());
        vo.setPhone(employee.getPhone());
        vo.setEmail(employee.getEmail());
        vo.setDeptId(employee.getDeptId());
        vo.setPositionName(employee.getPositionName());

        return vo;
    }

    /**
     * 填充部门名称
     */
    private void fillDeptName(List<EmployeeVO> voList) {

        if (voList.isEmpty()) {
            return;
        }

        List<Long> deptIds =
                voList.stream()
                        .map(EmployeeVO::getDeptId)
                        .distinct()
                        .toList();

        if (deptIds.isEmpty()) {
            return;
        }

        List<SysDept> deptList =
                sysDeptMapper.selectBatchIds(deptIds);

        Map<Long, String> deptNameMap =
                deptList.stream()
                        .collect(Collectors.toMap(
                                SysDept::getId,
                                SysDept::getDeptName
                        ));

        for (EmployeeVO vo : voList) {
            vo.setDeptName(
                    deptNameMap.get(vo.getDeptId())
            );
        }
    }
}