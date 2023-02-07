package com.Spring_Security.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Spring_Security.entity.User;
import com.Spring_Security.repository.UserRepositiory;



@Service
public class GroupUserDetailsService implements UserDetailsService {
	
	@Autowired
	private  UserRepositiory userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepo.getByUsername(username);
		
		
		return user.map(GroupUserDetails::new).orElseThrow(()-> new UsernameNotFoundException(username+"does not exist"));
	}
	
	
	
	
	
	

}
