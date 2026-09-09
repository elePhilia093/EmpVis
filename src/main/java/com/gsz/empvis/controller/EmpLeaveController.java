package com.gsz.empvis.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.leave.LeaveAddDTO;
import com.gsz.empvis.dto.leave.LeaveAuditDTO;
import com.gsz.empvis.dto.leave.LeaveQueryDTO;
import com.gsz.empvis.excel.service.ExcelService;
import com.gsz.empvis.security.LoginUser;
import com.gsz.empvis.service.EmpLeaveService;
import com.gsz.empvis.vo.leave.LeaveVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Data
@RequestMapping("/emp/leave")
public class EmpLeaveController {

    private final EmpLeaveService empLeaveService;

    private final ExcelService excelService;

    /**
     * 分页查询请假记录
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:leave:list')")
    public Result<IPage<LeaveVO>> list(
            LeaveQueryDTO queryDTO,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return Result.success(
                empLeaveService.page(
                        queryDTO,
                        userId
                )
        );
    }

    /**
     * 提交请假申请
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:leave:apply')")
    public Result<Void> add(
            @Valid @RequestBody LeaveAddDTO addDTO,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        empLeaveService.add(
                addDTO,
                userId
        );

        return Result.success(null);
    }

    /**
     * 审批请假
     */
    @PutMapping("/audit")
    @PreAuthorize("hasAuthority('system:leave:audit')")
    public Result<Void> audit(
            @Valid @RequestBody LeaveAuditDTO auditDTO,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        empLeaveService.audit(
                auditDTO,
                userId
        );

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

    /**
     * Excel 导出
     */
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('system:leave:export')")
    public void export(
            LeaveQueryDTO queryDTO,
            HttpServletResponse response)
            throws IOException {

        excelService.exportLeave(
                queryDTO,
                response
        );
    }
}