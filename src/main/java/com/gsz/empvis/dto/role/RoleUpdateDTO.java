package com.gsz.empvis.dto.role;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleUpdateDTO {

    @NotNull(message = "角色ID不能为空")
    private Long id;

    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    @Size(max = 50, message = "角色编码长度不能超过50个字符")
    private String roleCode;

    @NotNull(message = "角色状态不能为空")
    private Integer status;

    @Size(max = 255, message = "角色说明长度不能超过255个字符")
    private String remark;
}