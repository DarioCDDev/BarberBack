package com.barber.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barber.entities.Service;

public interface ServiceRepository extends JpaRepository<Service, Long> {

}
