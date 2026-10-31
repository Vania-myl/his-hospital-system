package com.his.base.dto;

import lombok.Data;

/**
 * 登录请求体
 */
@Data
public class LoginDTO {
    private String username;
    private String password;
}
