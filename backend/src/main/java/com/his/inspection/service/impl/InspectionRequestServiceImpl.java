package com.his.inspection.service.impl;

import com.his.inspection.dto.InspectionPageVO;
import com.his.inspection.dto.InspectionQueryDTO;
import com.his.inspection.dto.InspectionResultDTO;
import com.his.inspection.service.InspectionRequestService;
import com.his.common.LoginUser;
import com.his.common.ResultCode;
import com.his.common.exception.BusinessException;
import com.his.common.util.UserContext;
import com.his.mapper.InspectionRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InspectionRequestServiceImpl implements InspectionRequestService {
    @Autowired
    private InspectionRequestMapper mapper;

    @Override
    public InspectionPageVO list(InspectionQueryDTO query) {
        requireInspectionRole();
        if (query == null) query = new InspectionQueryDTO();
        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int size = query.getSize() == null || query.getSize() < 1 ? 10 : Math.min(query.getSize(), 100);
        query.setPage(page);
        query.setSize(size);
        query.setOffset((page - 1) * size);
        if (query.getInspectionState() != null && !query.getInspectionState().isEmpty()
                && !query.getInspectionState().matches("[1-4]")) {
            throw new BusinessException("检验状态不正确");
        }
        if (query.getCreationDate() != null && !query.getCreationDate().isEmpty()
                && !query.getCreationDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new BusinessException("开立日期格式应为 yyyy-MM-dd");
        }
        return new InspectionPageVO(mapper.selectList(query), mapper.countList(query), page, size);
    }

    @Override
    @Transactional
    public void assign(Integer id) {
        LoginUser user = requireInspectionRole();
        if (id == null || id < 1) throw new BusinessException("申请 ID 不正确");
        if (mapper.assign(id, user.getId()) != 1) {
            throw new BusinessException("检验申请不存在、状态不是已开立，或尚未收费/已退费");
        }
    }

    @Override
    @Transactional
    public void saveResult(Integer id, InspectionResultDTO dto) {
        LoginUser user = requireInspectionRole();
        if (id == null || id < 1) throw new BusinessException("申请 ID 不正确");
        if (dto == null || dto.getInspectionResult() == null || dto.getInspectionResult().trim().isEmpty()) {
            throw new BusinessException("检验结果不能为空");
        }
        String result = dto.getInspectionResult().trim();
        String remark = dto.getInspectionRemark() == null ? null : dto.getInspectionRemark().trim();
        if (result.length() > 512 || (remark != null && remark.length() > 512)) {
            throw new BusinessException("检验结果或备注不能超过 512 字");
        }
        if (mapper.saveResult(id, user.getId(), result, remark) != 1) {
            throw new BusinessException("检验申请不存在或尚未登记、已完成");
        }
    }

    private LoginUser requireInspectionRole() {
        LoginUser user = UserContext.get();
        if (user == null) throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        if (!"lab".equals(user.getRole())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅检验医生可以操作检验申请");
        }
        return user;
    }
}
