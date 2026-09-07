package com.gsz.empvis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("biz_leave")
public class EmpLeave {

    /**
     * 请假记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 申请员工ID
     */
    private Long employeeId;

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
     * 0-待审批
     * 1-已通过
     * 2-已驳回
     */
    private Integer approvalStatus;

    /**
     * 审批人用户ID
     */
    private Long approverId;

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