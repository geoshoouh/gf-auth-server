package com.gf.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gf.server.entity.GF_User;

public interface UserRepository extends JpaRepository<GF_User, Long> {
    Optional<GF_User> findByEmail(String email);
}
