package com.his.common.interceptor;

import com.his.common.LoginUser;
import com.his.common.ResultCode;
import com.his.common.exception.BusinessException;
import com.his.common.util.JwtUtil;
import com.his.common.util.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录鉴权拦截器：校验 Authorization: Bearer <token>
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录或 token 缺失");
        }
        String token = auth.substring(7);
        try {
            LoginUser user = JwtUtil.parseToken(token);
            UserContext.set(user);
            return true;
        } catch (Exception e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "token 无效或已过期");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
