package com.his.disposal.service.impl;

import com.his.disposal.dto.DisposalPageVO;
import com.his.disposal.dto.DisposalQueryDTO;
import com.his.disposal.dto.DisposalResultDTO;
import com.his.disposal.service.DisposalRequestService;
import com.his.common.LoginUser;
import com.his.common.ResultCode;
import com.his.common.exception.BusinessException;
import com.his.common.util.UserContext;
import com.his.mapper.DisposalRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisposalRequestServiceImpl implements DisposalRequestService {
    @Autowired
    private DisposalRequestMapper mapper;

    @Override
    public DisposalPageVO list(DisposalQueryDTO query) {
        requireDisposalRole();
        if (query == null) query = new DisposalQueryDTO();
        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int size = query.getSize() == null || query.getSize() < 1 ? 10 : Math.min(query.getSize(), 100);
        query.setPage(page);
        query.setSize(size);
        query.setOffset((page - 1) * size);
        if (query.getDisposalState() != null && !query.getDisposalState().isEmpty()
                && !query.getDisposalState().matches("[1-4]")) {
            throw new BusinessException("处置状态不正确");
        }
        if (query.getCreationDate() != null && !query.getCreationDate().isEmpty()
                && !query.getCreationDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new BusinessException("开立日期格式应为 yyyy-MM-dd");
        }
        return new DisposalPageVO(mapper.selectList(query), mapper.countList(query), page, size);
    }

    @Override
    @Transactional
    public void assign(Integer id) {
        LoginUser user = requireDisposalRole();
        if (id == null || id < 1) throw new BusinessException("申请 ID 不正确");
        if (mapper.assign(id, user.getId()) != 1) {
            throw new BusinessException("处置申请不存在、状态不是已开立，或尚未收费/已退费");
        }
    }

    @Override
    @Transactional
    public void saveResult(Integer id, DisposalResultDTO dto) {
        LoginUser user = requireDisposalRole();
        if (id == null || id < 1) throw new BusinessException("申请 ID 不正确");
        if (dto == null || dto.getDisposalResult() == null || dto.getDisposalResult().trim().isEmpty()) {
            throw new BusinessException("处置结果不能为空");
        }
        String result = dto.getDisposalResult().trim();
        String remark = dto.getDisposalRemark() == null ? null : dto.getDisposalRemark().trim();
        if (result.length() > 512 || (remark != null && remark.length() > 512)) {
            throw new BusinessException("处置结果或备注不能超过 512 字");
        }
        if (mapper.saveResult(id, user.getId(), result, remark) != 1) {
            throw new BusinessException("处置申请不存在或尚未登记、已完成");
        }
    }

    private LoginUser requireDisposalRole() {
        LoginUser user = UserContext.get();
        if (user == null) throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        if (!"treat".equals(user.getRole())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅处置医生可以操作处置申请");
        }
        return user;
    }
}
