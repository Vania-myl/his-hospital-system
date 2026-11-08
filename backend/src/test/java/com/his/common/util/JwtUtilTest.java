package com.his.common.util;

import com.his.common.LoginUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtUtilTest {
    @Test
    void tokenCanBeCreatedAndParsedOnCurrentJdk() {
        LoginUser source = new LoginUser();
        source.setId(5);
        source.setUsername("check01");
        source.setRealname("检查医生");
        source.setRole("check");

        LoginUser parsed = JwtUtil.parseToken(JwtUtil.generateToken(source));

        assertEquals(source.getId(), parsed.getId());
        assertEquals(source.getUsername(), parsed.getUsername());
        assertEquals(source.getRealname(), parsed.getRealname());
        assertEquals(source.getRole(), parsed.getRole());
    }
}
