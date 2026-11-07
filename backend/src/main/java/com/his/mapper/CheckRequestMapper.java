package com.his.mapper;

import com.his.statistics.dto.StatisticsQueryDTO;

public interface CheckRequestMapper {

    /** 统计：检查例数 */
    int countAll(StatisticsQueryDTO query);
}
