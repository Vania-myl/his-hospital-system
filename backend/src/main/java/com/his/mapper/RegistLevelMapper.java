package com.his.mapper;

import com.his.entity.RegistLevel;

import java.util.List;

public interface RegistLevelMapper {

    List<RegistLevel> selectActive();

    RegistLevel selectById(Integer id);
}
