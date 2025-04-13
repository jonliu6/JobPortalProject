package org.freecode.demo.jobportal.controller;

import java.util.List;
import java.util.Optional;

import org.freecode.demo.jobportal.entity.Users;
import org.freecode.demo.jobportal.entity.UsersType;
import org.freecode.demo.jobportal.service.UsersService;
import org.freecode.demo.jobportal.service.UsersTypeService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class UsersController {

	private final UsersTypeService usersTypeService;
	private final UsersService usersService;
	
	public UsersController(UsersTypeService usersTypeService, UsersService usersService) {
		this.usersTypeService = usersTypeService;
		this.usersService = usersService;
	}
	
	@GetMapping("/register")
	public String register(Model model) {
		List<UsersType> usersType = usersTypeService.getAll();
		model.addAttribute("getAllTypes", usersType);
		model.addAttribute("user", new Users());
		
		return "register";
	}
	
	@PostMapping("/register/new")
	public String userRegistration(@Valid Users users, Model model) {
		//System.out.println("Users:: " + users);
		Optional<Users> optionalUsers = usersService.getUserByEmail(users.getEmail());
		
		if (optionalUsers.isPresent()) {
			model.addAttribute("error", "Email already registered , try to login or register with another email.");
			List<UsersType> usersType = usersTypeService.getAll();
			model.addAttribute("getAllTypes", usersType);
			model.addAttribute("user", new Users());
			
			return "register";
		}
		usersService.addNew(users);
		return "redirect:/dashboard";
	}
	
	@GetMapping("/login")
	public String login() {
		
		return "login";
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest req, HttpServletResponse resp) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(req, resp, auth);
		}
		return "redirect:/";
		
	}
}
