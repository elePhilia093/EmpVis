package com.gsz.empvis.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.salary.SalaryAddDTO;
import com.gsz.empvis.dto.salary.SalaryQueryDTO;
import com.gsz.empvis.dto.salary.SalaryUpdateDTO;
import com.gsz.empvis.security.LoginUser;
import com.gsz.empvis.service.EmpSalaryService;
import com.gsz.empvis.vo.salary.SalaryVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emp/salary")
public class EmpSalaryController {

    private final EmpSalaryService empSalaryService;

    public EmpSalaryController(
            EmpSalaryService empSalaryService) {
        this.empSalaryService = empSalaryService;
    }

    /**
     * 分页查询薪资记录
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:salary:list')")
    public Result<IPage<SalaryVO>> list(
            SalaryQueryDTO queryDTO,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return Result.success(
                empSalaryService.page(
                        queryDTO,
                        userId
                )
        );
    }

    /**
     * 新增薪资
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:salary:add')")
    public Result<Void> add(
            @Valid @RequestBody SalaryAddDTO addDTO) {

        empSalaryService.add(addDTO);

        return Result.success(null);
    }

    /**
     * 修改薪资
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:salary:update')")
    public Result<Void> update(
            @Valid @RequestBody SalaryUpdateDTO updateDTO) {

        empSalaryService.update(updateDTO);

        return Result.success(null);
    }

    /**
     * 删除薪资
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('system:salary:delete')")
    public Result<Void> delete(
            @PathVariable Long id) {

        empSalaryService.delete(id);

        return Result.success(null);
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getUserId(
            Authentication authentication) {

        LoginUser loginUser =
                (LoginUser) authentication.getPrincipal();

        return loginUser.getUser().getId();
    }
}