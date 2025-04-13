package org.freecode.demo.jobportal.controller;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.freecode.demo.jobportal.entity.RecruiterProfile;
import org.freecode.demo.jobportal.entity.Users;
import org.freecode.demo.jobportal.repository.UsersRepository;
import org.freecode.demo.jobportal.service.RecruiterProfileService;
import org.freecode.demo.jobportal.util.FileUploadUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

	private final UsersRepository usersRepo;
	
	private final RecruiterProfileService recruiterProfileSvc;
	
	public RecruiterProfileController(UsersRepository usersRepository, RecruiterProfileService recruiterProfileService) {
		this.usersRepo = usersRepository;
		this.recruiterProfileSvc = recruiterProfileService;
	}
	@GetMapping("/")
	public String recruiterProfile(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String currentUsername = auth.getName();
			Users user = usersRepo.findByEmail(currentUsername).orElseThrow(()-> new UsernameNotFoundException("Could not find user"));
			Optional<RecruiterProfile> rp = recruiterProfileSvc.getOne(user.getUserId());
			
			if (! rp.isEmpty()) {
				model.addAttribute("profile", rp.get());
			}
		}
		return "recruiter_profile";
	}
	
	@PostMapping("/addNew")
	public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile multiPartFile, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String currentUsername = auth.getName();
			Users user = usersRepo.findByEmail(currentUsername).orElseThrow(()-> new UsernameNotFoundException("Could not find user"));
			recruiterProfile.setUserId(user);
			recruiterProfile.setUserAccountId(user.getUserId());
		}
		model.addAttribute("profile", recruiterProfile);
		String filename = "";
		if (!multiPartFile.getOriginalFilename().equals("")) {
			filename = StringUtils.cleanPath(Objects.requireNonNull(multiPartFile.getOriginalFilename()));
			recruiterProfile.setProfilePhoto(filename);
		}
		RecruiterProfile savedUser = recruiterProfileSvc.addNew(recruiterProfile);
		
		String uploadDir = "photos/recruiter/" + savedUser.getUserAccountId();
		try {
			FileUploadUtil.saveFile(uploadDir, filename, multiPartFile);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return "redirect:/dashboard";
	}
}
