package com.bolsadeideas.springboot.blogpost.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.blogpost.app.entities.Blog;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
	
	public List<Blog> findAll();
	
	public Page<Blog> findAll(Pageable pageable);
	
	@Query("SELECT b From Blog b where b.id=?1")
	public Optional<Blog> findById(Integer id);

}
