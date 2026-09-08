package com.gsz.empvis.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gsz.empvis.dto.salary.SalaryAddDTO;
import com.gsz.empvis.dto.salary.SalaryQueryDTO;
import com.gsz.empvis.dto.salary.SalaryUpdateDTO;
import com.gsz.empvis.vo.salary.SalaryVO;

public interface EmpSalaryService {

    /**
     * 分页查询薪资
     */
    IPage<SalaryVO> page(
            SalaryQueryDTO queryDTO,
            Long userId);

    /**
     * 新增薪资
     */
    void add(SalaryAddDTO addDTO);

    /**
     * 修改薪资
     */
    void update(SalaryUpdateDTO updateDTO);

    /**
     * 删除薪资
     */
    void delete(Long id);
}