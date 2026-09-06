package com.gsz.empvis.service;

import com.gsz.empvis.dto.menu.MenuAddDTO;
import com.gsz.empvis.dto.menu.MenuQueryDTO;
import com.gsz.empvis.dto.menu.MenuUpdateDTO;
import com.gsz.empvis.vo.menu.MenuVO;

import java.util.List;

public interface SysMenuService {

    List<MenuVO> tree(MenuQueryDTO queryDTO);

    void add(MenuAddDTO addDTO);

    void update(MenuUpdateDTO updateDTO);

    void delete(Long id);
}