package com.gsz.empvis.dto.personal;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PersonalInfoUpdateDTO {

    /**
     * 联系电话
     */
    @Pattern(
            regexp = "^$|^1[3-9]\\d{9}$",
            message = "请输入正确的手机号码"
    )
    private String phone;

    /**
     * 电子邮箱
     */
    @Pattern(
            regexp = "^$|^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "请输入正确的邮箱地址"
    )
    private String email;
}