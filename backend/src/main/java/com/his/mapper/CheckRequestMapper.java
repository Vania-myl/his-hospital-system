package com.his.mapper;

import com.his.check.dto.CheckQueryDTO;
import com.his.check.dto.CheckRequestVO;
import com.his.entity.CheckRequest;
import com.his.statistics.dto.StatisticsQueryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CheckRequestMapper {
    int insert(CheckRequest request);
    List<CheckRequestVO> selectList(CheckQueryDTO query);
    int countList(CheckQueryDTO query);
    CheckRequest selectById(Integer id);
    int assign(@Param("id") Integer id, @Param("employeeId") Integer employeeId);
    int saveResult(@Param("id") Integer id, @Param("employeeId") Integer employeeId,
                   @Param("result") String result, @Param("remark") String remark);
    /** 统计：检查例数 */
    int countAll(StatisticsQueryDTO query);
}
