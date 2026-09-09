package com.gsz.empvis.dto.leave;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LeaveExportDTO {

    @ExcelProperty("员工编号")
    private String employeeNo;

    @ExcelProperty("员工姓名")
    private String employeeName;

    @ExcelProperty("请假类型")
    private String leaveType;

    @ExcelProperty("开始时间")
    private String startTime;

    @ExcelProperty("结束时间")
    private String endTime;

    @ExcelProperty("请假天数")
    private BigDecimal leaveDays;

    @ExcelProperty("请假原因")
    private String reason;

    @ExcelProperty("审批状态")
    private String approvalStatus;

    @ExcelProperty("审批人")
    private String approverName;

    @ExcelProperty("审批时间")
    private String approvalTime;

    @ExcelProperty("审批意见")
    private String approvalComment;
}