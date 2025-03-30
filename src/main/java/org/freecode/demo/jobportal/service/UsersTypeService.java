package org.freecode.demo.jobportal.service;

import java.util.List;

import org.freecode.demo.jobportal.entity.UsersType;
import org.freecode.demo.jobportal.repository.UsersTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersTypeService {

	private final UsersTypeRepository usersTypeRepository;
	
	@Autowired
	public UsersTypeService(UsersTypeRepository usersTypeRepository) {
		this.usersTypeRepository = usersTypeRepository;
	}
	
	public List<UsersType> getAll() {
		return usersTypeRepository.findAll();
	}
}
