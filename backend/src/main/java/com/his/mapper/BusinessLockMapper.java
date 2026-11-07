package com.his.mapper;

import com.his.entity.Register;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface BusinessLockMapper {
  @Select("SELECT * FROM register WHERE id=#{id} FOR UPDATE")
  Register register(Integer id);

  Integer item(@Param("type") String type, @Param("id") Integer id);

  @Update("UPDATE register SET visit_state=#{next} WHERE id=#{id} AND visit_state=#{expected}")
  int visit(@Param("id") Integer id, @Param("expected") int expected, @Param("next") int next);
}
