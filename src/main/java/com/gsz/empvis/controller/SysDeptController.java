package com.gsz.empvis.controller;

import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.dept.DeptAddDTO;
import com.gsz.empvis.dto.dept.DeptQueryDTO;
import com.gsz.empvis.dto.dept.DeptUpdateDTO;
import com.gsz.empvis.service.SysDeptService;
import com.gsz.empvis.vo.dept.SysDeptVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/dept")
public class SysDeptController {

    private final SysDeptService sysDeptService;

    public SysDeptController(SysDeptService sysDeptService) {
        this.sysDeptService = sysDeptService;
    }

    /**
     * 部门查询
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:dept:list')")
    public Result<List<SysDeptVO>> list(
            DeptQueryDTO queryDTO) {

        return Result.success(
                sysDeptService.list(queryDTO)
        );
    }

    /**
     * 新增子部门
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:dept:add')")
    public Result<Void> add(
            @Valid @RequestBody DeptAddDTO addDTO) {

        sysDeptService.add(addDTO);

        return Result.success(null);
    }

    /**
     * 修改部门
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:dept:update')")
    public Result<Void> update(
            @Valid @RequestBody DeptUpdateDTO updateDTO) {

        sysDeptService.update(updateDTO);

        return Result.success(null);
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('system:dept:delete')")
    public Result<Void> delete(
            @PathVariable Long id) {

        sysDeptService.delete(id);

        return Result.success(null);
    }
}