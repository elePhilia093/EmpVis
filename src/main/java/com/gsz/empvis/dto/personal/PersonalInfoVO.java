package com.gsz.empvis.vo.personal;

import lombok.Data;

@Data
public class PersonalInfoVO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 员工编号
     */
    private String employeeNo;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private String birthDate;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 职位名称
     */
    private String positionName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 用户状态
     */
    private Integer status;
}