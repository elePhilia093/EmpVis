package com.gsz.empvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gsz.empvis.dto.menu.MenuAddDTO;
import com.gsz.empvis.dto.menu.MenuQueryDTO;
import com.gsz.empvis.dto.menu.MenuUpdateDTO;
import com.gsz.empvis.entity.SysMenu;
import com.gsz.empvis.entity.SysRoleMenu;
import com.gsz.empvis.exception.BusinessException;
import com.gsz.empvis.mapper.SysMenuMapper;
import com.gsz.empvis.mapper.SysRoleMenuMapper;
import com.gsz.empvis.service.SysMenuService;
import com.gsz.empvis.vo.menu.MenuVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    private final SysRoleMenuMapper sysRoleMenuMapper;

    public SysMenuServiceImpl(
            SysMenuMapper sysMenuMapper,
            SysRoleMenuMapper sysRoleMenuMapper) {

        this.sysMenuMapper = sysMenuMapper;
        this.sysRoleMenuMapper = sysRoleMenuMapper;
    }

    /**
     * 菜单树查询
     */
    @Override
    public List<MenuVO> tree(MenuQueryDTO queryDTO) {

        LambdaQueryWrapper<SysMenu> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.like(
                StringUtils.hasText(queryDTO.getMenuName()),
                SysMenu::getMenuName,
                queryDTO.getMenuName()
        );

        wrapper.eq(
                queryDTO.getMenuType() != null,
                SysMenu::getMenuType,
                queryDTO.getMenuType()
        );

        wrapper.orderByAsc(SysMenu::getSortOrder);

        List<SysMenu> menus =
                sysMenuMapper.selectList(wrapper);

        List<MenuVO> voList = menus.stream()
                .map(this::toVO)
                .toList();

        // 有查询条件：直接平铺返回
        if (StringUtils.hasText(queryDTO.getMenuName())
                || queryDTO.getMenuType() != null) {

            return voList;
        }

        // 无查询条件：构建树
        Map<Long, MenuVO> menuMap = voList.stream()
                .collect(Collectors.toMap(
                        MenuVO::getId,
                        Function.identity()
                ));

        List<MenuVO> roots = new ArrayList<>();

        for (MenuVO menu : voList) {

            if (menu.getParentId() == 0) {

                roots.add(menu);

            } else {

                MenuVO parent =
                        menuMap.get(menu.getParentId());

                if (parent != null) {
                    parent.getChildren().add(menu);
                }
            }
        }

        return roots;
    }

    /**
     * 新增菜单
     */
    @Override
    public void add(MenuAddDTO addDTO) {

        if (addDTO.getParentId() != 0) {

            SysMenu parent =
                    sysMenuMapper.selectById(
                            addDTO.getParentId()
                    );

            if (parent == null) {
                throw new BusinessException("父级菜单不存在");
            }
        }

        SysMenu menu = new SysMenu();

        menu.setParentId(addDTO.getParentId());
        menu.setMenuName(addDTO.getMenuName());
        menu.setMenuType(addDTO.getMenuType());
        menu.setPath(addDTO.getPath());
        menu.setComponent(addDTO.getComponent());
        menu.setPermission(addDTO.getPermission());
        menu.setIcon(addDTO.getIcon());

        menu.setSortOrder(
                addDTO.getSortOrder() == null
                        ? 0
                        : addDTO.getSortOrder()
        );

        sysMenuMapper.insert(menu);
    }

    /**
     * 修改菜单
     */
    @Override
    public void update(MenuUpdateDTO updateDTO) {

        SysMenu menu =
                sysMenuMapper.selectById(updateDTO.getId());

        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }

        // 不能把自己设置为自己的父级
        if (updateDTO.getId()
                .equals(updateDTO.getParentId())) {

            throw new BusinessException(
                    "父级菜单不能设置为当前菜单"
            );
        }

        if (updateDTO.getParentId() != 0) {

            SysMenu parent =
                    sysMenuMapper.selectById(
                            updateDTO.getParentId()
                    );

            if (parent == null) {
                throw new BusinessException("父级菜单不存在");
            }
        }

        menu.setParentId(updateDTO.getParentId());
        menu.setMenuName(updateDTO.getMenuName());
        menu.setMenuType(updateDTO.getMenuType());
        menu.setPath(updateDTO.getPath());
        menu.setComponent(updateDTO.getComponent());
        menu.setPermission(updateDTO.getPermission());
        menu.setIcon(updateDTO.getIcon());

        menu.setSortOrder(
                updateDTO.getSortOrder() == null
                        ? 0
                        : updateDTO.getSortOrder()
        );

        sysMenuMapper.updateById(menu);
    }

    /**
     * 删除菜单
     */
    @Override
    public void delete(Long id) {

        SysMenu menu =
                sysMenuMapper.selectById(id);

        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }

        // 判断是否存在子菜单
        LambdaQueryWrapper<SysMenu> childWrapper =
                new LambdaQueryWrapper<>();

        childWrapper.eq(
                SysMenu::getParentId,
                id
        );

        Long childCount =
                sysMenuMapper.selectCount(childWrapper);

        if (childCount > 0) {
            throw new BusinessException(
                    "该菜单存在子菜单，不能直接删除"
            );
        }

        // 判断是否已经被角色使用
        LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper =
                new LambdaQueryWrapper<>();

        roleMenuWrapper.eq(
                SysRoleMenu::getMenuId,
                id
        );

        Long roleMenuCount =
                sysRoleMenuMapper.selectCount(
                        roleMenuWrapper
                );

        if (roleMenuCount > 0) {
            throw new BusinessException(
                    "该菜单已分配给角色，不能直接删除"
            );
        }

        sysMenuMapper.deleteById(id);
    }

    private MenuVO toVO(SysMenu menu) {

        MenuVO vo = new MenuVO();

        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setMenuName(menu.getMenuName());
        vo.setMenuType(menu.getMenuType());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setPermission(menu.getPermission());
        vo.setIcon(menu.getIcon());
        vo.setSortOrder(menu.getSortOrder());

        return vo;
    }
}