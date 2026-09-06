package com.gsz.empvis.dto.dept;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeptUpdateDTO {

    @NotNull(message = "部门ID不能为空")
    private Long id;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 部门名称
     */
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