package org.freecode.demo.jobportal.service;

import java.util.List;

import org.freecode.demo.jobportal.entity.JobPostActivity;
import org.freecode.demo.jobportal.entity.JobSeekerProfile;
import org.freecode.demo.jobportal.entity.JobSeekerSave;
import org.freecode.demo.jobportal.repository.JobSeekerSaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobSeekerSaveService {
	
	private final JobSeekerSaveRepository jobSeekerSaveRepository;

	@Autowired
	public JobSeekerSaveService(JobSeekerSaveRepository jobSeekerSaveRepository) {
		this.jobSeekerSaveRepository = jobSeekerSaveRepository;
	}
	
	public List<JobSeekerSave> getCandidatesJob(JobSeekerProfile userAccountId) {
		return jobSeekerSaveRepository.findByUserId(userAccountId);
	}

	public List<JobSeekerSave> getJobCandidates(JobPostActivity job) {
		return jobSeekerSaveRepository.findByJob(job);
	}

	public void addNew(JobSeekerSave jobSeekerSave) {
		jobSeekerSaveRepository.save(jobSeekerSave);
		
	}
}
