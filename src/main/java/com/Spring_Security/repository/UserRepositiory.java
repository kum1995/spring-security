package com.Spring_Security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Spring_Security.entity.User;

public interface UserRepositiory extends JpaRepository<User, Long> {
	
	Optional<User> getByUsername(String username);

}
