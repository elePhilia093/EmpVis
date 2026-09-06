package com.gsz.empvis.vo.role;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleVO {

    private Long id;

    private String roleName;

    private String roleCode;

    private Integer status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}