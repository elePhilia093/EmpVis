package com.gsz.empvis.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gsz.empvis.dto.employee.EmployeeAddDTO;
import com.gsz.empvis.dto.employee.EmployeeQueryDTO;
import com.gsz.empvis.dto.employee.EmployeeUpdateDTO;
import com.gsz.empvis.vo.employee.EmployeeVO;

public interface EmployeeService {

    /**
     * 分页查询员工
     */
    IPage<EmployeeVO> page(EmployeeQueryDTO queryDTO);

    /**
     * 新增员工
     */
    void add(EmployeeAddDTO addDTO);

    /**
     * 修改员工
     */
    void update(EmployeeUpdateDTO updateDTO);

    /**
     * 删除员工
     */
    void delete(Long id);
}