package com.gsz.empvis.dto.role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RoleMenuDTO {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /**
     * 菜单权限ID集合
     * 允许为空，表示清空该角色权限
     */
    private List<Long> menuIds;
}