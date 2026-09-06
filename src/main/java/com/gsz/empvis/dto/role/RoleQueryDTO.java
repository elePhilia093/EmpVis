package com.gsz.empvis.dto.role;

import lombok.Data;

@Data
public class RoleQueryDTO {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色状态
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