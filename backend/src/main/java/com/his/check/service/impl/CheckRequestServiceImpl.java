package com.his.check.service.impl;

import com.his.check.dto.CheckPageVO;
import com.his.check.dto.CheckQueryDTO;
import com.his.check.dto.CheckResultDTO;
import com.his.check.service.CheckRequestService;
import com.his.common.LoginUser;
import com.his.common.ResultCode;
import com.his.common.exception.BusinessException;
import com.his.common.util.UserContext;
import com.his.mapper.CheckRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckRequestServiceImpl implements CheckRequestService {
    @Autowired
    private CheckRequestMapper mapper;

    @Override
    public CheckPageVO list(CheckQueryDTO query) {
        requireCheckRole();
        if (query == null) query = new CheckQueryDTO();
        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int size = query.getSize() == null || query.getSize() < 1 ? 10 : Math.min(query.getSize(), 100);
        query.setPage(page);
        query.setSize(size);
        query.setOffset((page - 1) * size);
        if (query.getCheckState() != null && !query.getCheckState().isEmpty()
                && !query.getCheckState().matches("[1-4]")) {
            throw new BusinessException("检查状态不正确");
        }
        if (query.getCreationDate() != null && !query.getCreationDate().isEmpty()
                && !query.getCreationDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new BusinessException("开立日期格式应为 yyyy-MM-dd");
        }
        return new CheckPageVO(mapper.selectList(query), mapper.countList(query), page, size);
    }

    @Override
    @Transactional
    public void assign(Integer id) {
        LoginUser user = requireCheckRole();
        if (id == null || id < 1) throw new BusinessException("申请 ID 不正确");
        if (mapper.assign(id, user.getId()) != 1) {
            throw new BusinessException("检查申请不存在、状态不是已开立，或尚未收费/已退费");
        }
    }

    @Override
    @Transactional
    public void saveResult(Integer id, CheckResultDTO dto) {
        LoginUser user = requireCheckRole();
        if (id == null || id < 1) throw new BusinessException("申请 ID 不正确");
        if (dto == null || dto.getCheckResult() == null || dto.getCheckResult().trim().isEmpty()) {
            throw new BusinessException("检查结果不能为空");
        }
        String result = dto.getCheckResult().trim();
        String remark = dto.getCheckRemark() == null ? null : dto.getCheckRemark().trim();
        if (result.length() > 512 || (remark != null && remark.length() > 512)) {
            throw new BusinessException("检查结果或备注不能超过 512 字");
        }
        if (mapper.saveResult(id, user.getId(), result, remark) != 1) {
            throw new BusinessException("检查申请不存在或尚未登记、已完成");
        }
    }

    private LoginUser requireCheckRole() {
        LoginUser user = UserContext.get();
        if (user == null) throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        if (!"check".equals(user.getRole())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅检查医生可以操作检查申请");
        }
        return user;
    }
}
