package com.bolsadeideas.springboot.blogpost.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.blogpost.app.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{
	
	public List<Role> findAll();
	
	public Optional<Role> findById(Integer id);

}
