package com.his.base.service;

import com.his.base.dto.LoginDTO;
import com.his.base.dto.LoginVO;
import com.his.common.LoginUser;

public interface AuthService {

    LoginVO login(LoginDTO dto);

    LoginUser currentUser();
}
