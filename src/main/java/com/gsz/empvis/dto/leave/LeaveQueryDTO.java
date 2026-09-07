package com.gsz.empvis.dto.leave;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveQueryDTO {

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 审批状态
     * 0-待审批
     * 1-已通过
     * 2-已驳回
     */
    private Integer approvalStatus;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 当前页
     */
    private Long current = 1L;

    /**
     * 每页条数
     */
    private Long size = 10L;
}