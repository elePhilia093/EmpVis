package com.gsz.empvis.dto.attendance;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AttendanceQueryDTO {

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 考勤日期
     */
    private LocalDate attendanceDate;

    /**
     * 考勤状态
     */
    private Integer attendanceStatus;

    /**
     * 当前页
     */
    private Long current = 1L;

    /**
     * 每页条数
     */
    private Long size = 10L;
}