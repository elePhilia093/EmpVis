package com.gsz.empvis.vo.dept;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SysDeptVO {

    private Long id;

    private Long parentId;

    private String deptCode;

    private String deptName;

    private Long leaderId;

    private Integer sortOrder;

    private Integer status;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private List<SysDeptVO> children;
}