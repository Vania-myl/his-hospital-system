package com.his.common;

/**
 * 状态码与业务错误码
 */
public interface ResultCode {

    int SUCCESS = 200;
    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int FORBIDDEN = 403;
    int INTERNAL_ERROR = 500;

    // 业务错误码
    int QUOTA_FULL = 1001;       // 该医生号源已满
    int CANNOT_CANCEL = 1002;    // 患者已就诊，不可退号
    int CANNOT_REFUND = 1003;    // 项目已执行 / 已取药，不可退费
    int LOGIN_FAILED = 1004;     // 用户名或密码错误
}
