package com.gf.server.controller;

import javax.security.auth.login.FailedLoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.gf.server.dto.ReqResDTO;
import com.gf.server.service.GF_UserManagementService;

import jakarta.security.auth.message.AuthException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
public class GF_UserManagementController {

    @Autowired
    private GF_UserManagementService userManagementService;

    @PostMapping("/auth/register")
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

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqResDTO> getAllUsers() {
        return ResponseEntity.ok(this.userManagementService.getAllUsers());
    }

    @GetMapping("/admin/get-user/{userId}")
    public ResponseEntity<ReqResDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(this.userManagementService.getUserById(userId));
    }
    
    @GetMapping("/admin/update/{userId}")
    public ResponseEntity<ReqResDTO> updateUser(@PathVariable Long userId, @RequestBody ReqResDTO body) {
        return ResponseEntity.ok(this.userManagementService.updateUser(userId, body.user()));
    }

    @PostMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqResDTO> deleteUser(@PathVariable Long userId, @RequestBody ReqResDTO body) {
        return ResponseEntity.ok(this.userManagementService.deleteUserById(userId));
    }

    @GetMapping("/ping")
    public ResponseEntity<String> pingAuthenticationServer() {
        return ResponseEntity.ok("Genesis Fitness User Authentication Server is HEALTHY.\n");
    }
}
