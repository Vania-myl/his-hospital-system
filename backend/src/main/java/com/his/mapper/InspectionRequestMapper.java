package com.his.mapper;

import com.his.statistics.dto.StatisticsQueryDTO;

public interface InspectionRequestMapper {

    /** 统计：检验例数 */
    int countAll(StatisticsQueryDTO query);
}
