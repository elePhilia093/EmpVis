package com.gsz.empvis.vo.attendance;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class AttendanceVO {

    /**
     * 考勤记录ID
     */
    private Long id;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 考勤日期
     */
    private LocalDate attendanceDate;

    /**
     * 上班打卡时间
     */
    private LocalTime checkInTime;

    /**
     * 下班打卡时间
     */
    private LocalTime checkOutTime;

    /**
     * 考勤状态
     */
    private Integer attendanceStatus;

    /**
     * 迟到分钟数
     */
    private Integer lateMinutes;

    /**
     * 早退分钟数
     */
    private Integer earlyLeaveMinutes;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}