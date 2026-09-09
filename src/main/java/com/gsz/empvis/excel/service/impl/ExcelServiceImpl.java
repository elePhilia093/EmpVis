package com.gsz.empvis.excel.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gsz.empvis.dto.attendance.AttendanceExportDTO;
import com.gsz.empvis.dto.attendance.AttendanceQueryDTO;
import com.gsz.empvis.dto.employee.EmployeeExcelDTO;
import com.gsz.empvis.dto.employee.EmployeeExportDTO;
import com.gsz.empvis.dto.employee.EmployeeQueryDTO;
import com.gsz.empvis.dto.leave.LeaveExportDTO;
import com.gsz.empvis.dto.leave.LeaveQueryDTO;
import com.gsz.empvis.entity.*;
import com.gsz.empvis.excel.listener.EmployeeExcelListener;
import com.gsz.empvis.excel.model.EmployeeImportRow;
import com.gsz.empvis.excel.service.ExcelService;
import com.gsz.empvis.excel.vo.ExcelErrorVO;
import com.gsz.empvis.excel.vo.ExcelImportResultVO;
import com.gsz.empvis.mapper.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Data
public class ExcelServiceImpl implements ExcelService {

    @Resource
    private EmployeeMapper employeeMapper;

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private EmpAttendanceMapper empAttendanceMapper;

    @Resource
    private EmpLeaveMapper empLeaveMapper;


