package com.barber.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barber.entities.Status;

public interface StatusRepository extends JpaRepository<Status, Long> {

}
