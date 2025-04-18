package org.freecode.demo.jobportal.repository;

import java.util.List;

import org.freecode.demo.jobportal.entity.JobPostActivity;
import org.freecode.demo.jobportal.entity.JobSeekerApply;
import org.freecode.demo.jobportal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer>{

	List<JobSeekerApply> findByUserId(JobSeekerProfile userId);
	
	List<JobSeekerApply> findByJob(JobPostActivity job);
}
