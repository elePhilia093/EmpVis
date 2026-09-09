package com.gsz.empvis.excel.model;

import com.gsz.empvis.dto.employee.EmployeeExcelDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeImportRow {

    /**
     * Excel行号
     */
    private Integer rowNumber;

    /**
     * 当前行数据
     */
    private EmployeeExcelDTO data;
}