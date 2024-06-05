package com.example.ResumeAnalysis.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ResumeAnalysis.Model.User;




public interface UserRepo extends JpaRepository<User, Long>{
	
	public User findByEmail(String email);

}
