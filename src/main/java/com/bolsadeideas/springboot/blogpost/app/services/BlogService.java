package com.bolsadeideas.springboot.blogpost.app.services;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bolsadeideas.springboot.blogpost.app.entities.Blog;
import com.bolsadeideas.springboot.blogpost.app.repositories.BlogRepository;
import com.bolsadeideas.springboot.blogpost.app.util.paginator.PageRender;

@Service
public class BlogService {
	
	@Autowired
	private BlogRepository repository;
	
	public List<Blog> listBlogs(String term){
		if(term != null && term != "") {
			List<Blog> dbresult = repository.findAll();
			List<Blog> filteredResult = new ArrayList<>();
			if(dbresult.size() > 0) {
				dbresult.forEach(blog -> {
					List<String> tagsList = blog.stringToArray();
					System.out.print(tagsList);
					for (String tag : tagsList) {
				        if (term.equals(tag)) {
							filteredResult.add(blog);
				        }
				    }
				});
			}
			if(filteredResult != null && filteredResult.size() > 0) {
				System.out.print(filteredResult);
				// return ResponseEntity.ok().body(filteredResult);	
				return filteredResult;

			}/*else {
				return ResponseEntity.badRequest().body("Lo sentimos no hemos encontrado ningun blog");
			}*/
		}
		//return ResponseEntity.ok().body(repository.findAll());
		return repository.findAll();
	} 
	
	public Page<Blog> listarBlogsPageable(Pageable pageable, String term){
		if(term != null && term != "") {
			List<Blog> dbresult = repository.findAll();
			List<Blog> filteredResult = new ArrayList<>();
			if(dbresult.size() > 0) {
				dbresult.forEach(blog -> {
					List<String> tagsList = blog.stringToArray();
					System.out.print(tagsList);
					for (String tag : tagsList) {
				        if (term.equals(tag)) {
							filteredResult.add(blog);
				        }
				    }
				});
			}
			if(filteredResult != null && filteredResult.size() > 0) {
				System.out.print(filteredResult);
				// Convertimos un list en un objeto del tipo Page
				int start = (int) pageable.getOffset();
			    int end = Math.min((start + pageable.getPageSize()), filteredResult.size());

			    List<Blog> pageContent = filteredResult.subList(start, end);
			    return new PageImpl<>(pageContent, pageable, filteredResult.size());
				// return ResponseEntity.ok().body(filteredResult);	

			}/*else {
				return ResponseEntity.badRequest().body("Lo sentimos no hemos encontrado ningun blog");
			}*/
		}
		//return ResponseEntity.ok().body(repository.findAll());
		return repository.findAll(pageable);
	}
	
	public Blog findById(Integer id){
		Optional<Blog> optionalBlog = repository.findById(id);
		
		if(!optionalBlog.isEmpty()) {
			Blog blog = optionalBlog.orElseThrow();
			//return ResponseEntity.ok().body(blog);
			return blog;
		}/*else {
			return ResponseEntity.badRequest().body("Lo sentimos el blog no ha sido encontrador, pueba con uno diferente");
		}*/
		return null;
		
	}
	
	public Blog crearBlog(Blog blog){
		return repository.save(blog);
	}
	
	public Blog updateBlog(Blog blog, Integer id) {
		
		Optional<Blog> optionalBlog = repository.findById(id);
		if(optionalBlog.isPresent()){
			Blog dbBlog = optionalBlog.orElseThrow(null);
			dbBlog.setTitle(blog.getTitle());
			dbBlog.setContent(blog.getContent());
			dbBlog.setCategory(blog.getCategory());
			dbBlog.setTags(blog.getTags());
			if(blog.getFoto() == null) {
				dbBlog.setFoto(dbBlog.getFoto());
			}else {
				dbBlog.setFoto(blog.getFoto());
			}
			return repository.save(dbBlog);
		}
		return null;
		
	}
	
	public Blog delete(Integer id){
		Optional<Blog> optionalBlog = repository.findById(id);
		if(optionalBlog.isPresent()){
			Blog blogDb = optionalBlog.orElseThrow();	
			if(blogDb.getFoto() != null && blogDb.getFoto().length() > 0) {
				Path pathFoto = Paths.get("src/main/resources/static/uploads").resolve(blogDb.getFoto()).toAbsolutePath();
				File archivo = pathFoto.toFile();
				if(archivo.exists() && archivo.canRead()) {
					archivo.delete();
				}
			}
			repository.deleteById(id);

			return optionalBlog.orElseThrow();	
		}
		return null;	
	}
	
	

}
