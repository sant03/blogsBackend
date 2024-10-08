package com.bolsadeideas.springboot.blogpost.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.blogpost.app.entities.Resource;


public interface ResourceRepository extends JpaRepository<Resource, Integer>{
	
	public List<Resource> findAll();
	
	public Optional<Resource> findById(int id);

}
