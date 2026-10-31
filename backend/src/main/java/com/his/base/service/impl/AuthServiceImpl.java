package com.his.base.service.impl;

import com.his.base.dto.LoginDTO;
import com.his.base.dto.LoginVO;
import com.his.base.service.AuthService;
import com.his.common.LoginUser;
import com.his.common.ResultCode;
import com.his.common.exception.BusinessException;
import com.his.common.util.JwtUtil;
import com.his.common.util.UserContext;
import com.his.entity.Employee;
import com.his.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public LoginVO login(LoginDTO dto) {
        Employee employee = employeeMapper.selectByUsername(dto.getUsername());
        // 密码校验：当前为明文比对，后续升级为 BCrypt 哈希
        if (employee == null || !employee.getPassword().equals(dto.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED, "用户名或密码错误");
        }
        LoginUser user = new LoginUser();
        user.setId(employee.getId());
        user.setUsername(employee.getUsername());
        user.setRealname(employee.getRealname());
        user.setRole(employee.getRole());

        LoginVO vo = new LoginVO();
        vo.setToken(JwtUtil.generateToken(user));
        vo.setUser(user);
        return vo;
    }

    @Override
    public LoginUser currentUser() {
        return UserContext.get();
    }
}
