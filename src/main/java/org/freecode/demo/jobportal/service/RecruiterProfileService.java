package org.freecode.demo.jobportal.service;

import java.util.Optional;

import org.freecode.demo.jobportal.entity.RecruiterProfile;
import org.freecode.demo.jobportal.entity.Users;
import org.freecode.demo.jobportal.repository.RecruiterProfileRepository;
import org.freecode.demo.jobportal.repository.UsersRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RecruiterProfileService {

	private final RecruiterProfileRepository recruiterProfileRepo;
	private final UsersRepository usersRepository;
	
	public RecruiterProfileService(RecruiterProfileRepository recruiterProfileRepository,
			UsersRepository usersRepo) {
		this.recruiterProfileRepo = recruiterProfileRepository;
		this.usersRepository = usersRepo;
	}
	
	public Optional<RecruiterProfile> getOne(Integer id) {
		return recruiterProfileRepo.findById(id);
	}
	
	public RecruiterProfile addNew(RecruiterProfile recruiterProfile) {
		return recruiterProfileRepo.save(recruiterProfile);
	}

	public RecruiterProfile getCurrentRecruiterProfile() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String currentUsername = auth.getName();
			Users user = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("User not found"));
			Optional<RecruiterProfile> recruiterProfile = getOne(user.getUserId());
			return recruiterProfile.orElse(null);
		} else return null;
	}
}
