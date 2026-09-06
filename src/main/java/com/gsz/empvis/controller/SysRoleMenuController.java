package com.gsz.empvis.controller;

import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.role.RoleMenuDTO;
import com.gsz.empvis.service.SysRoleMenuService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/role/menu")
public class SysRoleMenuController {

    private final SysRoleMenuService sysRoleMenuService;

    public SysRoleMenuController(
            SysRoleMenuService sysRoleMenuService) {

        this.sysRoleMenuService = sysRoleMenuService;
    }

    /**
     * 查询角色拥有的菜单
     */
    @GetMapping("/{roleId}")
    @PreAuthorize("hasAuthority('system:role:menu')")
    public Result<List<Long>> getMenuIds(
            @PathVariable Long roleId) {

        return Result.success(
                sysRoleMenuService.getMenuIds(roleId)
        );
    }

    /**
     * 保存角色菜单权限
     */
    @PutMapping("/save")
    @PreAuthorize("hasAuthority('system:role:menu')")
    public Result<Void> saveMenus(
            @Valid @RequestBody RoleMenuDTO dto) {

        sysRoleMenuService.saveMenus(dto);

        return Result.success(null);
    }
}