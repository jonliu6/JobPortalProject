package org.freecode.demo.jobportal.service;

import java.util.Date;
import java.util.Optional;

import org.freecode.demo.jobportal.entity.JobSeekerProfile;
import org.freecode.demo.jobportal.entity.RecruiterProfile;
import org.freecode.demo.jobportal.entity.Users;
import org.freecode.demo.jobportal.repository.JobSeekerProfileRepository;
import org.freecode.demo.jobportal.repository.RecruiterProfileRepository;
import org.freecode.demo.jobportal.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

	private final UsersRepository usersRepository;
	private final RecruiterProfileRepository recruiterProfileRepository;
	private final JobSeekerProfileRepository jobSeekerProfileRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	public UsersService(UsersRepository usersRepository, RecruiterProfileRepository recruiterProfileRepository, JobSeekerProfileRepository jobSeekerProfileRepository, PasswordEncoder passwordEncoder) {
		this.usersRepository = usersRepository;
		this.recruiterProfileRepository = recruiterProfileRepository;
		this.jobSeekerProfileRepository = jobSeekerProfileRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	public Users addNew(Users users) {
		users.setActive(true);
		users.setRegistrationDate(new Date());
		users.setPassword(passwordEncoder.encode(users.getPassword()));
		Users savedUser = usersRepository.save(users);
		int userTypeId = users.getUserTypeId().getUserTypeId();
		if (userTypeId == 1) {
			recruiterProfileRepository.save(new RecruiterProfile(savedUser));
		}
		else {
			jobSeekerProfileRepository.save(new JobSeekerProfile(savedUser));
		}
		return savedUser;
	}
	
	public Optional<Users> getUserByEmail(String email) {
		return usersRepository.findByEmail(email);
	}
	
	public Object getCurrentUserProfile() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String username = auth.getName();
			Users user = usersRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("Could not find user"));
			int userId = user.getUserId();
			
			
			if (auth.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
				RecruiterProfile currentUserProfile = recruiterProfileRepository.findById(userId).orElse(new RecruiterProfile());
				return currentUserProfile;
			}
			else {
				JobSeekerProfile currentUserProfile = jobSeekerProfileRepository.findById(userId).orElse(new JobSeekerProfile());
				return currentUserProfile;
			}
		}
		
		return null;
	}

	public Users getCurrentUser() {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String username = auth.getName();
			Users user = usersRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("Could not find user"));
			return user;
		}
		return null;
	}

	public Users findByEmail(String currentUsername) {
		return usersRepository.findByEmail(currentUsername).orElseThrow(()-> new UsernameNotFoundException("User not found."));
	}
}
