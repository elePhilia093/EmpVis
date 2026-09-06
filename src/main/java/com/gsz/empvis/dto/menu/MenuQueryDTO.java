package com.gsz.empvis.dto.menu;

import lombok.Data;

@Data
public class MenuQueryDTO {

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单类型
     */
    private Integer menuType;
}