package com.gsz.empvis.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserRoleDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 角色ID集合
     * 允许为空，表示清空该用户的全部角色
     */
    private List<Long> roleIds;
}