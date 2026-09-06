package com.gsz.empvis.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserAddDTO {

    @NotBlank(message = "登录账号不能为空")
    @Size(max = 50, message = "登录账号长度不能超过50个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度应为6-50个字符")
    private String password;

    /**
     * 可为空，表示暂未关联员工
     */
    private Long employeeId;

    @NotNull(message = "账号状态不能为空")
    private Integer status;

    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remark;
}