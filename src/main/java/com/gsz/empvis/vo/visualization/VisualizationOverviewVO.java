package com.gsz.empvis.vo.visualization;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VisualizationOverviewVO {

    /**
     * 员工总数
     */
    private Long employeeTotal;

    /**
     * 部门数量
     */
    private Long deptTotal;

    /**
     * 本月正常考勤率
     */
    private BigDecimal attendanceRate;

    /**
     * 本月实发工资总额
     */
    private BigDecimal monthNetSalary;

    /**
     * 部门员工人数
     */
    private List<DeptStatistics> deptStatistics;

    /**
     * 性别分布
     */
    private List<GenderStatistics> genderStatistics;

    /**
     * 本月考勤状态
     */
    private List<AttendanceStatistics> attendanceStatistics;

    /**
     * 本月请假审批状态
     */
    private List<LeaveStatistics> leaveStatistics;

    /**
     * 近6个月薪资趋势
     */
    private List<SalaryTrend> salaryTrend;

    /**
     * 近6个月考勤异常趋势
     */
    private List<AttendanceTrend> attendanceTrend;


    /**
     * 部门员工统计
     */
    @Data
    public static class DeptStatistics {

        private String deptName;

        private Long employeeCount;
    }


    /**
     * 性别统计
     */
    @Data
    public static class GenderStatistics {

        private Integer gender;

        private Long employeeCount;
    }


    /**
     * 考勤状态统计
     */
    @Data
    public static class AttendanceStatistics {

        private Integer attendanceStatus;

        private Long attendanceCount;
    }


    /**
     * 请假审批统计
     */
    @Data
    public static class LeaveStatistics {

        private Integer approvalStatus;

        private Long leaveCount;
    }


    /**
     * 薪资趋势
     */
    @Data
    public static class SalaryTrend {

        private String salaryMonth;

        private BigDecimal grossSalary;

        private BigDecimal netSalary;
    }


    /**
     * 考勤异常趋势
     */
    @Data
    public static class AttendanceTrend {

        private String attendanceMonth;

        private Long abnormalCount;
    }
}