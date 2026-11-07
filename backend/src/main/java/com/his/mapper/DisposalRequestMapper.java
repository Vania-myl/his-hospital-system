package com.his.mapper;

import com.his.statistics.dto.StatisticsQueryDTO;

public interface DisposalRequestMapper {

    /** 统计：处置例数 */
    int countAll(StatisticsQueryDTO query);
}
