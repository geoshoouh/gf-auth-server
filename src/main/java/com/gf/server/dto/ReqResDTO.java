package com.gf.server.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gf.server.entity.GF_User;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReqResDTO (
    int statusCode,
    String error,
    String message,
    String token,
    String refreshToken,
    String expirationTime,
    String lastname,
    String firstName,
    String role,
    String email,
    String password,
    GF_User user,
    List<GF_User> userList
) {}
