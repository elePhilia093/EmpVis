package com.gsz.empvis.dto.attendance;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class AttendanceExportDTO {

    @ExcelProperty("员工编号")
    private String employeeNo;

    @ExcelProperty("员工姓名")
    private String employeeName;

    @ExcelProperty("考勤日期")
    private String attendanceDate;

    @ExcelProperty("上班时间")
    private String checkInTime;

    @ExcelProperty("下班时间")
    private String checkOutTime;

    @ExcelProperty("考勤状态")
    private String attendanceStatus;

    @ExcelProperty("迟到分钟")
    private Integer lateMinutes;

    @ExcelProperty("早退分钟")
    private Integer earlyLeaveMinutes;

    @ExcelProperty("备注")
    private String remark;
}