package com.gsz.empvis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("biz_salary")
public class EmpSalary {

    /**
     * 薪资记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 薪资月份
     * 例如：2026-08-01
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
     * 0-未发放
     * 1-已发放
     */
    private Integer paymentStatus;

    /**
     * 实际发放时间
     */
    private LocalDateTime paymentTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}