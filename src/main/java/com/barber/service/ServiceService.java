package com.barber.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.barber.entities.Service;
import com.barber.repository.ServiceRepository;

@org.springframework.stereotype.Service
public class ServiceService {
	
	@Autowired
	ServiceRepository serviceRepository;

	public Service creatService(Service service) {
		try {
			return serviceRepository.save(service);
		} catch (Exception e) {
			throw e;
		}

	}
	
	public List<Service> getAllServices(){
		return serviceRepository.findAll();
	}


}
