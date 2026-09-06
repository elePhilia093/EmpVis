package com.gsz.empvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gsz.empvis.dto.role.RoleMenuDTO;
import com.gsz.empvis.entity.SysMenu;
import com.gsz.empvis.entity.SysRole;
import com.gsz.empvis.entity.SysRoleMenu;
import com.gsz.empvis.mapper.SysMenuMapper;
import com.gsz.empvis.mapper.SysRoleMapper;
import com.gsz.empvis.mapper.SysRoleMenuMapper;
import com.gsz.empvis.service.SysRoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;

    public SysRoleMenuServiceImpl(
            SysRoleMenuMapper sysRoleMenuMapper,
            SysRoleMapper sysRoleMapper,
            SysMenuMapper sysMenuMapper) {

        this.sysRoleMenuMapper = sysRoleMenuMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysMenuMapper = sysMenuMapper;
    }

    @Override
    public List<Long> getMenuIds(Long roleId) {

        LambdaQueryWrapper<SysRoleMenu> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.select(SysRoleMenu::getMenuId)
                .eq(SysRoleMenu::getRoleId, roleId);

        return sysRoleMenuMapper.selectList(wrapper)
                .stream()
                .map(SysRoleMenu::getMenuId)
                .toList();
    }

    @Override
    @Transactional
    public void saveMenus(RoleMenuDTO dto) {

        SysRole role =
                sysRoleMapper.selectById(dto.getRoleId());

        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        // 删除原有菜单权限
        LambdaQueryWrapper<SysRoleMenu> deleteWrapper =
                new LambdaQueryWrapper<>();

        deleteWrapper.eq(
                SysRoleMenu::getRoleId,
                dto.getRoleId()
        );

        sysRoleMenuMapper.delete(deleteWrapper);

        // 没有菜单，表示清空权限
        if (dto.getMenuIds() == null
                || dto.getMenuIds().isEmpty()) {
            return;
        }

        List<Long> menuIds = dto.getMenuIds()
                .stream()
                .distinct()
                .toList();

        // 校验菜单是否存在
        for (Long menuId : menuIds) {

            SysMenu menu =
                    sysMenuMapper.selectById(menuId);

            if (menu == null) {
                throw new RuntimeException(
                        "菜单不存在：" + menuId
                );
            }
        }

        for (Long menuId : menuIds) {

            SysRoleMenu roleMenu =
                    new SysRoleMenu();

            roleMenu.setRoleId(dto.getRoleId());
            roleMenu.setMenuId(menuId);

            sysRoleMenuMapper.insert(roleMenu);
        }
    }
}