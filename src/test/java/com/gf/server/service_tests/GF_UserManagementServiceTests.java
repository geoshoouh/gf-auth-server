package com.gf.server.service_tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gf.server.dto.ReqResDTO;
import com.gf.server.service.GF_UserManagementService;

import io.jsonwebtoken.lang.Assert;

@SpringBootTest
class GF_UserManagementServiceTests {

	@Autowired
    GF_UserManagementService userManagementService;

    
    @AfterEach
    void clearUserRepository() {
        userManagementService.clearUserRepository();
    }
    
    @Test
    void userManagementCanRegisterUser() {
        ReqResDTO request = new ReqResDTO(
            0, 
            null, 
            null, 
            null, 
            null, 
            null, 
            "Tate",
            "Joshua", 
            null, 
            "Admin", 
            "geoshoouh@gmail.com", 
            "p@55w0rd", 
            null, 
            null
        );

        Assert.isTrue(this.userManagementService.userCount() == 0);

        ReqResDTO response = this.userManagementService.register(request);

        Assert.isTrue(response.statusCode() == 200, "Reponse code was " + response.statusCode());
        Assert.isTrue(this.userManagementService.userCount() == 1);
    }

}