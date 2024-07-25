package com.barber.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barber.entities.Rol;
import com.barber.repository.RolRepository;

@Service
public class RolService {

	@Autowired
	RolRepository rolRepository;

	public Rol createRol(Rol rol) {
		try {
			return rolRepository.save(rol);
		} catch (Exception e) {
			throw e;
		}

	}
	
	public List<Rol> getAllRoles(){
		return rolRepository.findAll();
	}

}
