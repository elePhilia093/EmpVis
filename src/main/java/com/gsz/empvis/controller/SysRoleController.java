package com.gsz.empvis.controller;

import com.gsz.empvis.common.PageResult;
import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.role.RoleAddDTO;
import com.gsz.empvis.dto.role.RoleQueryDTO;
import com.gsz.empvis.dto.role.RoleUpdateDTO;
import com.gsz.empvis.service.SysRoleService;
import com.gsz.empvis.vo.role.RoleVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys/role")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<PageResult<RoleVO>> list(
            RoleQueryDTO queryDTO) {

        return Result.success(
                sysRoleService.page(queryDTO)
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<Void> add(
            @Valid @RequestBody RoleAddDTO addDTO) {

        sysRoleService.add(addDTO);

        return Result.success(null);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:role:update')")
    public Result<Void> update(
            @Valid @RequestBody RoleUpdateDTO updateDTO) {

        sysRoleService.update(updateDTO);

        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> delete(
            @PathVariable Long id) {

        sysRoleService.delete(id);

        return Result.success(null);
    }
}