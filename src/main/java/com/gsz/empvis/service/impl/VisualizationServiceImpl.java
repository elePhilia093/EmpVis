package com.gsz.empvis.service.impl;

import com.gsz.empvis.mapper.VisualizationMapper;
import com.gsz.empvis.service.VisualizationService;
import com.gsz.empvis.vo.visualization.VisualizationOverviewVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class VisualizationServiceImpl
        implements VisualizationService {

    private final VisualizationMapper visualizationMapper;

    public VisualizationServiceImpl(
            VisualizationMapper visualizationMapper) {

        this.visualizationMapper =
                visualizationMapper;
    }

    @Override
    public VisualizationOverviewVO getOverview(
            YearMonth month) {

        /*
         * 当前月份
         *
         * 例如：
         * 2026-09
         */
        LocalDate monthStart =
                month.atDay(1);

        LocalDate nextMonthStart =
                month.plusMonths(1)
                        .atDay(1);

        /*
         * 近6个月统计起点
         *
         * 例如查询 2026-09，
         * 趋势范围：
         * 2026-04 ~ 2026-09
         */
        YearMonth trendMonth =
                month.minusMonths(5);

        LocalDate trendStart =
                trendMonth.atDay(1);

        VisualizationOverviewVO vo =
                new VisualizationOverviewVO();

        /*
         * 顶部概览数据
         */

        vo.setEmployeeTotal(
                visualizationMapper
                        .countEmployees()
        );

        vo.setDeptTotal(
                visualizationMapper
                        .countDepartments()
        );

        vo.setAttendanceRate(
                visualizationMapper
                        .getMonthAttendanceRate(
                                monthStart,
                                nextMonthStart
                        )
        );

        vo.setMonthNetSalary(
                visualizationMapper
                        .sumMonthNetSalary(
                                monthStart,
                                nextMonthStart
                        )
        );

        /*
         * 员工数据
         */

        vo.setDeptStatistics(
                visualizationMapper
                        .getDeptStatistics()
        );

        vo.setGenderStatistics(
                visualizationMapper
                        .getGenderStatistics()
        );

        /*
         * 本月考勤、请假
         */

        vo.setAttendanceStatistics(
                visualizationMapper
                        .getAttendanceStatistics(
                                monthStart,
                                nextMonthStart
                        )
        );

        vo.setLeaveStatistics(
                visualizationMapper
                        .getLeaveStatistics(
                                monthStart,
                                nextMonthStart
                        )
        );

        /*
         * 近6个月趋势
         */

        vo.setSalaryTrend(
                visualizationMapper
                        .getSalaryTrend(
                                trendStart,
                                nextMonthStart
                        )
        );

        vo.setAttendanceTrend(
                visualizationMapper
                        .getAttendanceTrend(
                                trendStart,
                                nextMonthStart
                        )
        );

        return vo;
    }
}