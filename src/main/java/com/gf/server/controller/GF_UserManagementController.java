package com.gf.server.controller;

import javax.security.auth.login.FailedLoginException;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.gf.server.dto.ReqResDTO;
import com.gf.server.entity.GF_User;
import com.gf.server.service.GF_UserManagementService;

import jakarta.security.auth.message.AuthException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class GF_UserManagementController {

    @Autowired
    private GF_UserManagementService userManagementService;

    @PostMapping("/admin/register")
    public ResponseEntity<ReqResDTO> register(@RequestBody ReqResDTO request) {
        return ResponseEntity.ok(this.userManagementService.register(request));
    }
    
    @PostMapping("/auth/login")
    @ResponseBody
    public ResponseEntity<ReqResDTO> login(@RequestBody ReqResDTO request) throws FailedLoginException {
        return ResponseEntity.ok(this.userManagementService.login(request));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqResDTO> refreshToken(@RequestBody ReqResDTO request) throws AuthException {
        return ResponseEntity.ok(this.userManagementService.refreshToken(request));
    }

    @GetMapping("/admin/user/get/all")
    public ResponseEntity<ReqResDTO> getAllUsers() {
        return ResponseEntity.ok(this.userManagementService.getAllUsers());
    }

    @PostMapping("/admin/user/delete")
    public ResponseEntity<ReqResDTO> deleteUser(@RequestBody ReqResDTO request) throws BadRequestException {
        return ResponseEntity.ok(this.userManagementService.deleteUserByEmail(request.email()));
    }

    @GetMapping("/ping")
    public ResponseEntity<String> pingAuthenticationServer() {
        return ResponseEntity.ok("Genesis Fitness User Authentication Server is HEALTHY.\n");
    }

    @GetMapping("/auth/token/validate/admin")
    public ResponseEntity<Boolean> validateTokenAdmin() {
        return ResponseEntity.ok(true);
    }

    @GetMapping("/auth/token/validate/trainer")
    public ResponseEntity<Boolean> validateTokenTrainer() {
        return ResponseEntity.ok(true);
    }

    @GetMapping("/auth/token/validate/client")
    public ResponseEntity<Boolean> validateTokenClient() {
        return ResponseEntity.ok(true);
    }

    @PostMapping("/trainer/user/update/password")
    public ResponseEntity<Boolean> updateTrainerUser(@RequestBody ReqResDTO request) {

        ResponseEntity<Boolean> response = ResponseEntity.ok(true);

        try {
            this.userManagementService.login(request);

            GF_User user = this.userManagementService.getUserByEmail(request.email()).user();

            user.setPassword(request.newPassword());

            this.userManagementService.updateUser(request.email(), user);
        } catch (FailedLoginException e) {
            response = ResponseEntity.status(403).body(false);
        }

        return response;
    }
}
