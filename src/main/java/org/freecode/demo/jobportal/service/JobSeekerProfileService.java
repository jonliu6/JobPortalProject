package org.freecode.demo.jobportal.service;

import java.util.Optional;

import org.freecode.demo.jobportal.entity.JobSeekerProfile;
import org.freecode.demo.jobportal.entity.RecruiterProfile;
import org.freecode.demo.jobportal.entity.Users;
import org.freecode.demo.jobportal.repository.JobSeekerProfileRepository;
import org.freecode.demo.jobportal.repository.UsersRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JobSeekerProfileService {
	
	private final JobSeekerProfileRepository jobSeekerProfileRepository;
	private final UsersRepository usersRepository;

	public JobSeekerProfileService(JobSeekerProfileRepository jobSeekerProfileRepo, UsersRepository usersRepo) {
		this.jobSeekerProfileRepository = jobSeekerProfileRepo;
		this.usersRepository = usersRepo;
	}
	
	public Optional<JobSeekerProfile> getOne(Integer id) {
		return jobSeekerProfileRepository.findById(id);
	}

	public JobSeekerProfile addNew(JobSeekerProfile jobSeekerProfile) {
		return jobSeekerProfileRepository.save(jobSeekerProfile);
	}

	public JobSeekerProfile getCurrentSeekerProfile() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String currentUsername = auth.getName();
			Users user = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("User not found"));
			Optional<JobSeekerProfile> jobSeekerProfile = getOne(user.getUserId());
			return jobSeekerProfile.orElse(null);
		} else return null;
	}
}
