package com.gsz.empvis.service;

import com.gsz.empvis.dto.user.UserRoleDTO;

import java.util.List;

public interface SysUserRoleService {

    List<Long> getRoleIds(Long userId);

    void saveRoles(UserRoleDTO dto);
}