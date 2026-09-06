package com.gsz.empvis.dto.dept;

import lombok.Data;

@Data
public class DeptQueryDTO {

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门状态
     */
    private Integer status;
}