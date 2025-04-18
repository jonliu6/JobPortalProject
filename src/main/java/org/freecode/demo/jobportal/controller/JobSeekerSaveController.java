package org.freecode.demo.jobportal.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.freecode.demo.jobportal.entity.JobPostActivity;
import org.freecode.demo.jobportal.entity.JobSeekerProfile;
import org.freecode.demo.jobportal.entity.JobSeekerSave;
import org.freecode.demo.jobportal.entity.Users;
import org.freecode.demo.jobportal.service.JobPostActivityService;
import org.freecode.demo.jobportal.service.JobSeekerProfileService;
import org.freecode.demo.jobportal.service.JobSeekerSaveService;
import org.freecode.demo.jobportal.service.UsersService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class JobSeekerSaveController {

	private final UsersService usersService;
	private final JobSeekerProfileService jobSeekerProfileService;
	private final JobPostActivityService jobPostActivityService;
	private final JobSeekerSaveService jobSeekerSaveService;
	public JobSeekerSaveController(UsersService usersService, JobSeekerProfileService jobSeekerProfileService,
			JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService) {
		this.usersService = usersService;
		this.jobSeekerProfileService = jobSeekerProfileService;
		this.jobPostActivityService = jobPostActivityService;
		this.jobSeekerSaveService = jobSeekerSaveService;
	}
	
	@PostMapping("job-details/save/{id}")
	public String save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String currentUsername = auth.getName();
			Users user = usersService.findByEmail(currentUsername);
			Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
			JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
			if (seekerProfile.isPresent() && jobPostActivity != null) {
				jobSeekerSave = new JobSeekerSave();
				jobSeekerSave.setJob(jobPostActivity);
				jobSeekerSave.setUserId(seekerProfile.get());
			} else {
				throw new RuntimeException("User not found.");
			}
			jobSeekerSaveService.addNew(jobSeekerSave);
		}
		
		return "redirect:/dashboard";
		
	}
	
	@GetMapping("saved-jobs")
	public String savedJobs(Model model) {
		List<JobPostActivity> jobPost = new ArrayList<>();
		Object currentUserProfile = usersService.getCurrentUserProfile();
		
		List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);
		for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
			jobPost.add(jobSeekerSave.getJob());
		}
		
		model.addAttribute("jobPost", jobPost);
		model.addAttribute("user", currentUserProfile);
		
		return "saved-jobs";
		
	}
}
