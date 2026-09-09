package com.gsz.empvis.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.employee.EmployeeAddDTO;
import com.gsz.empvis.dto.employee.EmployeeQueryDTO;
import com.gsz.empvis.dto.employee.EmployeeUpdateDTO;
import com.gsz.empvis.excel.service.ExcelService;
import com.gsz.empvis.excel.vo.ExcelImportResultVO;
import com.gsz.empvis.service.EmployeeService;
import com.gsz.empvis.vo.employee.EmployeeVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/emp/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    private final ExcelService excelService;

    public EmployeeController(
            EmployeeService employeeService,
            ExcelService excelService) {

        this.employeeService = employeeService;
        this.excelService = excelService;
    }

    /**
     * 分页查询员工
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:employee:list')")
    public Result<IPage<EmployeeVO>> page(
            EmployeeQueryDTO queryDTO) {

        return Result.success(
                employeeService.page(queryDTO)
        );
    }

    /**
     * 新增员工
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:employee:add')")
    public Result<Void> add(
            @Valid @RequestBody EmployeeAddDTO addDTO) {

        employeeService.add(addDTO);

        return Result.success(null);
    }

    /**
     * 修改员工
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:employee:update')")
    public Result<Void> update(
            @Valid @RequestBody EmployeeUpdateDTO updateDTO) {

        employeeService.update(updateDTO);

        return Result.success(null);
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('system:employee:delete')")
    public Result<Void> delete(
            @PathVariable Long id) {

        employeeService.delete(id);

        return Result.success(null);
    }

    /**
     * Excel 导入员工
     */
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('system:employee:import')")
    public Result<ExcelImportResultVO> importExcel(
            @RequestParam("file") MultipartFile file)
            throws IOException {

        ExcelImportResultVO result =
                excelService.importEmployee(file);

        return Result.success(result);
    }


    @GetMapping("/export")
    @PreAuthorize("hasAuthority('system:employee:export')")
    public void export(
            EmployeeQueryDTO queryDTO,
            HttpServletResponse response)
            throws IOException {

        excelService.exportEmployee(
                queryDTO,
                response
        );
    }
}