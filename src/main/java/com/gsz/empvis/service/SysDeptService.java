package com.gsz.empvis.service;

import com.gsz.empvis.dto.dept.DeptQueryDTO;
import com.gsz.empvis.dto.dept.DeptAddDTO;
import com.gsz.empvis.dto.dept.DeptUpdateDTO;
import com.gsz.empvis.vo.dept.SysDeptVO;

import java.util.List;

public interface SysDeptService {

    /**
     * 部门查询
     */
    List<SysDeptVO> list(DeptQueryDTO queryDTO);

    /**
     * 新增子部门
     */
    void add(DeptAddDTO addDTO);

    /**
     * 修改部门
     */
    void update(DeptUpdateDTO updateDTO);

    /**
     * 删除部门
     */
    void delete(Long id);
}