package org.freecode.demo.jobportal.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.freecode.demo.jobportal.entity.JobPostActivity;
import org.freecode.demo.jobportal.entity.JobSeekerApply;
import org.freecode.demo.jobportal.entity.JobSeekerProfile;
import org.freecode.demo.jobportal.entity.JobSeekerSave;
import org.freecode.demo.jobportal.entity.RecruiterJobsDto;
import org.freecode.demo.jobportal.entity.RecruiterProfile;
import org.freecode.demo.jobportal.entity.Users;
import org.freecode.demo.jobportal.service.JobPostActivityService;
import org.freecode.demo.jobportal.service.JobSeekerApplyService;
import org.freecode.demo.jobportal.service.JobSeekerSaveService;
import org.freecode.demo.jobportal.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JobPostActivityController {

	private final UsersService usersService;
	private final JobPostActivityService jobPostActivitySvc;
	private final JobSeekerApplyService jobSeekerApplyService;
	private final JobSeekerSaveService jobSeekerSaveService;
	
	@Autowired
	public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService, JobSeekerApplyService jobSeekerApplySvc, JobSeekerSaveService jobSeekerSaveSvc) {
		this.usersService = usersService;
		this.jobPostActivitySvc = jobPostActivityService;
		this.jobSeekerApplyService = jobSeekerApplySvc;
		this.jobSeekerSaveService = jobSeekerSaveSvc;
	}
	
	@GetMapping("/dashboard")
	public String searchJobs(Model model, @RequestParam(value="job", required = false) String job,
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "partTime", required = false) String partTime,
			@RequestParam(value = "fullTime", required = false) String fullTime,
			@RequestParam(value = "freelance", required = false) String freelance,
			@RequestParam(value = "remoteOnly", required = false) String remoteOnly,
			@RequestParam(value = "officeOnly", required = false) String officeOnly,
			@RequestParam(value = "partialRemote", required = false) String partialRemote,
			@RequestParam(value = "today", required = false) boolean today,
			@RequestParam(value = "days7", required = false) boolean days7,
			@RequestParam(value = "days30", required = false) boolean days30
			) {
		model.addAttribute("partTime", Objects.equals(partTime, "partTime"));
		model.addAttribute("fullTime", Objects.equals(fullTime, "fullTime"));
		model.addAttribute("freelance", Objects.equals(freelance, "freelance"));
		
		model.addAttribute("remoteOnly", Objects.equals(partTime, "Remote-Only"));
		model.addAttribute("officeOnly", Objects.equals(officeOnly, "Office-Only"));
		model.addAttribute("partialRemote", Objects.equals(partialRemote, "Remote-Only"));
		
		model.addAttribute("today", today);
		model.addAttribute("days7", days7);
		model.addAttribute("days30", days30);
		
		model.addAttribute("job", job);
		model.addAttribute("location", location);
		
		LocalDate searchDate = null;
		List<JobPostActivity> jobPost = null;
		boolean dateSearchString = true;
		boolean remote = true;
		boolean type = true;
		
		if (days30) {
			searchDate = LocalDate.now().minusDays(30);
		}
		else if(days7) {
			searchDate = LocalDate.now().minusDays(7);
		}
		else if (today) {
			searchDate = LocalDate.now();
		}
		else {
			dateSearchString = false;
		}
		
		if (partTime==null && fullTime==null && freelance==null) {
			partTime = "Part-Time";
			fullTime = "Full-Time";
			freelance = "Freelance";
			remote = false;
		}
		
		if (officeOnly==null && remoteOnly==null && partialRemote==null) {
			officeOnly = "Office-Only";
			remoteOnly = "Remote-Only";
			partialRemote = "Partial-Remote";
			type = false;
		}
		
		if (!dateSearchString && !remote && !type && !StringUtils.hasText(job) && !StringUtils.hasText(location)) {
			jobPost = jobPostActivitySvc.getAll();
		}
		else {
			jobPost = jobPostActivitySvc.search(job, location, Arrays.asList(partTime, fullTime, freelance),
					Arrays.asList(remoteOnly, officeOnly, partialRemote), searchDate);
		}
		
		Object currentUserProfile = usersService.getCurrentUserProfile();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String currentUsername = auth.getName();
			model.addAttribute("username", currentUsername);
			if (auth.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
				List<RecruiterJobsDto> recruiterJobs = jobPostActivitySvc.getRecruiterJobs(((RecruiterProfile) currentUserProfile).getUserAccountId());
			    model.addAttribute("jobPost", recruiterJobs);
			}
			else {
				List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getCandidatesJobs((JobSeekerProfile) currentUserProfile);
				List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);
				
				boolean exist;
				boolean saved;
				for (JobPostActivity jobActivity : jobPost) {
					exist = false;
					saved = false;
					
					for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
						if (Objects.equals(jobActivity.getJobPostId(), jobSeekerApply.getJob().getJobPostId())) {
							jobActivity.setIsActive(true);
							exist = true;
							break;
						}
					}
					
					for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
						if (Objects.equals(jobActivity.getJobPostId(), jobSeekerSave.getJob().getJobPostId())) {
							jobActivity.setIsSaved(true);
							saved = true;
							break;
						}
					}
					
					if (!exist) {
						jobActivity.setIsActive(false);
					}
					if (!saved) {
						jobActivity.setIsSaved(false);
					}
					model.addAttribute("jobPost", jobPost);
				}
			}
		}
		model.addAttribute("user", currentUserProfile);
		
		return "dashboard";
	}
	
	@GetMapping("global-search/")
	public String globalSearch(Model model, @RequestParam(value="job", required = false) String job,
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "partTime", required = false) String partTime,
			@RequestParam(value = "fullTime", required = false) String fullTime,
			@RequestParam(value = "freelance", required = false) String freelance,
			@RequestParam(value = "remoteOnly", required = false) String remoteOnly,
			@RequestParam(value = "officeOnly", required = false) String officeOnly,
			@RequestParam(value = "partialRemote", required = false) String partialRemote,
			@RequestParam(value = "today", required = false) boolean today,
			@RequestParam(value = "days7", required = false) boolean days7,
			@RequestParam(value = "days30", required = false) boolean days30) {
		
		model.addAttribute("partTime", Objects.equals(partTime, "partTime"));
		model.addAttribute("fullTime", Objects.equals(fullTime, "fullTime"));
		model.addAttribute("freelance", Objects.equals(freelance, "freelance"));
		
		model.addAttribute("remoteOnly", Objects.equals(partTime, "Remote-Only"));
		model.addAttribute("officeOnly", Objects.equals(officeOnly, "Office-Only"));
		model.addAttribute("partialRemote", Objects.equals(partialRemote, "Remote-Only"));
		
		model.addAttribute("today", today);
		model.addAttribute("days7", days7);
		model.addAttribute("days30", days30);
		
		model.addAttribute("job", job);
		model.addAttribute("location", location);
		
		LocalDate searchDate = null;
		List<JobPostActivity> jobPost = null;
		boolean dateSearchString = true;
		boolean remote = true;
		boolean type = true;
		
		if (days30) {
			searchDate = LocalDate.now().minusDays(30);
		}
		else if(days7) {
			searchDate = LocalDate.now().minusDays(7);
		}
		else if (today) {
			searchDate = LocalDate.now();
		}
		else {
			dateSearchString = false;
		}
		
		if (partTime==null && fullTime==null && freelance==null) {
			partTime = "Part-Time";
			fullTime = "Full-Time";
			freelance = "Freelance";
			remote = false;
		}
		
		if (officeOnly==null && remoteOnly==null && partialRemote==null) {
			officeOnly = "Office-Only";
			remoteOnly = "Remote-Only";
			partialRemote = "Partial-Remote";
			type = false;
		}
		
		if (!dateSearchString && !remote && !type && !StringUtils.hasText(job) && !StringUtils.hasText(location)) {
			jobPost = jobPostActivitySvc.getAll();
		}
		else {
			jobPost = jobPostActivitySvc.search(job, location, Arrays.asList(partTime, fullTime, freelance),
					Arrays.asList(remoteOnly, officeOnly, partialRemote), searchDate);
		}
		
		model.addAttribute("jobPost", jobPost);
		
		return "global-search";
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
