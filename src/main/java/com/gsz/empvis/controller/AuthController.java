package com.gsz.empvis.controller;

import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.user.LoginDTO;
import com.gsz.empvis.entity.SysUser;
import com.gsz.empvis.security.LoginUser;
import com.gsz.empvis.service.SysUserService;
import com.gsz.empvis.vo.user.LoginVO;
import com.gsz.empvis.vo.user.UserInfoVO;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SysUserService sysUserService;

    public AuthController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @PostMapping("/login")
    public Result<LoginVO> login(
            @Valid @RequestBody LoginDTO loginDTO) {

        LoginVO loginVO = sysUserService.login(
                loginDTO.getUsername(),
                loginDTO.getPassword()
        );

        return Result.success(loginVO);
    }

    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        LoginUser loginUser =
                (LoginUser) authentication.getPrincipal();

        SysUser user = loginUser.getUser();

        UserInfoVO userInfoVO =
                sysUserService.getUserInfo(user.getId());

        return Result.success(userInfoVO);
    }
}