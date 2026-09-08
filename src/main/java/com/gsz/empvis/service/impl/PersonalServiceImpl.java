package com.gsz.empvis.service.impl;

import com.gsz.empvis.dto.personal.PasswordUpdateDTO;
import com.gsz.empvis.dto.personal.PersonalInfoUpdateDTO;
import com.gsz.empvis.entity.EmpEmployee;
import com.gsz.empvis.entity.SysDept;
import com.gsz.empvis.entity.SysUser;
import com.gsz.empvis.exception.BusinessException;
import com.gsz.empvis.mapper.EmpEmployeeMapper;
import com.gsz.empvis.mapper.SysDeptMapper;
import com.gsz.empvis.mapper.SysUserMapper;
import com.gsz.empvis.service.PersonalService;
import com.gsz.empvis.vo.personal.PersonalInfoVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PersonalServiceImpl
        implements PersonalService {

    private final SysUserMapper sysUserMapper;

    private final EmpEmployeeMapper empEmployeeMapper;

    private final SysDeptMapper sysDeptMapper;

    private final PasswordEncoder passwordEncoder;

    public PersonalServiceImpl(
            SysUserMapper sysUserMapper,
            EmpEmployeeMapper empEmployeeMapper,
            SysDeptMapper sysDeptMapper,
            PasswordEncoder passwordEncoder) {

        this.sysUserMapper = sysUserMapper;
        this.empEmployeeMapper = empEmployeeMapper;
        this.sysDeptMapper = sysDeptMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 获取当前用户个人信息
     */
    @Override
    public PersonalInfoVO getInfo(Long userId) {

        SysUser user =
                sysUserMapper.selectById(userId);

        if (user == null) {
            throw new BusinessException(
                    "用户不存在"
            );
        }

        PersonalInfoVO vo =
                new PersonalInfoVO();

        vo.setUsername(
                user.getUsername()
        );

        vo.setStatus(
                user.getStatus()
        );

        /*
         * 用户未绑定员工
         *
         * 例如系统账号可能只是测试账号，
         * 没有关联 employee_id。
         */
        if (user.getEmployeeId() == null) {
            return vo;
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

        vo.setEmployeeNo(
                employee.getEmployeeNo()
        );

        vo.setEmployeeName(
                employee.getEmployeeName()
        );

        vo.setGender(
                employee.getGender()
        );

        if (employee.getBirthDate() != null) {
            vo.setBirthDate(
                    employee.getBirthDate().toString()
            );
        }

        vo.setPositionName(
                employee.getPositionName()
        );

        vo.setPhone(
                employee.getPhone()
        );

        vo.setEmail(
                employee.getEmail()
        );

        /*
         * 查询部门名称
         */
        if (employee.getDeptId() != null) {

            SysDept dept =
                    sysDeptMapper.selectById(
                            employee.getDeptId()
                    );

            if (dept != null) {

                vo.setDeptName(
                        dept.getDeptName()
                );
            }
        }

        return vo;
    }

    /**
     * 修改当前用户个人信息
     */
    @Override
    @Transactional
    public void updateInfo(
            Long userId,
            PersonalInfoUpdateDTO updateDTO) {

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

        /*
         * 只修改本人允许维护的字段
         */
        employee.setPhone(
                StringUtils.hasText(
                        updateDTO.getPhone()
                )
                        ? updateDTO.getPhone()
                        : null
        );

        employee.setEmail(
                StringUtils.hasText(
                        updateDTO.getEmail()
                )
                        ? updateDTO.getEmail()
                        : null
        );

        empEmployeeMapper.updateById(
                employee
        );
    }

    /**
     * 修改当前用户密码
     */
    @Override
    @Transactional
    public void updatePassword(
            Long userId,
            PasswordUpdateDTO updateDTO) {

        SysUser user =
                sysUserMapper.selectById(userId);

        if (user == null) {
            throw new BusinessException(
                    "用户不存在"
            );
        }

        /*
         * 校验原密码
         */
        boolean matches =
                passwordEncoder.matches(
                        updateDTO.getOldPassword(),
                        user.getPassword()
                );

        if (!matches) {
            throw new BusinessException(
                    "原密码错误"
            );
        }

        /*
         * 新密码不能与旧密码相同
         */
        if (passwordEncoder.matches(
                updateDTO.getNewPassword(),
                user.getPassword())) {

            throw new BusinessException(
                    "新密码不能与原密码相同"
            );
        }

        /*
         * BCrypt 加密新密码
         */
        user.setPassword(
                passwordEncoder.encode(
                        updateDTO.getNewPassword()
                )
        );

        sysUserMapper.updateById(user);
    }
}