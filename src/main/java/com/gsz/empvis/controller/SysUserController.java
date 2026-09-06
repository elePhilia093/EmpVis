package com.gsz.empvis.controller;

import com.gsz.empvis.common.PageResult;
import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.user.UserAddDTO;
import com.gsz.empvis.dto.user.UserQueryDTO;
import com.gsz.empvis.dto.user.UserUpdateDTO;
import com.gsz.empvis.service.SysUserService;
import com.gsz.empvis.vo.user.UserVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /**
     * 用户分页查询
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<PageResult<UserVO>> list(
            UserQueryDTO queryDTO) {

        return Result.success(
                sysUserService.page(queryDTO)
        );
    }

    /**
     * 新增用户
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result<Void> add(
            @Valid @RequestBody UserAddDTO addDTO) {

        sysUserService.add(addDTO);

        return Result.success(null);
    }

    /**
     * 修改用户
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:user:update')")
    public Result<Void> update(
            @Valid @RequestBody UserUpdateDTO updateDTO) {

        sysUserService.update(updateDTO);

        return Result.success(null);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<Void> delete(
            @PathVariable Long id) {

        sysUserService.delete(id);

        return Result.success(null);
    }
}