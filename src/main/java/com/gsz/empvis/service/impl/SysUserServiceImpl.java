package com.gsz.empvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gsz.empvis.common.PageResult;
import com.gsz.empvis.dto.user.UserAddDTO;
import com.gsz.empvis.dto.user.UserQueryDTO;
import com.gsz.empvis.dto.user.UserUpdateDTO;
import com.gsz.empvis.entity.*;
import com.gsz.empvis.exception.BusinessException;
import com.gsz.empvis.mapper.*;
import com.gsz.empvis.service.SysUserService;
import com.gsz.empvis.utils.JwtUtil;
import com.gsz.empvis.vo.user.LoginVO;
import com.gsz.empvis.vo.user.UserInfoVO;
import com.gsz.empvis.vo.user.UserVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final SysUserRoleMapper sysUserRoleMapper;

    private final SysRoleMapper sysRoleMapper;

    private final SysRoleMenuMapper sysRoleMenuMapper;

    private final SysMenuMapper sysMenuMapper;

    private final EmpEmployeeMapper empEmployeeMapper;

    public SysUserServiceImpl(
            SysUserMapper sysUserMapper,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            SysUserRoleMapper sysUserRoleMapper,
            SysRoleMapper sysRoleMapper,
            SysRoleMenuMapper sysRoleMenuMapper,
            EmpEmployeeMapper empEmployeeMapper,
            SysMenuMapper sysMenuMapper) {

        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysRoleMenuMapper = sysRoleMenuMapper;
        this.sysMenuMapper = sysMenuMapper;
        this.empEmployeeMapper = empEmployeeMapper;
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {

        SysUser user = sysUserMapper.selectById(userId);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 查询用户角色关系
        LambdaQueryWrapper<SysUserRole> userRoleWrapper =
                new LambdaQueryWrapper<>();

        userRoleWrapper.eq(
                SysUserRole::getUserId,
                userId
        );

        List<SysUserRole> userRoles =
                sysUserRoleMapper.selectList(userRoleWrapper);

        List<String> roleCodes = new ArrayList<>();

        if (!userRoles.isEmpty()) {

            List<Long> roleIds = userRoles.stream()
                    .map(SysUserRole::getRoleId)
                    .distinct()
                    .toList();

            List<SysRole> roles =
                    sysRoleMapper.selectBatchIds(roleIds);

            roleCodes = roles.stream()
                    .filter(role ->
                            role.getStatus() != null
                                    && role.getStatus() == 1
                    )
                    .map(SysRole::getRoleCode)
                    .toList();
        }

        return new UserInfoVO(
                user.getId(),
                user.getUsername(),
                roleCodes
        );
    }

    @Override
    public SysUser getByUsername(String username) {

        LambdaQueryWrapper<SysUser> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(SysUser::getUsername, username);

        return sysUserMapper.selectOne(wrapper);
    }

    @Override
    public LoginVO login(String username, String password) {

        SysUser user = getByUsername(username);

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已停用");
        }

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getUsername()
        );

        return new LoginVO(
                token,
                user.getId(),
                user.getUsername()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthoritiesByUserId(
            Long userId) {

        List<GrantedAuthority> authorities = new ArrayList<>();

        LambdaQueryWrapper<SysUserRole> userRoleWrapper =
                new LambdaQueryWrapper<>();

        userRoleWrapper.eq(
                SysUserRole::getUserId,
                userId
        );

        List<SysUserRole> userRoles =
                sysUserRoleMapper.selectList(userRoleWrapper);

        if (userRoles.isEmpty()) {
            return authorities;
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .toList();

        List<SysRole> roles =
                sysRoleMapper.selectBatchIds(roleIds);

        for (SysRole role : roles) {

            if (role.getStatus() != null
                    && role.getStatus() == 1) {

                authorities.add(
                        new SimpleGrantedAuthority(
                                "ROLE_" + role.getRoleCode()
                        )
                );
            }
        }

        LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper =
                new LambdaQueryWrapper<>();

        roleMenuWrapper.in(
                SysRoleMenu::getRoleId,
                roleIds
        );

        List<SysRoleMenu> roleMenus =
                sysRoleMenuMapper.selectList(roleMenuWrapper);

        if (roleMenus.isEmpty()) {
            return authorities;
        }

        List<Long> menuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .distinct()
                .toList();

        List<SysMenu> menus =
                sysMenuMapper.selectBatchIds(menuIds);

        for (SysMenu menu : menus) {

            String permission = menu.getPermission();

            if (StringUtils.hasText(permission)) {

                authorities.add(
                        new SimpleGrantedAuthority(permission)
                );
            }
        }

        return authorities;
    }

    /**
     * 用户分页查询
     */
    @Override
    public PageResult<UserVO> page(UserQueryDTO queryDTO) {

        Page<SysUser> page =
                new Page<>(
                        queryDTO.getCurrent(),
                        queryDTO.getSize()
                );

        LambdaQueryWrapper<SysUser> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.like(
                StringUtils.hasText(queryDTO.getUsername()),
                SysUser::getUsername,
                queryDTO.getUsername()
        );

        wrapper.eq(
                queryDTO.getEmployeeId() != null,
                SysUser::getEmployeeId,
                queryDTO.getEmployeeId()
        );

        wrapper.eq(
                queryDTO.getStatus() != null,
                SysUser::getStatus,
                queryDTO.getStatus()
        );

        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result =
                sysUserMapper.selectPage(page, wrapper);

        List<UserVO> records = result.getRecords()
                .stream()
                .map(this::toVO)
                .toList();
        // 补充员工姓名
        List<Long> employeeIds = records.stream()
                .map(UserVO::getEmployeeId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (!employeeIds.isEmpty()) {

            List<EmpEmployee> employees =
                    empEmployeeMapper.selectBatchIds(employeeIds);

            Map<Long, String> employeeNameMap =
                    employees.stream()
                            .collect(Collectors.toMap(
                                    EmpEmployee::getId,
                                    EmpEmployee::getEmployeeName
                            ));

            records.forEach(vo ->
                    vo.setEmployeeName(
                            employeeNameMap.get(vo.getEmployeeId())
                    )
            );
        }

        return new PageResult<>(
                records,
                result.getTotal(),
                result.getCurrent(),
                result.getSize(),
                result.getPages()
        );
    }

    /**
     * 新增用户
     */
    @Override
    public void add(UserAddDTO addDTO) {

        SysUser exist =
                getByUsername(addDTO.getUsername());

        if (exist != null) {
            throw new BusinessException("登录账号已存在");
        }

        if (addDTO.getEmployeeId() != null) {

            LambdaQueryWrapper<SysUser> wrapper =
                    new LambdaQueryWrapper<>();

            wrapper.eq(
                    SysUser::getEmployeeId,
                    addDTO.getEmployeeId()
            );

            Long count =
                    sysUserMapper.selectCount(wrapper);

            if (count > 0) {
                throw new BusinessException("该员工已经绑定用户账号");
            }
        }

        SysUser user = new SysUser();

        user.setUsername(addDTO.getUsername());

        user.setPassword(
                passwordEncoder.encode(
                        addDTO.getPassword()
                )
        );

        user.setEmployeeId(addDTO.getEmployeeId());

        user.setStatus(addDTO.getStatus());

        user.setRemark(addDTO.getRemark());

        user.setDeleted(0);

        sysUserMapper.insert(user);
    }

    /**
     * 修改用户
     */
    @Override
    public void update(UserUpdateDTO updateDTO) {

        SysUser user =
                sysUserMapper.selectById(updateDTO.getId());

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (StringUtils.hasText(updateDTO.getUsername())) {

            LambdaQueryWrapper<SysUser> wrapper =
                    new LambdaQueryWrapper<>();

            wrapper.eq(
                    SysUser::getUsername,
                    updateDTO.getUsername()
            );

            wrapper.ne(
                    SysUser::getId,
                    updateDTO.getId()
            );

            Long count =
                    sysUserMapper.selectCount(wrapper);

            if (count > 0) {
                throw new BusinessException("登录账号已存在");
            }

            user.setUsername(updateDTO.getUsername());
        }

        if (updateDTO.getEmployeeId() != null
                && !updateDTO.getEmployeeId()
                .equals(user.getEmployeeId())) {

            LambdaQueryWrapper<SysUser> wrapper =
                    new LambdaQueryWrapper<>();

            wrapper.eq(
                    SysUser::getEmployeeId,
                    updateDTO.getEmployeeId()
            );

            wrapper.ne(
                    SysUser::getId,
                    updateDTO.getId()
            );

            Long count =
                    sysUserMapper.selectCount(wrapper);

            if (count > 0) {
                throw new BusinessException("该员工已经绑定其他用户账号");
            }

            user.setEmployeeId(updateDTO.getEmployeeId());
        }

        if (updateDTO.getEmployeeId() == null) {
            user.setEmployeeId(null);
        }

        if (StringUtils.hasText(updateDTO.getPassword())) {

            user.setPassword(
                    passwordEncoder.encode(
                            updateDTO.getPassword()
                    )
            );
        }

        user.setStatus(updateDTO.getStatus());

        user.setRemark(updateDTO.getRemark());

        sysUserMapper.updateById(user);
    }

    /**
     * 删除用户
     */
    @Override
    public void delete(Long id) {

        SysUser user =
                sysUserMapper.selectById(id);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        sysUserMapper.deleteById(id);
    }

    /**
     * Entity 转 VO
     */
    private UserVO toVO(SysUser user) {

        UserVO vo = new UserVO();

        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmployeeId(user.getEmployeeId());
        vo.setStatus(user.getStatus());
        vo.setRemark(user.getRemark());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());

        return vo;
    }
}