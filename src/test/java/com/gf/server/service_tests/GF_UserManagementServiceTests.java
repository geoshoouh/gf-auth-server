package com.gf.server.service_tests;

import javax.security.auth.login.FailedLoginException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gf.server.dto.ReqResDTO;
import com.gf.server.entity.GF_User;
import com.gf.server.enumeration.UserRole;
import com.gf.server.service.GF_UserManagementService;

import io.jsonwebtoken.lang.Assert;
import jakarta.security.auth.message.AuthException;

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
    void registeredAdminHasCorrectAuthorities() {

        GF_User registeredAdmin = this.registrationUtil(UserRole.ADMIN);

        Assert.eq(registeredAdmin.getAuthorities().size(), 1, "Expected size of auth array to be 1, was " +registeredAdmin.getAuthorities().size());
        Assert.isTrue(registeredAdmin.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN")));
    }

    @Test  
    void registeredUserCanLogin() throws FailedLoginException {

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
    void userCanRefreshTokenWork() throws FailedLoginException, AuthException {

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

    @Test
    void deleteUserByIdDeletesUser() {

        Assert.eq(this.userManagementService.userCount(), 0L, "User count was " + this.userManagementService.userCount() + ", expected " + 0);

        GF_User registeredUser = this.registrationUtil(UserRole.TRAINER);

        Assert.eq(this.userManagementService.userCount(), 1L, "User count was " + this.userManagementService.userCount() + ", expected " + 1);

        ReqResDTO response = this.userManagementService.deleteUserById(registeredUser.getId());

        Assert.eq(response.statusCode(), 200, "Status code was " + response.statusCode() + ", expected " + 200);
        Assert.eq(this.userManagementService.userCount(), 0L, "User count was " + this.userManagementService.userCount() + ", expected " + 0);
    }

    @Test
    void updateUserUpdatesUser() {
        GF_User registeredUser = this.registrationUtil(UserRole.TRAINER);

        GF_User updatedUser = new GF_User();

        String updatedEmail = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".com";
        String updatedFirstName = RandomStringUtils.randomAlphabetic(5);
        String updatedLastName = RandomStringUtils.randomAlphabetic(5);
        UserRole updatedRole = UserRole.ADMIN;
        String updatedPassword = RandomStringUtils.randomAlphanumeric(15);

        String existingPassword = registeredUser.getPassword();

        updatedUser.setEmail(updatedEmail);
        updatedUser.setFirstName(updatedFirstName);
        updatedUser.setLastName(updatedLastName);
        updatedUser.setRole(updatedRole);
        updatedUser.setPassword(updatedPassword);

        ReqResDTO response = this.userManagementService.updateUser(registeredUser.getId(), updatedUser);

        Assert.eq(response.statusCode(), 200, "Status code was " + response.statusCode() + ", expected " + 200);
        Assert.eq(response.user().getEmail(), updatedEmail, "Expected " + updatedEmail + ", was " + response.user().getEmail());
        Assert.eq(response.user().getFirstName(), updatedFirstName, "Expected " + updatedFirstName + ", was " + response.user().getFirstName());
        Assert.eq(response.user().getLastName(), updatedLastName, "Expected " + updatedLastName + ", was " + response.user().getLastName());
        Assert.eq(response.user().getRole(), updatedRole, "Expected " + updatedRole + ", was " + response.user().getRole());
        Assert.isTrue(response.user().getPassword() != existingPassword);
    }

    @Test
    void deleteUserByEmailDeletesUser() throws BadRequestException {
        GF_User registeredUser = this.registrationUtil(UserRole.TRAINER);

        Assert.eq(this.userManagementService.userCount(), 1L, "Expect user count 1; was " + this.userManagementService.userCount());

        this.userManagementService.deleteUserByEmail(registeredUser.getEmail());

        Assert.eq(this.userManagementService.userCount(), 0L, "Expect user count 0; was " + this.userManagementService.userCount());
    }
}