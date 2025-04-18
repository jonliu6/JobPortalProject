package org.freecode.demo.jobportal.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.freecode.demo.jobportal.entity.JobPostActivity;
import org.freecode.demo.jobportal.entity.JobSeekerApply;
import org.freecode.demo.jobportal.entity.JobSeekerProfile;
import org.freecode.demo.jobportal.entity.JobSeekerSave;
import org.freecode.demo.jobportal.entity.RecruiterProfile;
import org.freecode.demo.jobportal.entity.Users;
import org.freecode.demo.jobportal.service.JobPostActivityService;
import org.freecode.demo.jobportal.service.JobSeekerApplyService;
import org.freecode.demo.jobportal.service.JobSeekerProfileService;
import org.freecode.demo.jobportal.service.JobSeekerSaveService;
import org.freecode.demo.jobportal.service.RecruiterProfileService;
import org.freecode.demo.jobportal.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class JobSeekerApplyController {

	private final JobPostActivityService jobPostActivityService;
	private final UsersService usersService;
	private final JobSeekerApplyService jobSeekerApplyService;
	private final JobSeekerSaveService jobSeekerSaveService;
	private final RecruiterProfileService recruiterProfileService;
	private final JobSeekerProfileService jobSeekerProfileService;
	
	@Autowired
	public JobSeekerApplyController(JobPostActivityService jobPostActivitySvc, 
			UsersService usersSvc, 
			JobSeekerApplyService jobSeekerApplySvc, 
			JobSeekerSaveService jobSeekerSaveSvc,
			RecruiterProfileService recruiterProfileSvc,
			JobSeekerProfileService jobSeekerProfileSvc) {
		this.jobPostActivityService = jobPostActivitySvc;
		this.usersService = usersSvc;
		this.jobSeekerApplyService = jobSeekerApplySvc;
		this.jobSeekerSaveService = jobSeekerSaveSvc;
		this.recruiterProfileService = recruiterProfileSvc;
		this.jobSeekerProfileService = jobSeekerProfileSvc;
	}
	
	@GetMapping("/job-details-apply/{id}")
	public String display(@PathVariable("id") int id, Model model) {
		JobPostActivity jobDetails = jobPostActivityService.getOne(id);
		List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getJobCandidates(jobDetails);
		List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getJobCandidates(jobDetails);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			if (auth.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
				RecruiterProfile user = recruiterProfileService.getCurrentRecruiterProfile();
				if (user !=null) {
					model.addAttribute("applyList", jobSeekerApplyList);					
				}
			} else {
				JobSeekerProfile user = jobSeekerProfileService.getCurrentSeekerProfile();
				if (user != null) {
					boolean exists = false;
					boolean saved = false;
					for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
						if (jobSeekerApply.getUserId().getUserAccountId() == user.getUserAccountId()) {
							exists = true;
							break;
						}
					}
					for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
						if (jobSeekerSave.getUserId().getUserAccountId() == user.getUserAccountId()) {
							saved = true;
							break;
						}
					}
					model.addAttribute("alreadyApplied", exists);
					model.addAttribute("alreadySaved", saved);
				}
			}
		}
		
		JobSeekerApply jobSeekerApply = new JobSeekerApply();
		model.addAttribute("applyJob", jobSeekerApply);
		
		model.addAttribute("jobDetails", jobDetails);
		model.addAttribute("user", usersService.getCurrentUserProfile());
		
		return "job-details";
	}
	
	@PostMapping("job-details/apply/{id}")
	public String apply(@PathVariable("id") int id, JobSeekerApply jobSeekerApply) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String currentUsername = auth.getName();
			Users user = usersService.findByEmail(currentUsername);
			Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
			JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
			if (seekerProfile.isPresent() && jobPostActivity != null) {
				jobSeekerApply = new JobSeekerApply();
				jobSeekerApply.setUserId(seekerProfile.get());
				jobSeekerApply.setJob(jobPostActivity);
				jobSeekerApply.setApplyDate(new Date());
			} else {
				throw new RuntimeException("User not found.");
			}
			jobSeekerApplyService.addNew(jobSeekerApply);
		}
		
		return "redirect:/dashboard";
		
	}
}
