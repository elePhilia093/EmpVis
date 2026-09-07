package com.gsz.empvis.dto.employee;

import lombok.Data;

@Data
public class EmployeeQueryDTO {

    /**
     * 员工编号
     */
    private String employeeNo;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 所属部门ID
     */
    private Long deptId;

    /**
     * 性别
     * 1-男
     * 2-女
     */
    private Integer gender;

    /**
     * 当前页
     */
    private Long current = 1L;

    /**
     * 每页条数
     */
    private Long size = 10L;
}