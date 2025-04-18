package org.freecode.demo.jobportal.service;

import java.util.List;

import org.freecode.demo.jobportal.entity.JobPostActivity;
import org.freecode.demo.jobportal.entity.JobSeekerApply;
import org.freecode.demo.jobportal.entity.JobSeekerProfile;
import org.freecode.demo.jobportal.repository.JobSeekerApplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobSeekerApplyService {

	private final JobSeekerApplyRepository jobSeekerApplyRepository;

	@Autowired
	public JobSeekerApplyService(JobSeekerApplyRepository jobSeekerApplyRepository) {
		this.jobSeekerApplyRepository = jobSeekerApplyRepository;
	}
	
	public List<JobSeekerApply> getCandidatesJobs(JobSeekerProfile userAccountId) {
		return jobSeekerApplyRepository.findByUserId(userAccountId);
	}
	
	public List<JobSeekerApply> getJobCandidates(JobPostActivity job) {
		return jobSeekerApplyRepository.findByJob(job);
	}

	public void addNew(JobSeekerApply jobSeekerApply) {
		jobSeekerApplyRepository.save(jobSeekerApply);
		
	}
}
