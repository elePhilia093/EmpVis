package com.gsz.empvis.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.gsz.empvis.dto.employee.EmployeeExcelDTO;
import com.gsz.empvis.excel.model.EmployeeImportRow;
import com.gsz.empvis.excel.vo.ExcelErrorVO;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EmployeeExcelListener
        extends AnalysisEventListener<EmployeeExcelDTO> {

    /**
     * Excel 数据总行数
     */
    private int totalRows;

    /**
     * 校验通过的数据
     */
    private final List<EmployeeImportRow> successList = new ArrayList<>();

    /**
     * Excel 错误信息
     */
    private final List<ExcelErrorVO> errorList = new ArrayList<>();

    @Override
    public void invoke(
            EmployeeExcelDTO data,
            AnalysisContext context) {

        int rowNumber =
                context.readRowHolder().getRowIndex() + 1;

        totalRows++;

        boolean valid = true;

        // 员工编号
        if (isBlank(data.getEmployeeNo())) {
            addError(
                    rowNumber,
                    "员工编号",
                    "员工编号不能为空"
            );
            valid = false;
        } else if (!data.getEmployeeNo()
                .trim()
                .matches("^ACSF\\d{4}$")) {

            addError(
                    rowNumber,
                    "员工编号",
                    "员工编号格式应为 ACSF0001"
            );
            valid = false;
        }

        // 员工姓名
        if (isBlank(data.getEmployeeName())) {
            addError(
                    rowNumber,
                    "员工姓名",
                    "员工姓名不能为空"
            );
            valid = false;
        }

        // 性别
        if (!"男".equals(data.getGender())
                && !"女".equals(data.getGender())) {

            addError(
                    rowNumber,
                    "性别",
                    "性别只能填写男或女"
            );
            valid = false;
        }

        // 手机号
        if (!isBlank(data.getPhone())
                && !data.getPhone()
                .trim()
                .matches("^1\\d{10}$")) {

            addError(
                    rowNumber,
                    "手机号",
                    "手机号格式不正确"
            );
            valid = false;
        }

        // 邮箱
        if (!isBlank(data.getEmail())
                && !data.getEmail()
                .trim()
                .matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                )) {

            addError(
                    rowNumber,
                    "邮箱",
                    "邮箱格式不正确"
            );
            valid = false;
        }

        // 部门编码
        if (isBlank(data.getDeptCode())) {

            addError(
                    rowNumber,
                    "部门编码",
                    "部门编码不能为空"
            );
            valid = false;

        } else if (!data.getDeptCode()
                .trim()
                .matches("^(ADM|HR|FIN|MKT|TECH|CS)$")) {

            addError(
                    rowNumber,
                    "部门编码",
                    "部门编码格式不正确"
            );
            valid = false;
        }

        // 职位
        if (isBlank(data.getPositionName())) {

            addError(
                    rowNumber,
                    "职位",
                    "职位不能为空"
            );
            valid = false;
        }


        // 出生日期
        if (!isBlank(data.getBirthDate())
                && !isValidDate(
                data.getBirthDate())) {

            addError(
                    rowNumber,
                    "出生日期",
                    "日期格式应为 yyyy-MM-dd"
            );
            valid = false;
        }

        // 校验通过
        if (valid) {

            successList.add(
                    new EmployeeImportRow(
                            rowNumber,
                            data
                    )
            );
        }
    }

    private void addError(
            Integer rowNumber,
            String field,
            String message) {

        errorList.add(
                new ExcelErrorVO(
                        rowNumber,
                        field,
                        message
                )
        );
    }

    private boolean isBlank(String value) {

        return value == null
                || value.trim().isEmpty();
    }

    private boolean isValidDate(String value) {

        try {
            LocalDate.parse(value.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidEmploymentType(
            String value) {

        return "正式".equals(value)
                || "试用".equals(value)
                || "实习".equals(value);
    }

    private boolean isValidEducation(
            String value) {

        return "大专".equals(value)
                || "本科".equals(value)
                || "硕士".equals(value)
                || "博士".equals(value);
    }

    private boolean isValidEmployeeStatus(
            String value) {

        return "在职".equals(value)
                || "离职".equals(value);
    }

    @Override
    public void doAfterAllAnalysed(
            AnalysisContext context) {
    }
}