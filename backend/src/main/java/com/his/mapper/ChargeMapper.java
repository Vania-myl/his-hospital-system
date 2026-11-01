package com.his.mapper;

import com.his.register.dto.ChargeItemVO;

import java.util.List;

/**
 * 收费模块只读查询（待收费项目）
 */
public interface ChargeMapper {

    List<ChargeItemVO> selectPendingPrescription(Integer registerId);

    List<ChargeItemVO> selectPendingCheck(Integer registerId);

    List<ChargeItemVO> selectPendingInspection(Integer registerId);

    List<ChargeItemVO> selectPendingDisposal(Integer registerId);
}
