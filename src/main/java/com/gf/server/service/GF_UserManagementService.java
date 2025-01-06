package com.gf.server.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gf.server.dto.ReqResDTO;
import com.gf.server.entity.GF_User;
import com.gf.server.enumeration.UserRole;
import com.gf.server.repository.UserRepository;
import com.gf.server.util.JwtUtils;

@Service
public class GF_UserManagementService {

    private static final Logger logger = Logger.getLogger(GF_UserManagementService.class.getName());
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ReqResDTO register(ReqResDTO request) {
        
        int responseStatusCode = 500;
        String responseMessage = null;
        String responseErrorMessage = null;
        GF_User responseUser = null;
        
        try {
            GF_User user = new GF_User();

            user.setEmail(request.email());
            user.setFirstName(request.firstName());
            user.setLastName(request.lastname());
            user.setRole(UserRole.stringToEnum(request.role()));
            user.setPassword(passwordEncoder.encode(request.password()));

            GF_User userDB_Result = userRepository.save(user);

            if (userDB_Result.getId() > 0) {
                responseStatusCode = 200;
                responseMessage = "User saved successfully.";
                responseUser = userDB_Result;

            }
        } catch (Exception e) {
            responseErrorMessage = e.getMessage();
        }

        ReqResDTO response = new ReqResDTO(
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
            responseUser, 
            null
        );

        return response;

    }

    public ReqResDTO login(ReqResDTO request) {
        
        int responseStatusCode = 500;
        String responseErrorMessage = null;
        String responseToken = null;
        String responseRefreshToken = null;
        String responseExpirationTime = null;
        String responseMessage = null;

        try {
            this.authenticationManager
                .authenticate(
                    new UsernamePasswordAuthenticationToken(
                        request.email(), 
                        request.password())
                    );
            var user = userRepository.findByEmail(request.email()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            responseStatusCode = 200;
            responseToken = jwt;
            responseRefreshToken = refreshToken;
            responseExpirationTime = "24Hrs";
            responseMessage = "Successfully logged in.";
    
        } catch (Exception e) {
            responseErrorMessage = e.getMessage();
        }

        ReqResDTO response = new ReqResDTO(
            responseStatusCode, 
            responseErrorMessage, 
            responseMessage, 
            responseToken, 
            responseRefreshToken, 
            responseExpirationTime, 
            null,
            null, 
            null, 
            null, 
            null, 
            null, 
            null
        );

        return response;
    }

    public ReqResDTO refreshToken(ReqResDTO request) {

        int responseStatusCode = 500;
        String responseErrorMessage = null;
        String responseToken = null;
        String responseRefreshToken = null;
        String responseExpirationTime = null;
        String responseMessage = null;

        try {
            String userEmail = jwtUtils.extractUsername(request.token());
            GF_User user = userRepository.findByEmail(userEmail).orElseThrow();

            if (jwtUtils.tokenValid(request.refreshToken(), user)) {
                var jwt = jwtUtils.generateToken(user);

                responseStatusCode = 200;
                responseToken = jwt;
                responseRefreshToken = request.token();
                responseExpirationTime = "24Hrs";
                responseMessage = "Successfully refreshed token.";
            }
        } catch (Exception e) {
            responseErrorMessage = e.getMessage();
        }

        ReqResDTO response = new ReqResDTO(
            responseStatusCode, 
            responseErrorMessage, 
            responseMessage, 
            responseToken, 
            responseRefreshToken, 
            responseExpirationTime, 
            null,
            null, 
            null, 
            null, 
            null, 
            null, 
            null
        );

        return response;
    }
    
    public ReqResDTO getAllUsers() {

        int responseStatusCode = 500;
        String responseErrorMessage = null;
        String responseMessage = null;
        List<GF_User> responseUserList = new ArrayList<GF_User>();

        try {
            List<GF_User> result = userRepository.findAll();

            if (!result.isEmpty()) {
                responseStatusCode = 200;
                responseMessage = "Success.";
                responseUserList = result;
            } else {
                responseMessage = "No users found.";
            }
        } catch (Exception e) {
            responseErrorMessage = e.getMessage();
        }

        ReqResDTO response = new ReqResDTO(
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
            responseUserList
        );

        return response;
    }
    
    public boolean clearUserRepository() {
        boolean retVal = false;

        logger.info("Clearing User repository");

        try {
            this.userRepository.deleteAll();
            retVal = true;
        } catch (Exception e)
        {
            logger.severe(e.getMessage());
        }

        return retVal;
    }

    public Long userCount() {
        return this.userRepository.count();
    }

    public ReqResDTO getUserById(Long id) {
        GF_User responseUser = null;
        int responseStatusCode = 500;
        String responseMessage = null;
        String responseErrorMessage = null;

        try {
            responseUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found."));
            responseStatusCode = 200;
            responseMessage = "User with id " + id + " found successfully.";
        } catch (Exception e) {
            responseErrorMessage = e.getMessage();
        }

        ReqResDTO response = new ReqResDTO(
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
            responseUser, 
            null
        );

        return response;
    }

    public ReqResDTO deleteUserById(Long id) {
        int responseStatusCode = 500;
        String responseMessage = null;
        String responseErrorMessage = null;

        try {
            Optional<GF_User> userOptional = this.userRepository.findById(id);

            if (userOptional.isPresent()) {
                this.userRepository.deleteById(id);

                responseStatusCode = 200;
                responseMessage = "User deleted successfully.";
            } else {
                responseStatusCode = 404;
                responseMessage = "User not found.";
            }
        } catch (Exception e) {
            responseErrorMessage = e.getMessage();
        }

        ReqResDTO response = new ReqResDTO(
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
            null
        );

        return response;
    }

    public ReqResDTO updateUser(Long userId, GF_User updatedUser) {

        int responseStatusCode = 500;
        String responseMessage = null;
        String responseErrorMessage = null;
        GF_User responseUser = null;

        try {
            Optional<GF_User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                GF_User existingUser = userOptional.get();

                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setFirstName(updatedUser.getFirstName());
                existingUser.setLastName(updatedUser.getLastName());
                existingUser.setRole(updatedUser.getRole());

                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                GF_User savedUser = userRepository.save(existingUser);
                responseStatusCode = 200;
                responseUser = savedUser;
                responseMessage = "User updated successfully.";
            } else {
                responseStatusCode = 404;
                responseMessage = "User not found for update.";
            }
        } catch (Exception e) {
            responseStatusCode = 500;
            responseErrorMessage = e.getMessage();
        }

        ReqResDTO response = new ReqResDTO(
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
            responseUser, 
            null
        );

        return response;
    }
}
