package org.freecode.demo.jobportal.repository;

import java.util.List;

import org.freecode.demo.jobportal.entity.JobPostActivity;
import org.freecode.demo.jobportal.entity.JobSeekerProfile;
import org.freecode.demo.jobportal.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

	List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);
	
	List<JobSeekerSave> findByJob(JobPostActivity job);
}
