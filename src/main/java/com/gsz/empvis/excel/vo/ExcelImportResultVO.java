package com.gsz.empvis.excel.vo;

import lombok.Data;

import java.util.List;

@Data
public class ExcelImportResultVO {

    /**
     * 数据总行数
     */
    private Integer total;

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer errorCount;

    /**
     * 错误明细
     */
    private List<ExcelErrorVO> errorList;
}