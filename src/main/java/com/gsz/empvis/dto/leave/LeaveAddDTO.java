package com.gsz.empvis.dto.leave;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeaveAddDTO {

    /**
     * 请假类型
     */
    @NotNull(message = "请选择请假类型")
    private Integer leaveType;

    /**
     * 请假开始时间
     */
    @NotNull(message = "请选择开始时间")
    @FutureOrPresent(message = "开始时间不能早于当前时间")
    private LocalDateTime startTime;

    /**
     * 请假结束时间
     */
    @NotNull(message = "请选择结束时间")
    @FutureOrPresent(message = "结束时间不能早于当前时间")
    private LocalDateTime endTime;

    /**
     * 请假原因
     */
    @NotBlank(message = "请填写请假原因")
    private String reason;
}