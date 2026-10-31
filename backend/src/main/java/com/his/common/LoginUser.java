package com.his.common;

import lombok.Data;

/**
 * 当前登录用户信息（JWT 载荷 + ThreadLocal 上下文共用）
 */
@Data
public class LoginUser {
    private Integer id;
    private String username;
    private String realname;
    private String role;
}
