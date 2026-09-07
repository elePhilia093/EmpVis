package com.gsz.empvis.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.employee.EmployeeAddDTO;
import com.gsz.empvis.dto.employee.EmployeeQueryDTO;
import com.gsz.empvis.dto.employee.EmployeeUpdateDTO;
import com.gsz.empvis.service.EmpEmployeeService;
import com.gsz.empvis.vo.employee.EmployeeVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emp/employee")
public class EmpEmployeeController {

    private final EmpEmployeeService empEmployeeService;

    public EmpEmployeeController(
            EmpEmployeeService empEmployeeService) {
        this.empEmployeeService = empEmployeeService;
    }

    /**
     * 分页查询员工
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:employee:list')")
    public Result<IPage<EmployeeVO>> page(
            EmployeeQueryDTO queryDTO) {

        return Result.success(
                empEmployeeService.page(queryDTO)
        );
    }

    /**
     * 新增员工
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:employee:add')")
    public Result<Void> add(
            @Valid @RequestBody EmployeeAddDTO addDTO) {

        empEmployeeService.add(addDTO);

        return Result.success(null);
    }

    /**
     * 修改员工
     */
    @PutMapping("update")
    @PreAuthorize("hasAuthority('system:employee:update')")
    public Result<Void> update(
            @Valid @RequestBody EmployeeUpdateDTO updateDTO) {

        empEmployeeService.update(updateDTO);

        return Result.success(null);
    }

    /**
     * 删除员工
     */
    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasAuthority('system:employee:delete')")
    public Result<Void> delete(
            @PathVariable Long id) {

        empEmployeeService.delete(id);

        return Result.success(null);
    }
}