package com.gsz.empvis.dto.leave;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveAuditDTO {

    /**
     * 请假记录ID
     */
    @NotNull(message = "请假记录ID不能为空")
    private Long id;

    /**
     * 审批结果
     * 1-通过
     * 2-驳回
     */
    @NotNull(message = "请选择审批结果")
    private Integer approvalStatus;

    /**
     * 审批意见
     */
    private String approvalComment;
}