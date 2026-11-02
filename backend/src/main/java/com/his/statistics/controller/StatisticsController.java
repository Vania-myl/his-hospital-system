package com.his.statistics.controller;

import com.his.common.Result;
import com.his.statistics.dto.StatisticsOverviewDTO;
import com.his.statistics.dto.StatisticsQueryDTO;
import com.his.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /** 业务数据统计总览 */
    @GetMapping("/overview")
    public Result<StatisticsOverviewDTO> overview(StatisticsQueryDTO query) {
        return Result.success(statisticsService.overview(query));
    }
}
