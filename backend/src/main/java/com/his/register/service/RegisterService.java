package com.his.register.service;

import com.his.entity.Register;
import com.his.register.dto.RegisterDTO;
import com.his.register.dto.RegisterQueryDTO;

import java.util.List;

public interface RegisterService {

    /** 挂号：创建挂号记录并产生挂号费流水 */
    Register register(RegisterDTO dto);

    /** 挂号列表查询 */
    List<Register> list(RegisterQueryDTO query);

    /** 挂号列表总数（分页用） */
    int count(RegisterQueryDTO query);

    /** 退号：挂号状态置为已退号，并产生退费流水 */
    void refund(Integer registerId);
}
