package com.gf.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDTO(
    int statusCode,
    String email,
    String lastName,
    String firstName,
    String role
) {}
