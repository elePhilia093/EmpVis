package com.gsz.empvis.controller;

import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.user.UserRoleDTO;
import com.gsz.empvis.service.SysUserRoleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/user/role")
public class SysUserRoleController {

    private final SysUserRoleService sysUserRoleService;

    public SysUserRoleController(
            SysUserRoleService sysUserRoleService) {
        this.sysUserRoleService = sysUserRoleService;
    }

    /**
     * 查询用户角色
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('system:user:role')")
    public Result<List<Long>> getRoleIds(
            @PathVariable Long userId) {

        return Result.success(
                sysUserRoleService.getRoleIds(userId)
        );
    }

    /**
     * 保存用户角色
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:user:role')")
    public Result<Void> saveRoles(
            @Valid @RequestBody UserRoleDTO dto) {

        sysUserRoleService.saveRoles(dto);

        return Result.success(null);
    }
}