package com.gf.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gf.server.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
