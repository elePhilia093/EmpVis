package com.gsz.empvis.service;

import com.gsz.empvis.common.PageResult;
import com.gsz.empvis.dto.role.RoleAddDTO;
import com.gsz.empvis.dto.role.RoleQueryDTO;
import com.gsz.empvis.dto.role.RoleUpdateDTO;
import com.gsz.empvis.vo.role.RoleVO;

public interface SysRoleService {

    PageResult<RoleVO> page(RoleQueryDTO queryDTO);

    void add(RoleAddDTO addDTO);

    void update(RoleUpdateDTO updateDTO);

    void delete(Long id);
}