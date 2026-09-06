package com.gsz.empvis.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Size(max = 50, message = "登录账号长度不能超过50个字符")
    private String username;

    private String password;

    private Long employeeId;

    @NotNull(message = "账号状态不能为空")
    private Integer status;

    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remark;
}