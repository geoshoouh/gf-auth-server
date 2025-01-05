package com.gf.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gf.server.dto.ReqResDTO;
import com.gf.server.entity.User;
import com.gf.server.enumeration.UserRole;
import com.gf.server.repository.UserRepository;
import com.gf.server.util.JwtUtils;

@Service
public class GF_UserManagementService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ReqResDTO register(ReqResDTO request) {

        ReqResDTO response;
        
        int responseStatusCode = 500;
        String responseMessage = null;
        String responseErrorMessage = null;
        User responseUser = null;
        
        try {
            User user = new User();

            user.setEmail(request.email());
            user.setFirstName(request.firstName());
            user.setLastName(request.lastname());
            user.setRole(UserRole.stringToEnum(request.role()));
            user.setPassword(passwordEncoder.encode(request.password()));

            User userDB_Result = userRepository.save(user);

            if (userDB_Result.getId() > 0) {
                responseStatusCode = 200;
                responseMessage = "User saved successfully.";
                responseUser = userDB_Result;

            }
        } catch (Exception e) {
            responseErrorMessage = e.getMessage();
        }

        response = new ReqResDTO(
            responseStatusCode, 
            responseErrorMessage, 
            responseMessage, 
            null, 
            null, 
            null, 
            null,
            null, 
            null, 
            null, 
            null, 
            null, 
            responseUser, 
            null
        );

        return response;

    }
}
