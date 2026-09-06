package com.gsz.empvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gsz.empvis.dto.user.UserRoleDTO;
import com.gsz.empvis.entity.SysRole;
import com.gsz.empvis.entity.SysUser;
import com.gsz.empvis.entity.SysUserRole;
import com.gsz.empvis.exception.BusinessException;
import com.gsz.empvis.mapper.SysRoleMapper;
import com.gsz.empvis.mapper.SysUserMapper;
import com.gsz.empvis.mapper.SysUserRoleMapper;
import com.gsz.empvis.service.SysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class SysUserRoleServiceImpl implements SysUserRoleService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;

    public SysUserRoleServiceImpl(
            SysUserRoleMapper sysUserRoleMapper,
            SysUserMapper sysUserMapper,
            SysRoleMapper sysRoleMapper) {

        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public List<Long> getRoleIds(Long userId) {

        LambdaQueryWrapper<SysUserRole> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.select(SysUserRole::getRoleId)
                .eq(SysUserRole::getUserId, userId);

        return sysUserRoleMapper.selectList(wrapper)
                .stream()
                .map(SysUserRole::getRoleId)
                .toList();
    }

    @Override
    @Transactional
    public void saveRoles(UserRoleDTO dto) {

        SysUser user =
                sysUserMapper.selectById(dto.getUserId());

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 先删除原有角色
        LambdaQueryWrapper<SysUserRole> deleteWrapper =
                new LambdaQueryWrapper<>();

        deleteWrapper.eq(
                SysUserRole::getUserId,
                dto.getUserId()
        );

        sysUserRoleMapper.delete(deleteWrapper);

        // 没有角色，表示清空授权
        if (dto.getRoleIds() == null
                || dto.getRoleIds().isEmpty()) {
            return;
        }

        List<Long> roleIds = dto.getRoleIds()
                .stream()
                .distinct()
                .toList();

        // 校验角色是否全部存在
        for (Long roleId : roleIds) {

            SysRole role =
                    sysRoleMapper.selectById(roleId);

            if (role == null) {
                throw new BusinessException(
                        "角色不存在：" + roleId
                );
            }
        }

        for (Long roleId : roleIds) {

            SysUserRole userRole = new SysUserRole();

            userRole.setUserId(dto.getUserId());
            userRole.setRoleId(roleId);

            sysUserRoleMapper.insert(userRole);
        }
    }
}