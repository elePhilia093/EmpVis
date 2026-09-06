package com.gsz.empvis.vo.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;

    private String username;

    private Long employeeId;

    private Integer status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}