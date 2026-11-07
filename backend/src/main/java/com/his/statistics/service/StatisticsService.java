package com.his.statistics.service;

import com.his.statistics.dto.StatisticsOverviewDTO;
import com.his.statistics.dto.StatisticsQueryDTO;

public interface StatisticsService {

    /** 业务数据统计总览 */
    StatisticsOverviewDTO overview(StatisticsQueryDTO query);
}
