package com.gsz.empvis.controller;

import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.personal.PasswordUpdateDTO;
import com.gsz.empvis.dto.personal.PersonalInfoUpdateDTO;
import com.gsz.empvis.security.LoginUser;
import com.gsz.empvis.service.PersonalService;
import com.gsz.empvis.vo.personal.PersonalInfoVO;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/personal")
public class PersonalController {

    private final PersonalService personalService;

    public PersonalController(
            PersonalService personalService) {

        this.personalService = personalService;
    }

    /**
     * 获取当前用户个人信息
     */
    @GetMapping("/info")
    public Result<PersonalInfoVO> getInfo(
            Authentication authentication) {

        Long userId =
                getUserId(authentication);

        return Result.success(
                personalService.getInfo(
                        userId
                )
        );
    }

    /**
     * 修改当前用户个人信息
     */
    @PutMapping("/info")
    public Result<Void> updateInfo(
            @Valid @RequestBody
            PersonalInfoUpdateDTO updateDTO,
            Authentication authentication) {

        Long userId =
                getUserId(authentication);

        personalService.updateInfo(
                userId,
                updateDTO
        );

        return Result.success(null);
    }

    /**
     * 修改当前用户密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(
            @Valid @RequestBody
            PasswordUpdateDTO updateDTO,
            Authentication authentication) {

        Long userId =
                getUserId(authentication);

        personalService.updatePassword(
                userId,
                updateDTO
        );

        return Result.success(null);
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getUserId(
            Authentication authentication) {

        LoginUser loginUser =
                (LoginUser) authentication.getPrincipal();

        return loginUser.getUser().getId();
    }
}