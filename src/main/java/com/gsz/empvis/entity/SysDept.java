package com.gsz.empvis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_dept")
public class SysDept {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String deptCode;

    private String deptName;

    private Long leaderId;

    private Integer sortOrder;

    private Integer status;

    private String remark;

    private LocalDateTime createTime;
}