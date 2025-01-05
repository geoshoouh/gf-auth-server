package com.gf.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gf.server.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
