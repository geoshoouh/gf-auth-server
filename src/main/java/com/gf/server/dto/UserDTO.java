package com.gf.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDTO(
    String email,
    String lastName,
    String firstName,
    String role
) {}
