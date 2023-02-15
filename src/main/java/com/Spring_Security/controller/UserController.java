package com.Spring_Security.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Spring_Security.comman.UserConstant;
import com.Spring_Security.entity.User;
import com.Spring_Security.repository.UserRepositiory;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@Slf4j 
public class UserController {
	
	

	@Autowired
	private UserRepositiory userRepo;
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@PostMapping("/join")
	public String joinGroup(@RequestBody User user) {
		user.setRoles(UserConstant.DEFAULT_ROLE);
		String encode = passwordEncoder.encode(user.getPassword());
		log.info("controller is working");
		user.setPassword(encode);
		
		userRepo.save(user);
		return " Hi "+  user.getUsername()  +   "Welcome to BlueThink family";
		
	}
	@GetMapping("/access/{id}/{userRole}")
	//@Secured("ROLE_ADMIN")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_RESOURCE')")
	public String giveAccessToUser(@PathVariable Long id,@PathVariable String userRole,Principal principle) {
		User user= userRepo.findById(id).get();
		List<String> activeRoles = getRolesByLoggedInUser(principle);
		 
		String newRole="";
		if(activeRoles.contains(userRole)) {
			
			newRole=user.getRoles()+ ","+userRole;
			user.setRoles(newRole);
			
		}
		userRepo.save(user);
		return"Hi" +  user.getUsername() +  " new role assign by "   +  principle.getName();
		
		
		
	}
	@GetMapping
	@Secured("ROLE_ADMIN")
	
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	
	public List<User>loadUser(){
		return userRepo.findAll();
		
	}
	@GetMapping("/test")
    @PreAuthorize("hasAuthority('ROLE_USER') ")
	public String testUserAccess() {		
		return "user can only access this";
	}
	
	private List<String> getRolesByLoggedInUser(Principal principal){
		String roles=getLoggedInUser(principal).getRoles();
		List<String> assignRoles = Arrays.stream(roles.split(",")).collect(Collectors.toList());
		
		if(assignRoles.contains("ROLE_ADMIN")) {
			return Arrays.stream(UserConstant.ADMIN_ACCESS).collect(Collectors.toList());
			
		}
		if(assignRoles.contains("ROLE_RESOURCE")) {
			return Arrays.stream(UserConstant.RESOURCE_ACCESS).collect(Collectors.toList());
			
		}
		return Collections.emptyList();
		
	}
	
	private User getLoggedInUser(Principal principle) {
		return userRepo.getByUsername(principle.getName()).get();
		
	}

}
