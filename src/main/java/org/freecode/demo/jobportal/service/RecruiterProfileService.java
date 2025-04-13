package org.freecode.demo.jobportal.service;

import java.util.Optional;

import org.freecode.demo.jobportal.entity.RecruiterProfile;
import org.freecode.demo.jobportal.repository.RecruiterProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class RecruiterProfileService {

	private final RecruiterProfileRepository recruiterProfileRepo;
	
	public RecruiterProfileService(RecruiterProfileRepository recruiterProfileRepository) {
		this.recruiterProfileRepo = recruiterProfileRepository;
	}
	
	public Optional<RecruiterProfile> getOne(Integer id) {
		return recruiterProfileRepo.findById(id);
	}
	
	public RecruiterProfile addNew(RecruiterProfile recruiterProfile) {
		return recruiterProfileRepo.save(recruiterProfile);
	}
}
