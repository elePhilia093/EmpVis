package com.gsz.empvis.dto.personal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordUpdateDTO {

    /**
     * 原密码
     */
    @NotBlank(message = "请输入原密码")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "请输入新密码")
    @Size(
            min = 6,
            max = 20,
            message = "密码长度为6-20位"
    )
    private String newPassword;
}