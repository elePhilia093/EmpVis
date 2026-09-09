package com.gsz.empvis.dto.employee;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class EmployeeExcelDTO {

    @ExcelProperty("员工编号")
    private String employeeNo;

    @ExcelProperty("员工姓名")
    private String employeeName;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("出生日期")
    private String birthDate;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("部门编码")
    private String deptCode;

    @ExcelProperty("职位")
    private String positionName;
}