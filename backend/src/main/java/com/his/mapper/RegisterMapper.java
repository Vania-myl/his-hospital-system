package com.his.mapper;

import com.his.entity.Register;
import com.his.register.dto.RegisterQueryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RegisterMapper {

    int insert(Register register);

    Register selectById(Integer id);

    Register selectLatestByCaseNumber(String caseNumber);

    Register selectLatestByCardNumber(String cardNumber);

    List<Register> selectList(RegisterQueryDTO query);

    int countList(RegisterQueryDTO query);

    int updateVisitState(@Param("id") Integer id, @Param("visitState") Integer visitState);
}
