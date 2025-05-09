package org.freecode.demo.jobportal.entity;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"userId", "job"})
})
public class JobSeekerSave implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer Id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="userId", referencedColumnName = "user_account_id")
	private JobSeekerProfile userId;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="job", referencedColumnName = "jobPostId")
	private JobPostActivity job;

	public JobSeekerSave() {
	}

	public JobSeekerSave(Integer id, JobSeekerProfile userId, JobPostActivity job) {
		Id = id;
		this.userId = userId;
		this.job = job;
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public JobSeekerProfile getUserId() {
		return userId;
	}

	public void setUserId(JobSeekerProfile userId) {
		this.userId = userId;
	}

	public JobPostActivity getJob() {
		return job;
	}

	public void setJob(JobPostActivity job) {
		this.job = job;
	}

	@Override
	public String toString() {
		return "JobSeekerSave [Id=" + Id + ", userId=" + userId + ", job=" + job + "]";
	}
}
