package com.gsz.empvis.excel.service;

import com.gsz.empvis.dto.attendance.AttendanceQueryDTO;
import com.gsz.empvis.dto.employee.EmployeeQueryDTO;
import com.gsz.empvis.dto.leave.LeaveQueryDTO;
import com.gsz.empvis.excel.vo.ExcelImportResultVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelService {

    ExcelImportResultVO importEmployee(
            MultipartFile file
    ) throws IOException;

    void exportEmployee(
            EmployeeQueryDTO queryDTO,
            HttpServletResponse response
    ) throws IOException;

    void exportAttendance(
            AttendanceQueryDTO queryDTO,
            HttpServletResponse response
    ) throws IOException;

    void exportLeave(
            LeaveQueryDTO queryDTO,
            HttpServletResponse response
    ) throws IOException;
}