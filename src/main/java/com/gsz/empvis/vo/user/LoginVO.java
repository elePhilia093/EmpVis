package com.gsz.empvis.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginVO {

    /**
     * JWT
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录账号
     */
    private String username;
}