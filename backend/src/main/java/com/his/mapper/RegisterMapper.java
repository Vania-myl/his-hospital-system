package com.his.mapper;

import com.his.entity.Register;
import com.his.register.dto.RegisterQueryDTO;
import com.his.statistics.dto.DeptCountVO;
import com.his.statistics.dto.StatisticsQueryDTO;
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

    /** 统计：挂号量 */
    int countRegister(StatisticsQueryDTO query);

    /** 统计：退号量 */
    int countRegisterRefund(StatisticsQueryDTO query);

    /** 统计：各科室就诊人次分布 */
    List<DeptCountVO> countGroupByDept(StatisticsQueryDTO query);
}
