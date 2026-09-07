package com.gsz.empvis.vo.leave;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LeaveVO {

    /**
     * 请假记录ID
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
     * 请假类型
     */
    private Integer leaveType;

    /**
     * 请假开始时间
     */
    private LocalDateTime startTime;

    /**
     * 请假结束时间
     */
    private LocalDateTime endTime;

    /**
     * 请假天数
     */
    private BigDecimal leaveDays;

    /**
     * 请假原因
     */
    private String reason;

    /**
     * 审批状态
     */
    private Integer approvalStatus;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 审批人姓名
     */
    private String approverName;

    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

    /**
     * 审批意见
     */
    private String approvalComment;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}