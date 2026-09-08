package com.gsz.empvis.mapper;

import com.gsz.empvis.vo.visualization.VisualizationOverviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface VisualizationMapper {

    /**
     * 员工总数
     */
    @Select("""
        SELECT COUNT(*)
        FROM emp_employee
        """)
    Long countEmployees();


    /**
     * 部门数量
     *
     * 只统计启用状态的部门
     */
    @Select("""
        SELECT COUNT(*)
        FROM sys_dept
        WHERE status = 1
        """)
    Long countDepartments();


    /**
     * 本月实发工资总额
     */
    @Select("""
        SELECT COALESCE(SUM(net_salary), 0)
        FROM biz_salary
        WHERE salary_month >= #{monthStart}
          AND salary_month < #{nextMonthStart}
        """)
    BigDecimal sumMonthNetSalary(
            @Param("monthStart") LocalDate monthStart,
            @Param("nextMonthStart") LocalDate nextMonthStart
    );


    /**
     * 本月正常考勤率
     *
     * 正常考勤记录数 / 本月考勤记录总数
     */
    @Select("""
        SELECT
            CASE
                WHEN COUNT(*) = 0 THEN 0
                ELSE
                    ROUND(
                        SUM(
                            CASE
                                WHEN attendance_status = 0
                                THEN 1
                                ELSE 0
                            END
                        ) * 100.0 / COUNT(*),
                        2
                    )
            END
        FROM biz_attendance
        WHERE attendance_date >= #{monthStart}
          AND attendance_date < #{nextMonthStart}
        """)
    BigDecimal getMonthAttendanceRate(
            @Param("monthStart") LocalDate monthStart,
            @Param("nextMonthStart") LocalDate nextMonthStart
    );


    /**
     * 部门员工人数
     */
    @Select("""
        SELECT
            d.dept_name AS deptName,
            COUNT(e.id) AS employeeCount
        FROM sys_dept d
        LEFT JOIN emp_employee e
            ON e.dept_id = d.id
        WHERE d.status = 1
        GROUP BY d.id, d.dept_name
        ORDER BY employeeCount DESC
        """)
    List<VisualizationOverviewVO.DeptStatistics>
    getDeptStatistics();


    /**
     * 员工性别分布
     */
    @Select("""
        SELECT
            gender,
            COUNT(*) AS employeeCount
        FROM emp_employee
        GROUP BY gender
        ORDER BY gender
        """)
    List<VisualizationOverviewVO.GenderStatistics>
    getGenderStatistics();


    /**
     * 本月考勤状态统计
     */
    @Select("""
        SELECT
            attendance_status AS attendanceStatus,
            COUNT(*) AS attendanceCount
        FROM biz_attendance
        WHERE attendance_date >= #{monthStart}
          AND attendance_date < #{nextMonthStart}
        GROUP BY attendance_status
        ORDER BY attendance_status
        """)
    List<VisualizationOverviewVO.AttendanceStatistics>
    getAttendanceStatistics(
            @Param("monthStart") LocalDate monthStart,
            @Param("nextMonthStart") LocalDate nextMonthStart
    );


    /**
     * 本月请假审批统计
     */
    @Select("""
        SELECT
            approval_status AS approvalStatus,
            COUNT(*) AS leaveCount
        FROM biz_leave
        WHERE start_time >= #{monthStartTime}
          AND start_time < #{nextMonthStartTime}
        GROUP BY approval_status
        ORDER BY approval_status
        """)
    List<VisualizationOverviewVO.LeaveStatistics>
    getLeaveStatistics(
            @Param("monthStartTime") LocalDate monthStartTime,
            @Param("nextMonthStartTime") LocalDate nextMonthStartTime
    );


    /**
     * 近6个月薪资趋势
     */
    @Select("""
        SELECT
            DATE_FORMAT(salary_month, '%Y-%m') AS salaryMonth,
            COALESCE(SUM(gross_salary), 0) AS grossSalary,
            COALESCE(SUM(net_salary), 0) AS netSalary
        FROM biz_salary
        WHERE salary_month >= #{trendStart}
          AND salary_month < #{nextMonthStart}
        GROUP BY DATE_FORMAT(salary_month, '%Y-%m')
        ORDER BY salaryMonth
        """)
    List<VisualizationOverviewVO.SalaryTrend>
    getSalaryTrend(
            @Param("trendStart") LocalDate trendStart,
            @Param("nextMonthStart") LocalDate nextMonthStart
    );


    /**
     * 近6个月考勤异常趋势
     *
     * attendance_status != 0
     * 表示存在迟到、早退等异常
     */
    @Select("""
        SELECT
            DATE_FORMAT(attendance_date, '%Y-%m')
                AS attendanceMonth,
            COUNT(*) AS abnormalCount
        FROM biz_attendance
        WHERE attendance_date >= #{trendStart}
          AND attendance_date < #{nextMonthStart}
          AND attendance_status <> 0
        GROUP BY DATE_FORMAT(attendance_date, '%Y-%m')
        ORDER BY attendanceMonth
        """)
    List<VisualizationOverviewVO.AttendanceTrend>
    getAttendanceTrend(
            @Param("trendStart") LocalDate trendStart,
            @Param("nextMonthStart") LocalDate nextMonthStart
    );
}