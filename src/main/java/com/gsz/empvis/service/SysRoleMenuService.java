package com.gsz.empvis.service;

import com.gsz.empvis.dto.role.RoleMenuDTO;

import java.util.List;

public interface SysRoleMenuService {

    List<Long> getMenuIds(Long roleId);

    void saveMenus(RoleMenuDTO dto);
}