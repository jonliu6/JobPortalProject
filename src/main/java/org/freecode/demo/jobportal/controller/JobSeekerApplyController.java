package org.freecode.demo.jobportal.controller;

import org.freecode.demo.jobportal.entity.JobPostActivity;
import org.freecode.demo.jobportal.service.JobPostActivityService;
import org.freecode.demo.jobportal.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class JobSeekerApplyController {

	private final JobPostActivityService jobPostActivityService;
	private final UsersService usersService;
	
	@Autowired
	public JobSeekerApplyController(JobPostActivityService jobPostActivitySvc, UsersService usersSvc) {
		this.jobPostActivityService = jobPostActivitySvc;
		this.usersService = usersSvc;
	}
	
	@GetMapping("/job-details-apply/{id}")
	public String display(@PathVariable("id") int id, Model model) {
		JobPostActivity jobDetails = jobPostActivityService.getOne(id);
		model.addAttribute("jobDetails", jobDetails);
		model.addAttribute("user", usersService.getCurrentUserProfile());
		
		return "job-details";
	}
	
}
