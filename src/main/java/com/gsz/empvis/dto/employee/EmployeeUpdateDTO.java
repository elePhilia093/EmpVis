package com.gsz.empvis.dto.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeUpdateDTO {

    /**
     * 员工ID
     */
    @NotNull(message = "员工ID不能为空")
    private Long id;

    /**
     * 员工编号
     */
    @NotBlank(message = "员工编号不能为空")
    private String employeeNo;

    /**
     * 员工姓名
     */
    @NotBlank(message = "员工姓名不能为空")
    private String employeeName;

    /**
     * 性别
     */
    @NotNull(message = "请选择性别")
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 所属部门ID
     */
    @NotNull(message = "请选择所属部门")
    private Long deptId;

    /**
     * 职位名称
     */
    @NotBlank(message = "职位名称不能为空")
    private String positionName;
}