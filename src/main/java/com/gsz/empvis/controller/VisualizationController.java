package com.gsz.empvis.controller;

import com.gsz.empvis.common.Result;
import com.gsz.empvis.service.VisualizationService;
import com.gsz.empvis.vo.visualization.VisualizationOverviewVO;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/visualization")
public class VisualizationController {

    private final VisualizationService visualizationService;

    public VisualizationController(
            VisualizationService visualizationService) {

        this.visualizationService =
                visualizationService;
    }

    /**
     * 获取可视化统计数据
     */
    @GetMapping("/overview")
    public Result<VisualizationOverviewVO> getOverview(
            @RequestParam(required = false)
            String month) {

        /*
         * 前端未传月份时，
         * 默认使用当前月份。
         */
        YearMonth targetMonth;

        if (month == null || month.isBlank()) {

            targetMonth =
                    YearMonth.now();

        } else {

            targetMonth =
                    YearMonth.parse(month);
        }

        return Result.success(
                visualizationService
                        .getOverview(
                                targetMonth
                        )
        );
    }
}