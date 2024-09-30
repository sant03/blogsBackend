package com.bolsadeideas.springboot.blogpost.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.blogpost.app.entities.User;

public interface UserRepository extends CrudRepository<User, Integer>{
	
	public List<User> findAll();
	
	public Optional<User> findById(Integer id);
	
	public Optional<User> findByUsernameAndPassword(String username, String password);
	
	public Optional<User> findByUsername(String username);

}
