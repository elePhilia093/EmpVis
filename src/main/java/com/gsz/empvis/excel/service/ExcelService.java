package com.gsz.empvis.excel.service;

import com.gsz.empvis.dto.employee.EmployeeQueryDTO;
import com.gsz.empvis.excel.vo.ExcelImportResultVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelService {

    /**
     * 员工 Excel 导入
     */
    ExcelImportResultVO importEmployee(
            MultipartFile file
    ) throws IOException;


    void exportEmployee(
            EmployeeQueryDTO queryDTO,
            HttpServletResponse response
    ) throws IOException;
}