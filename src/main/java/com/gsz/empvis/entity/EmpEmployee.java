package com.gsz.empvis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("emp_employee")
public class EmpEmployee {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String employeeNo;

    private String employeeName;

    private Integer gender;

    private LocalDate birthDate;

    private String phone;

    private String email;

    private Long deptId;

    private String positionName;
}