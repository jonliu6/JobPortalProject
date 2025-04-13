package org.freecode.demo.jobportal.controller;

import java.util.Date;
import java.util.List;

import org.freecode.demo.jobportal.entity.JobPostActivity;
import org.freecode.demo.jobportal.entity.RecruiterJobsDto;
import org.freecode.demo.jobportal.entity.RecruiterProfile;
import org.freecode.demo.jobportal.entity.Users;
import org.freecode.demo.jobportal.service.JobPostActivityService;
import org.freecode.demo.jobportal.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class JobPostActivityController {

	private final UsersService usersService;
	private final JobPostActivityService jobPostActivitySvc;
	
	@Autowired
	public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService) {
		this.usersService = usersService;
		this.jobPostActivitySvc = jobPostActivityService;
	}
	
	@GetMapping("/dashboard")
	public String searchJobs(Model model) {
		Object currentUserProfile = usersService.getCurrentUserProfile();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String currentUsername = auth.getName();
			model.addAttribute("username", currentUsername);
			if (auth.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
				List<RecruiterJobsDto> recruiterJobs = jobPostActivitySvc.getRecruiterJobs(((RecruiterProfile) currentUserProfile).getUserAccountId());
			    model.addAttribute("jobPost", recruiterJobs);
			}
		}
		model.addAttribute("user", currentUserProfile);
		
		return "dashboard";
	}
	
	@GetMapping("/dashboard/add")
	public String addJobs(Model model) {
		model.addAttribute("jobPostActivity", new JobPostActivity());
		model.addAttribute("user", usersService.getCurrentUserProfile());
		
		return "add-jobs";
	}
	
	@PostMapping("/dashboard/addNew")
	public String addNew(JobPostActivity jobPostActivity, Model model) {
		Users user = usersService.getCurrentUser();	
		if (user != null) {
			jobPostActivity.setPostedById(user);
			
		}
		jobPostActivity.setPostedDate(new Date());
		model.addAttribute("jobPostActitity", jobPostActivity);
		JobPostActivity saved = jobPostActivitySvc.addNew(jobPostActivity);
		
		return "redirect:/dashboard";
	}
	
	@PostMapping("/dashboard/edit/{id}")
	public String editJob(@PathVariable("id") int id, Model model) {
		JobPostActivity jobPostActivity = jobPostActivitySvc.getOne(id);
		model.addAttribute("jobPostActivity", jobPostActivity);
		model.addAttribute("user", usersService.getCurrentUserProfile());
		return "add-jobs";
	}
	
	@PostMapping("dashboard/deleteJob/{id}")
	public String deleteJob(@PathVariable("id") int id) {
		jobPostActivitySvc.deleteById(id);
		return "redirect:/dashboard";
	}
}
