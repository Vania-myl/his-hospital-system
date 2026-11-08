package com.his.mapper;

import com.his.inspection.dto.InspectionQueryDTO;
import com.his.inspection.dto.InspectionRequestVO;
import com.his.entity.InspectionRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InspectionRequestMapper {
    int insert(InspectionRequest request);
    List<InspectionRequestVO> selectList(InspectionQueryDTO query);
    int countList(InspectionQueryDTO query);
    InspectionRequest selectById(Integer id);
    int assign(@Param("id") Integer id, @Param("employeeId") Integer employeeId);
    int saveResult(@Param("id") Integer id, @Param("employeeId") Integer employeeId,
                   @Param("result") String result, @Param("remark") String remark);
}
