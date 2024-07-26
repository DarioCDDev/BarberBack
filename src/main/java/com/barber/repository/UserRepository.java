package com.barber.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barber.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Optional<User> findOneByEmail(String email);

}
