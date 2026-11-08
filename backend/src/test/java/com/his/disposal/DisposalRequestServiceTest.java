package com.his.disposal;

import com.his.disposal.dto.DisposalResultDTO;
import com.his.disposal.service.impl.DisposalRequestServiceImpl;
import com.his.common.LoginUser;
import com.his.common.ResultCode;
import com.his.common.exception.BusinessException;
import com.his.common.util.UserContext;
import com.his.mapper.DisposalRequestMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DisposalRequestServiceTest {
    private final DisposalRequestMapper mapper = mock(DisposalRequestMapper.class);
    private final DisposalRequestServiceImpl service = new DisposalRequestServiceImpl();

    DisposalRequestServiceTest() {
        ReflectionTestUtils.setField(service, "mapper", mapper);
    }

    @AfterEach
    void clearUser() {
        UserContext.clear();
    }

    @Test
    void assignRecordsLoggedInOperatorAndRejectsRepeatedTransition() {
        login("treat");
        when(mapper.assign(9, 5)).thenReturn(1, 0);
        service.assign(9);
        assertThrows(BusinessException.class, () -> service.assign(9));
        verify(mapper, times(2)).assign(9, 5);
    }

    @Test
    void resultRequiresContentAndExecutedState() {
        login("treat");
        DisposalResultDTO dto = new DisposalResultDTO();
        dto.setDisposalResult("  正常  ");
        dto.setDisposalRemark("备注");
        when(mapper.saveResult(eq(9), eq(5), eq("正常"), anyString())).thenReturn(0);
        assertThrows(BusinessException.class, () -> service.saveResult(9, dto));
        verify(mapper).saveResult(9, 5, "正常", "备注");
        dto.setDisposalResult("  ");
        assertThrows(BusinessException.class, () -> service.saveResult(9, dto));
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void otherRoleCannotReadOrWrite() {
        login("doctor");
        BusinessException ex = assertThrows(BusinessException.class, () -> service.list(null));
        assertEquals(ResultCode.FORBIDDEN, ex.getCode());
        assertThrows(BusinessException.class, () -> service.assign(9));
        verifyNoInteractions(mapper);
    }

    private void login(String role) {
        LoginUser user = new LoginUser();
        user.setId(5);
        user.setRole(role);
        UserContext.set(user);
    }
}
