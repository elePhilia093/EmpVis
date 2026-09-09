package com.gsz.empvis.excel.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExcelErrorVO {

    /**
     * Excel行号
     */
    private Integer rowNumber;

    /**
     * 错误字段
     */
    private String field;

    /**
     * 错误信息
     */
    private String message;
}