package com.gsz.empvis.vo.menu;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuVO {

    private Long id;

    private Long parentId;

    private String menuName;

    private Integer menuType;

    private String path;

    private String component;

    private String permission;

    private String icon;

    private Integer sortOrder;

    private List<MenuVO> children = new ArrayList<>();
}