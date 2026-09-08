package com.gsz.empvis.service;

import com.gsz.empvis.vo.visualization.VisualizationOverviewVO;

import java.time.YearMonth;

public interface VisualizationService {

    VisualizationOverviewVO getOverview(YearMonth month);
}