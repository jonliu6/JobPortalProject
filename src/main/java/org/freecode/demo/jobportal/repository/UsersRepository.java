package org.freecode.demo.jobportal.repository;

import java.util.Optional;

import org.freecode.demo.jobportal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Integer>{

	Optional<Users> findByEmail(String email);
}
