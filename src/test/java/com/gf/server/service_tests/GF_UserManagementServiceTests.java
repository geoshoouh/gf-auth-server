package com.gf.server.service_tests;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gf.server.dto.ReqResDTO;
import com.gf.server.entity.GF_User;
import com.gf.server.enumeration.UserRole;
import com.gf.server.service.GF_UserManagementService;

import io.jsonwebtoken.lang.Assert;

@SpringBootTest
class GF_UserManagementServiceTests {

	@Autowired
    GF_UserManagementService userManagementService;

    private GF_User registrationUtil(UserRole role) {
        return this.registrationUtil(role, RandomStringUtils.randomAlphanumeric(15));
    }

    private GF_User registrationUtil(UserRole role, String password) {

        String roleString;

        switch (role) {
        case ADMIN:
            roleString = "Admin";
            break;
        case TRAINER:
            roleString = "Trainer";
            break;
        case CLIENT:
            roleString = "Client";
            break;
        default:
            roleString = "Client";
        }

        ReqResDTO request = new ReqResDTO(
            0, 
            null, 
            null, 
            null, 
            null, 
            null, 
            RandomStringUtils.randomAlphabetic(7),
            RandomStringUtils.randomAlphabetic(7), 
            null, 
            roleString, 
            RandomStringUtils.randomAlphanumeric(7) + "@" + RandomStringUtils.randomAlphabetic(4) + ".com", 
            password, 
            null, 
            null
        );

        ReqResDTO response = this.userManagementService.register(request);

        return response.user();
    }
    
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

        Assert.eq(response.statusCode(), 200, "Expected status code 200, was " + response.statusCode());
        Assert.isTrue(this.userManagementService.userCount() == 1);
    }

    @Test  
    void registeredUserCanLogin() {

        String password = "my-secure-password";
        GF_User registeredUser = this.registrationUtil(UserRole.TRAINER, password);

        ReqResDTO request = new ReqResDTO(
            0, 
            null, 
            null, 
            null, 
            null, 
            null, 
            null,
            null, 
            null, 
            null, 
            registeredUser.getEmail(), 
            password, 
            null, 
            null
        );

        ReqResDTO response = this.userManagementService.login(request);

        Assert.eq(response.statusCode(), 200, "Expected status code 200, was " + response.statusCode());
        Assert.notNull(response.token());
        Assert.notNull(response.refreshToken());
    }

    @Test
    void userCanRefreshTokenWork() {

        String password = "my-secure-password";

        GF_User registeredUser = this.registrationUtil(UserRole.TRAINER, password);

        ReqResDTO loginRequest = new ReqResDTO(
            0, 
            null, 
            null, 
            null, 
            null, 
            null, 
            null,
            null, 
            null, 
            null, 
            registeredUser.getEmail(), 
            password, 
            null, 
            null
        );

        ReqResDTO loginResponse = this.userManagementService.login(loginRequest);

        ReqResDTO refreshTokenRequest = new ReqResDTO(
            0, 
            null, 
            null, 
            loginResponse.token(), 
            loginResponse.refreshToken(), 
            null, 
            null,
            null, 
            null, 
            null, 
            null, 
            null, 
            null, 
            null
        );

        ReqResDTO refreshTokenResponse = this.userManagementService.refreshToken(refreshTokenRequest);

        Assert.eq(refreshTokenResponse.statusCode(), 200, "Expected 200, was " + refreshTokenResponse.statusCode());
        Assert.notNull(refreshTokenResponse.token());
        Assert.notNull(refreshTokenResponse.refreshToken());
    }

    @Test  
    void getUserByIdGetsUserById() {
        GF_User registeredUser = this.registrationUtil(UserRole.TRAINER);

        GF_User foundUser = this.userManagementService.getUserById(registeredUser.getId()).user();

        Assert.notNull(foundUser);
        Assert.eq(registeredUser.getId(), foundUser.getId(), "Registered: " + registeredUser.getId() + "; Found: " + foundUser.getId());
    }
}