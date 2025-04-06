package org.freecode.demo.jobportal.service;

import org.freecode.demo.jobportal.entity.Users;
import org.freecode.demo.jobportal.repository.UsersRepository;
import org.freecode.demo.jobportal.util.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	private final UsersRepository usersRepo;
	
	@Autowired
	public CustomUserDetailsService(UsersRepository usersRepository) {
		this.usersRepo = usersRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = usersRepo.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("Could not find user"));
		return new CustomUserDetails(user);
	}
}
