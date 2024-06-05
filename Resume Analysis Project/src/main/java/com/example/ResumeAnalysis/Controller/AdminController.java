package com.example.ResumeAnalysis.Controller;

import java.security.Principal;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ResumeAnalysis.Model.User;
import com.example.ResumeAnalysis.Repository.UserRepo;
import com.example.ResumeAnalysis.Service.UserService;

import jakarta.servlet.http.HttpSession;


@Controller
@CrossOrigin("*")
@RequestMapping("/admin/")
public class AdminController {
	
	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserService userService;
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AdminController.class);

	
	
	@GetMapping("/")
    public String Dashboard(Model model, Principal principal) {
        try {
            boolean isLoggedIn = principal != null;
            model.addAttribute("isLoggedIn", isLoggedIn);

            if (isLoggedIn) {
                String email = principal.getName();
                User user = userRepo.findByEmail(email);
                model.addAttribute("users", user);
            }
            model.addAttribute("user", userService.getAllUser());

            // Log INFO message for successful dashboard loading
            logger.info("Dashboard loaded successfully");
            return "/AdminPanel/dashboard";
        } catch (Exception e) {
            // Log ERROR message for dashboard loading failure
            logger.error("An error occurred while loading the dashboard", e);
            // Redirect to an error page
            return "redirect:/errorPage";
        }
    }

    @GetMapping("/addUser")
    public String addUser(Model model, Principal principal) {
        try {
            boolean isLoggedIn = principal != null;
            model.addAttribute("isLoggedIn", isLoggedIn);

            if (isLoggedIn) {
                String email = principal.getName();
                User user = userRepo.findByEmail(email);
                model.addAttribute("users", user);
            }
            return "AdminPanel/addUser";
        } catch (Exception e) {
            logger.error("An error occurred while accessing add user page", e);
            return "redirect:/errorPage";
        }
    }

    @PostMapping("/submits")
    public String saveUser(@ModelAttribute User user, HttpSession session) {
        try {
            // Your code to save the user
            User savedUser = userService.saveUser(user);

            // Log INFO message for successful user saving
            logger.info("User saved successfully: {}", savedUser);
            // Redirect to the appropriate page
            return "redirect:/admin/";
        } catch (Exception e) {
            // Log ERROR message for user saving failure
            logger.error("An error occurred while saving user", e);
            // Redirect to an error page
            return "redirect:/errorPage";
        }
    }

    @GetMapping("/editUser")
    public String editUser(Model model, Principal principal) {
        try {
            boolean isLoggedIn = principal != null;
            model.addAttribute("isLoggedIn", isLoggedIn);
            if (isLoggedIn) {
                String email = principal.getName();
                User user = userRepo.findByEmail(email);
                model.addAttribute("users", user);
            }
            return "AdminPanel/editUser";
        } catch (Exception e) {
            logger.error("An error occurred while accessing edit user page", e);
            return "redirect:/errorPage";
        }
    }

    @GetMapping("/editUser/{id}")
    public String editSUser(@PathVariable Long id, Model model) {
        try {
            // Attempt to retrieve the user by ID
            User user = userService.getUserByID(id);

            // Add the user to the model
            model.addAttribute("user", user);

            return "AdminPanel/editUser";
        } catch (Exception e) {
            logger.error("An error occurred while accessing edit user page by ID", e);
            return "redirect:/errorPage";
        }
    }

    @PostMapping("/editUser/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user, Model model) {
        try {
            // Retrieve the existing user by ID
            User existingUser = userService.getUserByID(id);

            // Update the existing user's details
            existingUser.setId(id);
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setMobile(user.getMobile());

            // Encrypt the new password
            String newPassword = user.getPassword();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String hashedPassword = encoder.encode(newPassword);
            existingUser.setPassword(hashedPassword);

            // Update the user in the database
            userService.updateUser(existingUser);

            // Log DEBUG message for successful user update
            logger.debug("User updated successfully: {}", existingUser);
            return "redirect:/admin/";
        } catch (Exception e) {
            logger.error("An error occurred while updating user", e);
            return "redirect:/errorPage";
        }
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id) {
        try {
            // Attempt to delete the user by ID
            userService.deleteUser(id);
            // Log INFO message for successful user deletion
            logger.info("User deleted successfully with ID: {}", id);
            return "redirect:/admin/";
        } catch (Exception e) {
            logger.error("An error occurred while deleting user", e);
            return "redirect:/errorPage";
        }
    }



	

}
