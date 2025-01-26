package com.gf.server.controller_tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang3.RandomStringUtils;

import com.gf.server.controller.GF_UserManagementController;
import com.gf.server.dto.ReqResDTO;
import com.gf.server.entity.GF_User;
import com.gf.server.enumeration.UserRole;
import com.gf.server.service.GF_UserManagementService;
import com.google.gson.Gson;

import io.jsonwebtoken.lang.Assert;

@SpringBootTest
@AutoConfigureMockMvc
public class GF_UserManagementControllerTests {
    
    @Autowired
    GF_UserManagementController userManagementController;

    @Autowired
    GF_UserManagementService userManagementService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Gson gson;

    @AfterEach
    void clearUserRepository() {
        userManagementService.clearUserRepository();
    }

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

    @Test
    void restControllerRegistersUsers() throws Exception {

        String adminUserPassword = "p@55w0rd";
        GF_User adminUser = registrationUtil(UserRole.ADMIN, adminUserPassword);

        ReqResDTO adminUserLoginRequest = new ReqResDTO(
            0, 
            null, 
            null, 
            null, 
            null, 
            null,
            null, 
            null, 
            null, 
            adminUser.getEmail(), 
            adminUserPassword, 
            null, 
            null
        );

        String adminUserToken = this.userManagementService.login(adminUserLoginRequest).token();

        ReqResDTO request = new ReqResDTO(
            0, 
            null, 
            null, 
            null, 
            null, 
            null,
            null, 
            null, 
            "Admin", 
            "geoshoouh@gmail.com", 
            "p@55w0rd", 
            null, 
            null
        );

        this.mockMvc.perform(post("/admin/register").header("Authorization", "Bearer " + adminUserToken)
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .content(gson.toJson(request)))
                                                                .andExpect(status().isOk());
    }

    @Test
    void restControllerLogsInUsers() throws Exception {
        String registeredUserPassword = "86p@55w0rd64";
        GF_User registeredUser = this.registrationUtil(UserRole.TRAINER, registeredUserPassword);

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
            registeredUserPassword, 
            null, 
            null
        );

        ReqResDTO response = gson.fromJson(
            this.mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                                                                .content(gson.toJson(request)))
                                                                .andExpect(status().isOk())
                                                                .andReturn()
                                                                .getResponse()
                                                                .getContentAsString(), 
                                                                ReqResDTO.class
        );

        Assert.notNull(response.user().getRole(), "Unexpected role for user in response");
    }

    @Test
    void canPingRestController() throws Exception {
        this.mockMvc.perform(get("/ping")).andExpect(status().isOk());
    }

    @Test
    void adminCanDeleteUser() throws Exception {

        String adminUserPass = "p@$$w0rd";
        GF_User adminUser = registrationUtil(UserRole.ADMIN, adminUserPass);
        GF_User trainerUser = registrationUtil(UserRole.TRAINER);

        ReqResDTO adminLoginRequest = new ReqResDTO(
            0, 
            null, 
            null, 
            null, 
            null, 
            null,
            null, 
            null, 
            null, 
            adminUser.getEmail(), 
            adminUserPass, 
            null, 
            null
        );

        ReqResDTO userDeleteRequest = new ReqResDTO(
            0, 
            null, 
            null, 
            null, 
            null, 
            null,
            null, 
            null, 
            null, 
            trainerUser.getEmail(), 
            null, 
            null, 
            null
        );

        String adminToken = this.userManagementService.login(adminLoginRequest).token();

        gson.fromJson(
            this.mockMvc.perform(post("/admin/user/delete", trainerUser.getEmail()).header("Authorization", "Bearer " + adminToken)
                                                                                               .contentType(MediaType.APPLICATION_JSON)
                                                                                               .content(gson.toJson(userDeleteRequest)))
                                                                                               .andExpect(status().isOk())
                                                                                               .andReturn()
                                                                                               .getResponse()
                                                                                               .getContentAsString(),     
                                                                                               ReqResDTO.class
            );
    }

    @Test
    void trainerCannotDeleteUser() throws Exception {

        String trainerUserPass = "p@$$w0rd";
        GF_User trainerUser = registrationUtil(UserRole.TRAINER, trainerUserPass);

        GF_User user = registrationUtil(UserRole.TRAINER);

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
            trainerUser.getEmail(), 
            trainerUserPass, 
            null, 
            null
        );

        ReqResDTO userDeleteRequest = new ReqResDTO(
            0, 
            null, 
            null, 
            null, 
            null, 
            null,
            null, 
            null, 
            null, 
            user.getEmail(), 
            null, 
            null, 
            null
        );

        String trainerToken = userManagementService.login(request).token();

        gson.fromJson(
            this.mockMvc.perform(post("/admin/user/delete").header("Authorization", "Bearer " + trainerToken)
                                                                       .contentType(MediaType.APPLICATION_JSON)
                                                                       .content(gson.toJson(userDeleteRequest)))
                                                                       .andExpect(status().isForbidden())
                                                                       .andReturn()
                                                                       .getResponse()
                                                                       .getContentAsString(),     
                                                                       ReqResDTO.class
            );
    }

    @Test
    void validateTokenRequestValidatesToken() throws Exception {

        String trainerUserPass = "p@$$w0rd";
        GF_User trainerUser = registrationUtil(UserRole.TRAINER, trainerUserPass);

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
            trainerUser.getEmail(), 
            trainerUserPass, 
            null, 
            null
        );

        String trainerToken = userManagementService.login(request).token();

        gson.fromJson(
            this.mockMvc.perform(get("/auth/token/validate/trainer").header("Authorization", "Bearer " + trainerToken))
                                                                                                 .andExpect(status().isOk())
                                                                                                 .andReturn()
                                                                                                 .getResponse()
                                                                                                 .getContentAsString(),     
                                                                                                 Boolean.class
            );
    }

    @Test
    void validateTokenRejectsBadToken() throws Exception {
        String trainerUserPass = "p@$$w0rd";
        GF_User trainerUser = registrationUtil(UserRole.TRAINER, trainerUserPass);

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
            trainerUser.getEmail(), 
            trainerUserPass, 
            null, 
            null
        );

        String trainerToken = userManagementService.login(request).token();

        gson.fromJson(
            this.mockMvc.perform(get("/auth/token/validate/admin").header("Authorization", "Bearer " + trainerToken))
                                                                                                 .andExpect(status().isOk())
                                                                                                 .andReturn()
                                                                                                 .getResponse()
                                                                                                 .getContentAsString(),     
                                                                                                 Boolean.class
            );
    }
}
