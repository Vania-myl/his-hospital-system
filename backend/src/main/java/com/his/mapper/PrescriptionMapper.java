package com.his.mapper;

import com.his.statistics.dto.StatisticsQueryDTO;

public interface PrescriptionMapper {

    /** 统计：处方量（开立） */
    int countAll(StatisticsQueryDTO query);

    /** 统计：发药量（已发药） */
    int countDispensed(StatisticsQueryDTO query);
}
