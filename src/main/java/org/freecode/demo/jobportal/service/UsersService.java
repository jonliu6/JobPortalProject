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
import org.springframework.stereotype.Service;

@Service
public class UsersService {

	private final UsersRepository usersRepository;
	private final RecruiterProfileRepository recruiterProfileRepository;
	private final JobSeekerProfileRepository jobSeekerProfileRepository;
	
	@Autowired
	public UsersService(UsersRepository usersRepository, RecruiterProfileRepository recruiterProfileRepository, JobSeekerProfileRepository jobSeekerProfileRepository) {
		this.usersRepository = usersRepository;
		this.recruiterProfileRepository = recruiterProfileRepository;
		this.jobSeekerProfileRepository = jobSeekerProfileRepository;
	}
	
	public Users addNew(Users users) {
		users.setActive(true);
		users.setRegistrationDate(new Date());
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
}
