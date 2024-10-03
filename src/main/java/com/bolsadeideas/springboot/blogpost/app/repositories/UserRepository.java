package com.bolsadeideas.springboot.blogpost.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.blogpost.app.entities.User;

public interface UserRepository extends CrudRepository<User, Integer>{
	
	public List<User> findAll();
	
	public Page<User> findAll(Pageable page);
	
	public Optional<User> findById(Integer id);
	
	public Optional<User> findByUsernameAndPassword(String username, String password);
	
	public Optional<User> findByUsername(String username);
	
//	@Query("SELECT u.username, r.name\r\n"
//			+ "FROM User u\r\n"
//			+ "JOIN Role ON emp.department_id = dep.id")
    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.username LIKE %?1% OR r.name LIKE ?1")
	public List<User> searchByTerm(String term);

}
