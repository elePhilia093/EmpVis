package com.gsz.empvis.dto.salary;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalaryUpdateDTO {

    /**
     * 薪资记录ID
     */
    @NotNull(message = "薪资记录ID不能为空")
    private Long id;

    /**
     * 员工ID
     */
    @NotNull(message = "请选择员工")
    private Long employeeId;

    /**
     * 薪资月份
     */
    @NotBlank(message = "请选择薪资月份")
    private String salaryMonth;

    /**
     * 基本工资
     */
    @NotNull(message = "请输入基本工资")
    @DecimalMin(value = "0.00", message = "基本工资不能为负数")
    private BigDecimal baseSalary;

    /**
     * 绩效工资
     */
    @NotNull(message = "请输入绩效工资")
    @DecimalMin(value = "0.00", message = "绩效工资不能为负数")
    private BigDecimal performanceSalary;

    /**
     * 津贴补助
     */
    @NotNull(message = "请输入津贴补助")
    @DecimalMin(value = "0.00", message = "津贴补助不能为负数")
    private BigDecimal allowance;

    /**
     * 奖金
     */
    @NotNull(message = "请输入奖金")
    @DecimalMin(value = "0.00", message = "奖金不能为负数")
    private BigDecimal bonus;

    /**
     * 其他扣除
     */
    @NotNull(message = "请输入其他扣除")
    @DecimalMin(value = "0.00", message = "其他扣除不能为负数")
    private BigDecimal otherDeduction;

    /**
     * 个人所得税
     */
    @NotNull(message = "请输入个人所得税")
    @DecimalMin(value = "0.00", message = "个人所得税不能为负数")
    private BigDecimal incomeTax;

    /**
     * 发放状态
     */
    @NotNull(message = "请选择发放状态")
    private Integer paymentStatus;

    /**
     * 备注
     */
    private String remark;
}