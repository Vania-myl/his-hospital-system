package com.his.common.util;

import com.his.common.LoginUser;
import com.his.common.exception.BusinessException;
import java.time.LocalDate;

public final class RoleAccess {
  private RoleAccess() {}

  public static LoginUser role(String... roles) {
    LoginUser u = UserContext.get();
    if (u == null) throw new BusinessException(401, "请先登录");
    for (String role : roles) if (role.equals(u.getRole())) return u;
    throw new BusinessException(403, "当前岗位无权执行此操作");
  }

  public static String text(String value, String label, int max, boolean required) {
    String s = value == null ? "" : value.trim();
    if (required && s.isEmpty()) throw new BusinessException(label + "不能为空");
    if (s.length() > max) throw new BusinessException(label + "不能超过" + max + "字");
    return s;
  }

  public static void date(String date) {
    if (date != null && !date.isEmpty()) {
      try {
        LocalDate.parse(date);
      } catch (Exception e) {
        throw new BusinessException("日期格式应为 yyyy-MM-dd");
      }
    }
  }
}
