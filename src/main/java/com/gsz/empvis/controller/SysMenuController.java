package com.gsz.empvis.controller;

import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.menu.MenuAddDTO;
import com.gsz.empvis.dto.menu.MenuQueryDTO;
import com.gsz.empvis.dto.menu.MenuUpdateDTO;
import com.gsz.empvis.service.SysMenuService;
import com.gsz.empvis.vo.menu.MenuVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    public SysMenuController(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    /**
     * 菜单树查询
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<List<MenuVO>> tree(
            MenuQueryDTO queryDTO) {

        return Result.success(
                sysMenuService.tree(queryDTO)
        );
    }

    /**
     * 新增菜单
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    public Result<Void> add(
            @Valid @RequestBody MenuAddDTO addDTO) {

        sysMenuService.add(addDTO);

        return Result.success(null);
    }

    /**
     * 修改菜单
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:menu:update')")
    public Result<Void> update(
            @Valid @RequestBody MenuUpdateDTO updateDTO) {

        sysMenuService.update(updateDTO);

        return Result.success(null);
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public Result<Void> delete(
            @PathVariable Long id) {

        sysMenuService.delete(id);

        return Result.success(null);
    }
}