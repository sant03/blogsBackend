package com.bolsadeideas.springboot.blogpost.app.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import com.bolsadeideas.springboot.blogpost.app.entities.Blog;
import com.bolsadeideas.springboot.blogpost.app.services.BlogService;
import com.bolsadeideas.springboot.blogpost.app.util.paginator.PageRender;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/blogs")
@SessionAttributes("blog")
public class BlogController {
	
	@Autowired
	private BlogService service;
	
	private List<String> tags = new ArrayList<>();
	
	@GetMapping("/listar")
    @PreAuthorize("hasAnyRole({'LECTOR', 'ADMIN', 'EDITOR'})")
	public ResponseEntity<?> obtenerBlogs(Model model ,@RequestParam(required=false) String term, @RequestParam(defaultValue="0") int page){
		
		Pageable pageRequest = PageRequest.of(page, 5);
		
		Page<Blog> blogs = service.listarBlogsPageable(pageRequest, term);
		PageRender<Blog> pageRender = new PageRender<Blog>("/listar", blogs);
		//List<Blog> blogs = service.listBlogs(term);
		model.addAttribute("blogs", blogs);
		model.addAttribute("page", pageRender);
		return ResponseEntity.ok().body(pageRender);
	}
	
	@GetMapping("/listar/{id}")
	public ResponseEntity<?> obtenerBlogPorId(Model model, @PathVariable Integer id, SessionStatus status){
		Blog blog = service.findById(id);

		model.addAttribute("blog", blog);
		status.setComplete();
		return ResponseEntity.ok().body(blog);
	}
	
	@GetMapping("/crear")
	public String crearBlog(@SessionAttribute(required=false) Blog blog, Model model) {
		if(blog != null) {
			model.addAttribute("blog", blog);
			model.addAttribute("tags", tags);
			return "crearBlog";
		}
		Blog newblog = new Blog();
		model.addAttribute("blog", newblog);
		return "crearBlog";
		
	}
	
	@GetMapping("/update/{id}")
	public String updateBlog(@SessionAttribute(required=false) Blog blog, Model model, @PathVariable Integer id) {
		if(blog != null) {
			model.addAttribute("blog", blog);
			model.addAttribute("tags", tags);
			return "crearBlog";
		}
		
		Blog dbBlog = service.findById(id);
		if(dbBlog != null) {
			this.tags = dbBlog.stringToArray();
			model.addAttribute("blog", dbBlog);
			model.addAttribute("tags", tags);
			return "crearBlog";
		}
		return "redirect:/blogs/listar";
		
	}
	
	@PostMapping("/upload")
	public ResponseEntity<?> subirImagen(@RequestParam("file") MultipartFile foto){
		if(!foto.isEmpty()) {
			Path directorioRecursos = Paths.get("src//main//resources//static/uploads");
			String roothPath = directorioRecursos.toFile().getAbsolutePath();
			try {
				byte[] bytes = foto.getBytes();
				Path rutaCompleta =  Paths.get(roothPath + "//" + foto.getOriginalFilename());
				Files.write(rutaCompleta, bytes);
				return ResponseEntity.ok().body(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return ResponseEntity.ok().body(false);

	}
	
	@PostMapping("/crear")
	public ResponseEntity<?> crearBlog(Model model, @Valid @RequestBody Blog blog, BindingResult result) {
		Map<String, Object> errors = new HashMap<>();

		if(result.hasFieldErrors()) {
			result.getFieldErrors().forEach(error -> {
				errors.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage());
			});
			//return ResponseEntity.badRequest().body(errors);
			model.addAttribute("errors", errors);
			model.addAttribute("blog", blog);
			return ResponseEntity.ok().body(false);

		}
		Blog newBlog = service.crearBlog(blog);
		//return "redirect:/blogs/listar/" + newBlog.getId();
		return ResponseEntity.ok().body(newBlog);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateBlog(Model model, @Valid @RequestBody Blog blog, BindingResult result , @PathVariable Integer id) {
		Map<String, Object> errors = new HashMap<>();
		
		if(result.hasFieldErrors()) {
			result.getFieldErrors().forEach(error -> {
				errors.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage());
			});
			return ResponseEntity.badRequest().body(errors);
			//model.addAttribute("errors", errors);
			//model.addAttribute("blog", blog);
			//return "crearBlog";

		}
		
		Blog updatedBlog = service.updateBlog(blog, id);
		//return "redirect:/blogs/listar/" + updatedBlog.getId();
		return ResponseEntity.ok().body(updatedBlog);
	}
	
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Integer id){
		Blog result = service.delete(id);
		//return ResponseEntity.badRequest().body(result != null ? result : "No encontramos el blog a eliminar");
		return ResponseEntity.ok().body(result);

	}
	
	@GetMapping("/tags")
	public String getTags(@SessionAttribute(required=false) Blog blog, Model model, @RequestParam(required=false) String tag) {
		if(tag != null && tag != "") {
			tags.add(tag);
			blog.setTags(tags.toString().replace("[", "").replace("]", ""));
		}
		if(blog.getId() != null) {
			return "redirect:/blogs/update/" + blog.getId();
		}else {
			return "redirect:/blogs/crear";
		}
		
	}
	
	@GetMapping("/deleteTag")
	public String deleteTags(@SessionAttribute(required=false) Blog blog, Model model, @RequestParam(required=false) String tag) {
			tags.remove(tag);
			blog.setTags(tags.toString());
		return "redirect:/blogs/crear";
		
	}
	
	
	

}
