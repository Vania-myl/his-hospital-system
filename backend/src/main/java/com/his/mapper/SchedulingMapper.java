package com.his.mapper;

import com.his.entity.Scheduling;

import java.util.List;

public interface SchedulingMapper {

    List<Scheduling> selectActive();
}
