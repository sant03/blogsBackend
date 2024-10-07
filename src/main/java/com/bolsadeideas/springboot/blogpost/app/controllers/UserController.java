package com.bolsadeideas.springboot.blogpost.app.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tomcat.util.digester.ArrayStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import com.bolsadeideas.springboot.blogpost.app.entities.Blog;
import com.bolsadeideas.springboot.blogpost.app.entities.Role;
import com.bolsadeideas.springboot.blogpost.app.entities.User;
import com.bolsadeideas.springboot.blogpost.app.services.UserService;
import com.bolsadeideas.springboot.blogpost.app.util.paginator.PageRender;

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
	public ResponseEntity<?> listarUsuarios(Model model, @RequestParam(required=false) String term, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="5") int size){
		
		PageRequest pageRequest = PageRequest.of(page, size);
		
		Page<User> users = service.listUsersPageable(pageRequest , term);
		PageRender<User> pageRender = new PageRender<User>("/users", users);
		
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("users", users);
		return ResponseEntity.ok().body(pageRender);
	}
	
	@GetMapping("/users/{id}")
	public ResponseEntity<?> listarUsuario(Model model, @PathVariable Integer id){
		User user = service.findById(id);
		if(user == null) {
			//return "redirect:/users";
			return ResponseEntity.badRequest().body(false);
		}
		model.addAttribute("existsUser", false);
		model.addAttribute("user", user);
		//return "user";
		return ResponseEntity.ok().body(user);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteUsuario(Model model, @PathVariable Integer id){
		User user = service.deleteUser(id);
		System.out.print("Usuaurio despues de eliminar: " +  user);
		if(user == null) {
			//return "redirect:/users";
			return ResponseEntity.badRequest().body(false);
		}
		model.addAttribute("existsUser", false);
		model.addAttribute("user", user);
		//return "user";
		return ResponseEntity.ok().body(user);
	}
	
	@GetMapping("upload/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename){
		Path pathFoto = Paths.get("src//main//resources//static/uploads").resolve(filename).toAbsolutePath();
		Resource recurso = null;
		try {
			recurso = new UrlResource(pathFoto.toUri());
			if(!recurso.exists() && !recurso.isReadable()) {
				throw new RuntimeException("Error no se puede leer la imagen" + pathFoto.toString());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"").body(recurso);
		
	}
	
	@PostMapping("/upload")
	public ResponseEntity<?> subirImagen(@RequestParam("file") MultipartFile foto, @RequestParam int id){
		if(!foto.isEmpty()) {
			User user = service.findById(id);
			if(user != null) {
				System.out.print("Objeto Blog: " + user.toString());
				System.out.print("Validacion: " + (user.getFoto() != null && user.getFoto().length() > 0));
				if(user.getFoto() != null && user.getFoto().length() > 0) {
					Path pathFoto = Paths.get("src/main/resources/static/uploads").resolve(user.getFoto()).toAbsolutePath();
					File archivo = pathFoto.toFile();
					if(archivo.exists() && archivo.canRead()) {
						if(archivo.delete()) {
							System.out.print("Imagen eliminada correctamente");
						}else {
							System.out.print("la imagen no se eliminó");
						}
					}
				}else {
					System.out.print("Imagen no existe, es null");
				}
				Path directorioRecursos = Paths.get("src//main//resources//static/uploads");
				String roothPath = directorioRecursos.toFile().getAbsolutePath();
				try {
			        String uniqueFileName = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
					byte[] bytes = foto.getBytes();
					Path rutaCompleta =  Paths.get(roothPath + "//" + uniqueFileName);
					Files.write(rutaCompleta, bytes);
					user.setFoto(uniqueFileName);
					service.updateUser(user, user.getId());
					return ResponseEntity.ok().body(true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		return ResponseEntity.ok().body(false);

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
			return ResponseEntity.ok().body(isAuth);
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
