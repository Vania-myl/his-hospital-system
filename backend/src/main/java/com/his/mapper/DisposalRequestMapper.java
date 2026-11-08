package com.his.mapper;

import com.his.disposal.dto.DisposalQueryDTO;
import com.his.disposal.dto.DisposalRequestVO;
import com.his.entity.DisposalRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DisposalRequestMapper {
    int insert(DisposalRequest request);
    List<DisposalRequestVO> selectList(DisposalQueryDTO query);
    int countList(DisposalQueryDTO query);
    DisposalRequest selectById(Integer id);
    int assign(@Param("id") Integer id, @Param("employeeId") Integer employeeId);
    int saveResult(@Param("id") Integer id, @Param("employeeId") Integer employeeId,
                   @Param("result") String result, @Param("remark") String remark);
}
