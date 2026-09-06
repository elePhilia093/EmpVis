package com.gsz.empvis.dto.user;

import lombok.Data;

@Data
public class UserQueryDTO {

    /**
     * 登录账号
     */
    private String username;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 账号状态
     */
    private Integer status;

    /**
     * 当前页
     */
    private long current = 1;

    /**
     * 每页条数
     */
    private long size = 10;
}