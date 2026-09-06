package com.gsz.empvis.dto.menu;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MenuUpdateDTO {

    @NotNull(message = "菜单ID不能为空")
    private Long id;

    @NotNull(message = "父级菜单不能为空")
    private Long parentId;

    @NotNull(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    @Size(max = 200, message = "路由路径长度不能超过200个字符")
    private String path;

    @Size(max = 255, message = "组件路径长度不能超过255个字符")
    private String component;

    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    private String permission;

    @Size(max = 100, message = "菜单图标长度不能超过100个字符")
    private String icon;

    private Integer sortOrder;
}