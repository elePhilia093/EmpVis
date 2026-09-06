package com.gsz.empvis.dto.dept;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeptAddDTO {

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门编码
     */
    @NotBlank(message = "部门编码不能为空")
    private String deptCode;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    /**
     * 负责人ID
     */
    private Long leaderId;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}