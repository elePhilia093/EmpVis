package com.gsz.empvis.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gsz.empvis.dto.attendance.AttendanceQueryDTO;
import com.gsz.empvis.vo.attendance.AttendanceVO;

public interface EmpAttendanceService {

    /**
     * 分页查询考勤记录
     */
    IPage<AttendanceVO> page(
            AttendanceQueryDTO queryDTO,
            Long userId);

    /**
     * 上班打卡
     */
    void checkIn(Long userId);

    /**
     * 下班打卡
     */
    void checkOut(Long userId);
}