    @Override
    public void exportAttendance(
            AttendanceQueryDTO queryDTO,
            HttpServletResponse response) throws IOException {

        LambdaQueryWrapper<EmpAttendance> wrapper =
                new LambdaQueryWrapper<>();

        if (queryDTO != null) {

            if (queryDTO.getEmployeeId() != null) {
                wrapper.eq(
                        EmpAttendance::getEmployeeId,
                        queryDTO.getEmployeeId()
                );
            }

            if (queryDTO.getAttendanceDate() != null) {
                wrapper.eq(
                        EmpAttendance::getAttendanceDate,
                        queryDTO.getAttendanceDate()
                );
            }

            if (queryDTO.getAttendanceStatus() != null) {
                wrapper.eq(
                        EmpAttendance::getAttendanceStatus,
                        queryDTO.getAttendanceStatus()
                );
            }
        }

        wrapper.orderByDesc(
                EmpAttendance::getAttendanceDate
        );

        List<EmpAttendance> list =
                empAttendanceMapper.selectList(wrapper);

        Set<Long> employeeIds =
                list.stream()
                        .map(EmpAttendance::getEmployeeId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        Map<Long, EmpEmployee> employeeMap =
                employeeIds.isEmpty()
                        ? new HashMap<>()
                        : employeeMapper.selectBatchIds(employeeIds)
                        .stream()
                        .collect(Collectors.toMap(
                                EmpEmployee::getId,
                                employee -> employee
                        ));

        List<AttendanceExportDTO> exportList =
                new ArrayList<>();

        for (EmpAttendance attendance : list) {

            AttendanceExportDTO dto =
                    new AttendanceExportDTO();

            EmpEmployee employee =
                    employeeMap.get(
                            attendance.getEmployeeId()
                    );

            if (employee != null) {
                dto.setEmployeeNo(
                        employee.getEmployeeNo()
                );

                dto.setEmployeeName(
                        employee.getEmployeeName()
                );
            }

            dto.setAttendanceDate(
                    attendance.getAttendanceDate() == null
                            ? ""
                            : attendance.getAttendanceDate().toString()
            );

            dto.setCheckInTime(
                    attendance.getCheckInTime() == null
                            ? ""
                            : attendance.getCheckInTime().toString()
            );

            dto.setCheckOutTime(
                    attendance.getCheckOutTime() == null
                            ? ""
                            : attendance.getCheckOutTime().toString()
            );

            dto.setAttendanceStatus(
                    convertAttendanceStatus(
                            attendance.getAttendanceStatus()
                    )
            );

            dto.setLateMinutes(
                    attendance.getLateMinutes()
            );

            dto.setEarlyLeaveMinutes(
                    attendance.getEarlyLeaveMinutes()
            );

            dto.setRemark(
                    attendance.getRemark()
            );

            exportList.add(dto);
        }

        writeExcel(
                response,
                "考勤记录.xlsx",
                "考勤记录",
                AttendanceExportDTO.class,
                exportList
        );
    }

    private String convertAttendanceStatus(
            Integer status) {

        if (status == null) {
            return "";
        }

        return switch (status) {
            case 1 -> "正常";
            case 2 -> "迟到";
            case 3 -> "早退";
            case 4 -> "迟到早退";
            default -> "未知";
        };
    }

    @Override
    public void exportLeave(
            LeaveQueryDTO queryDTO,
            HttpServletResponse response) throws IOException {

        LambdaQueryWrapper<EmpLeave> wrapper =
                new LambdaQueryWrapper<>();

        if (queryDTO != null) {

            if (queryDTO.getEmployeeId() != null) {
                wrapper.eq(
                        EmpLeave::getEmployeeId,
                        queryDTO.getEmployeeId()
                );
            }

            if (queryDTO.getApprovalStatus() != null) {
                wrapper.eq(
                        EmpLeave::getApprovalStatus,
                        queryDTO.getApprovalStatus()
                );
            }

            if (queryDTO.getStartDate() != null) {
                wrapper.ge(
                        EmpLeave::getStartTime,
                        queryDTO.getStartDate()
                                .atStartOfDay()
                );
            }

            if (queryDTO.getEndDate() != null) {
                wrapper.lt(
                        EmpLeave::getStartTime,
                        queryDTO.getEndDate()
                                .plusDays(1)
                                .atStartOfDay()
                );
            }
        }

        wrapper.orderByDesc(
                EmpLeave::getStartTime
        );

        List<EmpLeave> list =
                empLeaveMapper.selectList(wrapper);

        Set<Long> employeeIds =
                list.stream()
                        .map(EmpLeave::getEmployeeId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        Set<Long> approverIds =
                list.stream()
                        .map(EmpLeave::getApproverId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        Map<Long, EmpEmployee> employeeMap =
                employeeIds.isEmpty()
                        ? new HashMap<>()
                        : employeeMapper.selectBatchIds(employeeIds)
                        .stream()
                        .collect(Collectors.toMap(
                                EmpEmployee::getId,
                                employee -> employee
                        ));

        Map<Long, SysUser> userMap =
                approverIds.isEmpty()
                        ? new HashMap<>()
                        : sysUserMapper.selectBatchIds(approverIds)
                        .stream()
                        .collect(Collectors.toMap(
                                SysUser::getId,
                                user -> user
                        ));

        List<LeaveExportDTO> exportList =
                new ArrayList<>();

        for (EmpLeave leave : list) {

            LeaveExportDTO dto =
                    new LeaveExportDTO();

            EmpEmployee employee =
                    employeeMap.get(
                            leave.getEmployeeId()
                    );

            if (employee != null) {
                dto.setEmployeeNo(
                        employee.getEmployeeNo()
                );

                dto.setEmployeeName(
                        employee.getEmployeeName()
                );
            }

            dto.setLeaveType(
                    convertLeaveType(
                            leave.getLeaveType()
                    )
            );

            dto.setStartTime(
                    leave.getStartTime() == null
                            ? ""
                            : leave.getStartTime().toString()
            );

            dto.setEndTime(
                    leave.getEndTime() == null
                            ? ""
                            : leave.getEndTime().toString()
            );

            dto.setLeaveDays(
                    leave.getLeaveDays()
            );

            dto.setReason(
                    leave.getReason()
            );

            dto.setApprovalStatus(
                    convertApprovalStatus(
                            leave.getApprovalStatus()
                    )
            );

            SysUser approver =
                    userMap.get(
                            leave.getApproverId()
                    );

            dto.setApproverName(
                    approver == null
                            ? ""
                            : approver.getUsername()
            );

            dto.setApprovalTime(
                    leave.getApprovalTime() == null
                            ? ""
                            : leave.getApprovalTime().toString()
            );

            dto.setApprovalComment(
                    leave.getApprovalComment()
            );

            exportList.add(dto);
        }

        writeExcel(
                response,
                "请假记录.xlsx",
                "请假记录",
                LeaveExportDTO.class,
                exportList
        );
    }
    private String convertLeaveType(
            Integer type) {

        if (type == null) {
            return "";
        }

        return switch (type) {
            case 1 -> "事假";
            case 2 -> "病假";
            case 3 -> "年假";
            case 4 -> "婚假";
            case 5 -> "产假";
            default -> "其他";
        };
    }

    private String convertApprovalStatus(
            Integer status) {

        if (status == null) {
            return "";
        }

        return switch (status) {
            case 0 -> "待审批";
            case 1 -> "已通过";
            case 2 -> "已驳回";
            default -> "未知";
        };
    }
    @Override
    public void exportEmployee(
            EmployeeQueryDTO queryDTO,
            HttpServletResponse response) throws IOException {

        LambdaQueryWrapper<EmpEmployee> wrapper =
                new LambdaQueryWrapper<>();

        if (queryDTO != null) {

            if (queryDTO.getEmployeeNo() != null
                    && !queryDTO.getEmployeeNo().isBlank()) {

                wrapper.like(
                        EmpEmployee::getEmployeeNo,
                        queryDTO.getEmployeeNo().trim()
                );
            }

            if (queryDTO.getEmployeeName() != null
                    && !queryDTO.getEmployeeName().isBlank()) {

                wrapper.like(
                        EmpEmployee::getEmployeeName,
                        queryDTO.getEmployeeName().trim()
                );
            }

            if (queryDTO.getDeptId() != null) {

                wrapper.eq(
                        EmpEmployee::getDeptId,
                        queryDTO.getDeptId()
                );
            }

            if (queryDTO.getGender() != null) {

                wrapper.eq(
                        EmpEmployee::getGender,
                        queryDTO.getGender()
                );
            }
        }

        wrapper.orderByAsc(
                EmpEmployee::getId
        );

        List<EmpEmployee> employeeList =
                employeeMapper.selectList(wrapper);

        Set<Long> deptIds =
                employeeList.stream()
                        .map(EmpEmployee::getDeptId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        Map<Long, SysDept> deptMap =
                deptIds.isEmpty()
                        ? Map.of()
                        : sysDeptMapper.selectBatchIds(deptIds)
                        .stream()
                        .collect(Collectors.toMap(
                                SysDept::getId,
                                dept -> dept
                        ));

        List<EmployeeExportDTO> exportList =
                new ArrayList<>();

        for (EmpEmployee employee : employeeList) {

            EmployeeExportDTO dto =
                    new EmployeeExportDTO();

            dto.setEmployeeNo(
                    employee.getEmployeeNo()
            );

            dto.setEmployeeName(
                    employee.getEmployeeName()
            );

            dto.setGender(
                    employee.getGender() != null
                            ? employee.getGender() == 1
                            ? "男"
                            : "女"
                            : ""
            );

            dto.setBirthDate(
                    employee.getBirthDate() != null
                            ? employee.getBirthDate().toString()
                            : ""
            );

            dto.setPhone(
                    employee.getPhone()
            );

            dto.setEmail(
                    employee.getEmail()
            );

            SysDept dept =
                    deptMap.get(
                            employee.getDeptId()
                    );

            if (dept != null) {

                dto.setDeptCode(
                        dept.getDeptCode()
                );
            }

            dto.setPositionName(
                    employee.getPositionName()
            );

            exportList.add(dto);
        }

        String fileName =
                URLEncoder.encode(
                        "员工信息.xlsx",
                        StandardCharsets.UTF_8
                ).replace("+", "%20");

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        response.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        response.setHeader(
                "Content-Disposition",
                "attachment; filename*=UTF-8''" + fileName
        );

        EasyExcel.write(
                        response.getOutputStream(),
                        EmployeeExportDTO.class
                )
                .autoCloseStream(false)
                .sheet("员工信息")
                .doWrite(exportList);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExcelImportResultVO importEmployee(
            MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Excel文件不能为空");
        }

        /*
         * 读取 Excel
         */
        EmployeeExcelListener listener =
                new EmployeeExcelListener();

        EasyExcel.read(
                file.getInputStream(),
                EmployeeExcelDTO.class,
                listener
        ).sheet().doRead();

        /*
         * Excel 格式校验通过的数据
         */
        List<EmployeeImportRow> rows =
                listener.getSuccessList();

        /*
         * Excel 格式错误
         */
        List<ExcelErrorVO> errorList =
                new ArrayList<>(
                        listener.getErrorList()
                );

        int totalRows =
                listener.getTotalRows();

        if (rows.isEmpty()) {
            return buildResult(
                    totalRows,
                    0,
                    errorList
            );
        }

        /*
         * 查询 Excel 中涉及的部门
         */
        Set<String> deptCodes =
                rows.stream()
                        .map(row ->
                                row.getData().getDeptCode())
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .collect(Collectors.toSet());

        List<SysDept> deptList =
                sysDeptMapper.selectList(
                        new LambdaQueryWrapper<SysDept>()
                                .in(
                                        SysDept::getDeptCode,
                                        deptCodes
                                )
                                .eq(
                                        SysDept::getStatus,
                                        1
                                )
                );

        /*
         * 部门编码 -> 部门 ID
         */
        Map<String, Long> deptMap =
                deptList.stream()
                        .collect(
                                Collectors.toMap(
                                        SysDept::getDeptCode,
                                        SysDept::getId
                                )
                        );

        /*
         * 查询数据库中已经存在的员工编号
         */
        Set<String> employeeNos =
                rows.stream()
                        .map(row ->
                                row.getData().getEmployeeNo())
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .collect(Collectors.toSet());

        List<EmpEmployee> existsList =
                employeeMapper.selectList(
                        new LambdaQueryWrapper<EmpEmployee>()
                                .in(
                                        EmpEmployee::getEmployeeNo,
                                        employeeNos
                                )
                );

        Set<String> existsNos =
                existsList.stream()
                        .map(EmpEmployee::getEmployeeNo)
                        .collect(Collectors.toSet());

        /*
         * 检查 Excel 内部重复员工编号
         */
        Map<String, Long> employeeNoCountMap =
                rows.stream()
                        .collect(
                                Collectors.groupingBy(
                                        row ->
                                                row.getData()
                                                        .getEmployeeNo()
                                                        .trim(),
                                        Collectors.counting()
                                )
                        );

        /*
         * 组装员工数据
         */
        List<EmpEmployee> employeeList =
                new ArrayList<>();

        for (EmployeeImportRow row : rows) {

            EmployeeExcelDTO dto =
                    row.getData();

            Integer rowNumber =
                    row.getRowNumber();

            boolean valid = true;

            String employeeNo =
                    dto.getEmployeeNo().trim();

            String deptCode =
                    dto.getDeptCode().trim();

            /*
             * 数据库中员工编号重复
             */
            if (existsNos.contains(employeeNo)) {

                errorList.add(
                        new ExcelErrorVO(
                                rowNumber,
                                "员工编号",
                                "员工编号已存在"
                        )
                );

                valid = false;
            }

            /*
             * Excel 内部员工编号重复
             */
            if (employeeNoCountMap.get(employeeNo) > 1) {

                errorList.add(
                        new ExcelErrorVO(
                                rowNumber,
                                "员工编号",
                                "Excel中存在重复员工编号"
                        )
                );

                valid = false;
            }

            /*
             * 部门不存在
             */
            Long deptId =
                    deptMap.get(deptCode);

            if (deptId == null) {

                errorList.add(
                        new ExcelErrorVO(
                                rowNumber,
                                "部门编码",
                                "部门编码不存在"
                        )
                );

                valid = false;
            }

            if (!valid) {
                continue;
            }

            /*
             * DTO -> Entity
             *
             * 只使用当前 EmpEmployee
             * 实际存在的字段
             */
            EmpEmployee employee =
                    new EmpEmployee();

            employee.setEmployeeNo(
                    employeeNo
            );

            employee.setEmployeeName(
                    dto.getEmployeeName().trim()
            );

            employee.setGender(
                    "男".equals(dto.getGender())
                            ? 1
                            : 0
            );

            /*
             * 出生日期
             */
            if (!isBlank(dto.getBirthDate())) {

                employee.setBirthDate(
                        LocalDate.parse(
                                dto.getBirthDate().trim()
                        )
                );
            }

            /*
             * 联系电话
             */
            employee.setPhone(
                    dto.getPhone()
            );

            /*
             * 电子邮箱
             */
            employee.setEmail(
                    dto.getEmail()
            );

            /*
             * 部门编码转换为部门 ID
             */
            employee.setDeptId(
                    deptId
            );

            /*
             * 职位
             */
            employee.setPositionName(
                    dto.getPositionName()
            );

            employeeList.add(employee);
        }

        /*
         * 批量保存
         */
        for (EmpEmployee employee : employeeList) {
            employeeMapper.insert(employee);
        }

        /*
         * 返回结果
         */
        return buildResult(
                totalRows,
                employeeList.size(),
                errorList
        );
    }

    /**
     * 组装导入结果
     */
    private ExcelImportResultVO buildResult(
            int totalRows,
            int successCount,
            List<ExcelErrorVO> errorList) {

        ExcelImportResultVO result =
                new ExcelImportResultVO();

        result.setTotal(totalRows);

        result.setSuccessCount(
                successCount
        );

        long errorRows =
                errorList.stream()
                        .map(ExcelErrorVO::getRowNumber)
                        .distinct()
                        .count();

        result.setErrorCount(
                (int) errorRows
        );

        result.setErrorList(
                errorList
        );

        return result;
    }

    private boolean isBlank(String value) {

        return value == null
                || value.trim().isEmpty();
    }

    private <T> void writeExcel(
            HttpServletResponse response,
            String fileName,
            String sheetName,
            Class<T> clazz,
            List<T> data
    ) throws IOException {

        String encodedFileName =
                URLEncoder.encode(
                        fileName,
                        StandardCharsets.UTF_8
                ).replace("+", "%20");

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        response.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        response.setHeader(
                "Content-Disposition",
                "attachment; filename*=UTF-8''"
                        + encodedFileName
        );

        EasyExcel.write(
                        response.getOutputStream(),
                        clazz
                )
                .autoCloseStream(false)
                .sheet(sheetName)
                .doWrite(data);
    }
}