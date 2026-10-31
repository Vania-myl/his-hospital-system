package com.his.mapper;

import com.his.entity.SettleCategory;

import java.util.List;

public interface SettleCategoryMapper {

    List<SettleCategory> selectActive();
}
