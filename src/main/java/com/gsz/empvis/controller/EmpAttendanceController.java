package com.gsz.empvis.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.attendance.AttendanceQueryDTO;
import com.gsz.empvis.security.LoginUser;
import com.gsz.empvis.service.EmpAttendanceService;
import com.gsz.empvis.vo.attendance.AttendanceVO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emp/attendance")
public class EmpAttendanceController {

    private final EmpAttendanceService empAttendanceService;

    public EmpAttendanceController(
            EmpAttendanceService empAttendanceService) {
        this.empAttendanceService = empAttendanceService;
    }

    /**
     * 分页查询考勤记录
     */
    @GetMapping("/list")
    public Result<IPage<AttendanceVO>> list(
            AttendanceQueryDTO queryDTO,
            Authentication authentication) {

        LoginUser loginUser =
                (LoginUser) authentication.getPrincipal();

        Long userId =
                loginUser.getUser().getId();

        return Result.success(
                empAttendanceService.page(
                        queryDTO,
                        userId
                )
        );
    }

    /**
     * 上班打卡
     */
    @PostMapping("/check-in")
    public Result<Void> checkIn(
            Authentication authentication) {

        LoginUser loginUser =
                (LoginUser) authentication.getPrincipal();

        Long userId =
                loginUser.getUser().getId();

        empAttendanceService.checkIn(userId);

        return Result.success(null);
    }

    /**
     * 下班打卡
     */
    @PostMapping("/check-out")
    public Result<Void> checkOut(
            Authentication authentication) {

        LoginUser loginUser =
                (LoginUser) authentication.getPrincipal();

        Long userId =
                loginUser.getUser().getId();

        empAttendanceService.checkOut(userId);

        return Result.success(null);
    }
}