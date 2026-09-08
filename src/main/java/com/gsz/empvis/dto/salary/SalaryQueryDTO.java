package com.gsz.empvis.dto.salary;

import lombok.Data;

@Data
public class SalaryQueryDTO {

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 薪资月份
     * 格式：yyyy-MM
     */
    private String salaryMonth;

    /**
     * 发放状态
     * 0-未发放
     * 1-已发放
     */
    private Integer paymentStatus;

    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 每页数量
     */
    private Integer size = 10;
}