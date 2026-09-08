package com.gsz.empvis.vo.salary;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SalaryVO {

    /**
     * 薪资记录ID
     */
    private Long id;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 薪资月份
     */
    private LocalDate salaryMonth;

    /**
     * 基本工资
     */
    private BigDecimal baseSalary;

    /**
     * 绩效工资
     */
    private BigDecimal performanceSalary;

    /**
     * 津贴补助
     */
    private BigDecimal allowance;

    /**
     * 奖金
     */
    private BigDecimal bonus;

    /**
     * 应发工资
     */
    private BigDecimal grossSalary;

    /**
     * 其他扣除
     */
    private BigDecimal otherDeduction;

    /**
     * 个人所得税
     */
    private BigDecimal incomeTax;

    /**
     * 实发工资
     */
    private BigDecimal netSalary;

    /**
     * 发放状态
     */
    private Integer paymentStatus;

    /**
     * 实际发放时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}