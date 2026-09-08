package com.gsz.empvis.dto.announcement;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnnouncementAddDTO {

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    private String title;

    /**
     * 公告正文
     */
    @NotBlank(message = "公告内容不能为空")
    private String content;

    /**
     * 发布状态
     * 0-未发布
     * 1-已发布
     */
    private Integer publishStatus = 0;

    /**
     * 是否置顶
     * 0-否
     * 1-是
     */
    private Integer isTop = 0;
}