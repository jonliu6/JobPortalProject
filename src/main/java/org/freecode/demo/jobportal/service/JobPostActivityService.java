package org.freecode.demo.jobportal.service;

import java.util.ArrayList;
import java.util.List;

import org.freecode.demo.jobportal.entity.IRecruiterJobs;
import org.freecode.demo.jobportal.entity.JobCompany;
import org.freecode.demo.jobportal.entity.JobLocation;
import org.freecode.demo.jobportal.entity.JobPostActivity;
import org.freecode.demo.jobportal.entity.RecruiterJobsDto;
import org.freecode.demo.jobportal.repository.JobPostActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobPostActivityService {

	private final JobPostActivityRepository jobPostActivityRepo;
	
	@Autowired
	public JobPostActivityService(JobPostActivityRepository jobPostActivityRepositoty) {
		this.jobPostActivityRepo = jobPostActivityRepositoty;
	}
	
	public JobPostActivity addNew(JobPostActivity jobPostActivity) {
		return jobPostActivityRepo.save(jobPostActivity);
	}
	
	public List<RecruiterJobsDto> getRecruiterJobs(int recruiter) {
		List<IRecruiterJobs> recruiterJobsDtos = jobPostActivityRepo.getRecruiterJobs(recruiter);
		List<RecruiterJobsDto> recruiterJobsDtoList = new ArrayList<>();
		
		for (IRecruiterJobs rec:recruiterJobsDtos) {
			JobLocation loc = new JobLocation(rec.getLocationId(), rec.getCity(), rec.getState(), rec.getCountry());
			JobCompany comp = new JobCompany(rec.getCompanyId(),rec.getName(),"");
			recruiterJobsDtoList.add(new RecruiterJobsDto(rec.getTotalCandidates(), rec.getJob_post_id(), rec.getJob_title(), loc, comp));
		}
		
		return recruiterJobsDtoList;
	}

	public JobPostActivity getOne(int id) {
		return jobPostActivityRepo.findById(id).orElseThrow(() -> new RuntimeException("Job" + id + " not found."));
	}
	
	public void deleteById(int id) {
		jobPostActivityRepo.deleteById(id);
	}
}
