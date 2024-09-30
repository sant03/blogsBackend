package com.bolsadeideas.springboot.blogpost.app.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.digester.ArrayStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.annotation.SessionScope;

import com.bolsadeideas.springboot.blogpost.app.entities.Blog;
import com.bolsadeideas.springboot.blogpost.app.entities.Role;
import com.bolsadeideas.springboot.blogpost.app.entities.User;
import com.bolsadeideas.springboot.blogpost.app.services.UserService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
@SessionScope
@SessionAttributes("user")
public class UserController {
	
	@Autowired
	private UserService service;
	
	@GetMapping("/")
	public String index(Model model){
		return "index";
	}
	
	@GetMapping("/users")
	public ResponseEntity<?> listarUsuarios(Model model){
		List<User> users = service.listUsers();
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("users", users);
		return ResponseEntity.ok().body(users);
	}
	
	@GetMapping("/users/{id}")
	public String listarUsuario(Model model, @PathVariable Integer id){
		User user = service.findById(id);
		if(user == null) {
			return "redirect:/users";
		}
		model.addAttribute("existsUser", false);
		model.addAttribute("user", user);
		return "user";
	}
	
	
	@GetMapping({"/login"})
	public String login(Model model) {
		User user = new User();
		model.addAttribute("isAuth", null);
		model.addAttribute("user", user);
		return "login";
	}
	
	@GetMapping({"/signUp"})
	public String signUp(Model model) {
		User user = new User();
		model.addAttribute("existsUser", false);
		model.addAttribute("user", user);
		return "signUp";
	}
	
	
	@PostMapping("/loginUser")
	public ResponseEntity<?> login(Model model,@Valid @RequestBody User user, BindingResult result) {
		Map<String, Object> errors = new HashMap<>();
        System.out.print("Aqui controller campos no llenados: " + user.getUsername());

		if(result.hasFieldErrors()) {
			result.getFieldErrors().forEach(error -> {
				errors.put(error.getField(), "El campo: " + error.getField() + ' ' + error.getDefaultMessage());
			});
			
			model.addAttribute("user", user);
			model.addAttribute("errors", errors);
			//return "login";
			return ResponseEntity.badRequest().body(errors);
		}
        System.out.print("Aqui controller campos llenados: " + user.getUsername());
		boolean isAuth = service.authenticateUser(user);
		model.addAttribute("isAuth", isAuth);
		if(isAuth) {
			//return "redirect:/blogs/listar";
			return ResponseEntity.ok().body(true);
			//return ResponseEntity.badRequest().body(errors);
		}else {
			return ResponseEntity.ok().body(false);
		}
		
	}
	
	@PostMapping({"/signUp"})
	public ResponseEntity<?> signUp(Model model,@Valid @RequestBody User user, BindingResult result) {
		Map<String, Object> errors = new HashMap<>();
		
		
		if(result.hasFieldErrors()) {
			result.getFieldErrors().forEach(error -> {
				errors.put(error.getField(), "El campo: " + error.getField() + ' ' + error.getDefaultMessage());
			});
			
			model.addAttribute("user", user);
			model.addAttribute("existsUser", false);
			model.addAttribute("errors", errors);
			return ResponseEntity.badRequest().body(errors);
		}
		User existsUser = service.saveUser(user);
		model.addAttribute("existsUser", existsUser);
		if(existsUser != null) {
			return ResponseEntity.ok().body(existsUser);
		}else {
			return ResponseEntity.ok().body(false);
		}
		
	}
	
	@PutMapping("/users/update/{id}")
	public ResponseEntity<?> updateUser(Model model,@Valid @RequestBody User user, BindingResult result, @PathVariable Integer id) {
		Map<String, Object> errors = new HashMap<>();
		
		
		if(result.hasFieldErrors()) {
			result.getFieldErrors().forEach(error -> {
				errors.put(error.getField(), "El campo: " + error.getField() + ' ' + error.getDefaultMessage());
			});
			
			//model.addAttribute("user", user);
			//model.addAttribute("existsUser", false);
			//model.addAttribute("errors", errors);
			return ResponseEntity.badRequest().body(errors);
		}
		
		User existsUser = service.updateUser(user, id);
		model.addAttribute("existsUser", existsUser);
		if(existsUser != null) {
			return ResponseEntity.ok().body(existsUser);
		}else {
			return ResponseEntity.ok().body(false);
		}
		//return ResponseEntity.ok().body(updatedBlog != null ? updatedBlog : "No encontramos el blog a actualizar, intenta con otro");
	}
	
	@ModelAttribute("roles")
	public List<Role> getRoles(){
		return service.getRoles();
	}
	
	
	

}
