package com.barber.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barber.entities.Status;
import com.barber.repository.StatusRepository;

@Service
public class StatusService {

	@Autowired
	StatusRepository statusRepository;

	public Status createStatus(Status status) {
		System.out.println(status.toString());
		try {
			return statusRepository.save(status);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Status> getAllStatus() {
		try {
			return statusRepository.findAll();
		} catch (Exception e) {
			throw e;
		}
	}

}
