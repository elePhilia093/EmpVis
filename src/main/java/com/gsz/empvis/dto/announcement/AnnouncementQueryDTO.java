package com.gsz.empvis.dto.announcement;

import lombok.Data;

@Data
public class AnnouncementQueryDTO {

    /**
     * 公告标题
     */
    private String title;

    /**
     * 发布状态
     */
    private Integer publishStatus;

    /**
     * 当前页
     */
    private Long current = 1L;

    /**
     * 每页条数
     */
    private Long size = 10L;
}