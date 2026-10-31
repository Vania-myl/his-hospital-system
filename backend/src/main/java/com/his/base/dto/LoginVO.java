package com.his.base.dto;

import com.his.common.LoginUser;
import lombok.Data;

/**
 * 登录返回体：token + 用户信息
 */
@Data
public class LoginVO {
    private String token;
    private LoginUser user;
}
