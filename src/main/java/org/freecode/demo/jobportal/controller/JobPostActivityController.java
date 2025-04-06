package org.freecode.demo.jobportal.controller;

import org.freecode.demo.jobportal.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JobPostActivityController {

	private final UsersService usersService;
	
	@Autowired
	public JobPostActivityController(UsersService usersService) {
		this.usersService = usersService;
	}
	
	@GetMapping("/dashboard")
	public String searchJobs(Model model) {
		Object currentUserProfile = usersService.getCurrentUserProfile();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String currentUsername = auth.getName();
			model.addAttribute("username", currentUsername);
		}
		model.addAttribute("user", currentUserProfile);
		
		return "dashboard";
	}
}
