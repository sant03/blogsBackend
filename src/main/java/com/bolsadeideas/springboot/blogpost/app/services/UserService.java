package com.bolsadeideas.springboot.blogpost.app.services;

import com.bolsadeideas.springboot.blogpost.app.entities.Blog;
import com.bolsadeideas.springboot.blogpost.app.entities.Role;
import com.bolsadeideas.springboot.blogpost.app.entities.User;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bolsadeideas.springboot.blogpost.app.repositories.RoleRepository;
import com.bolsadeideas.springboot.blogpost.app.repositories.UserRepository;
import com.bolsadeideas.springboot.blogpost.app.entities.User;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	public List<User> listUsers() {
		return repository.findAll();
	}

	public User findById(Integer id) {

		Optional<User> userOptional = repository.findById(id);
		if (!userOptional.isPresent()) {
			// return ResponseEntity.badRequest().body("Lo sentimos, no encontramos el
			// usuario, prueba con un correo y contraseña diferente");
			return null;
		} else {
			User user = userOptional.orElseThrow();
			return user;
		}

	}

	public User saveUser(User user) {
		Optional<User> optionalUser = repository.findByUsername(user.getUsername());
		if (optionalUser.isPresent()) {
			return null;
		}
		String passwordEncoded = passwordEncoder.encode(user.getPassword()); // Ciframos la contraseña
        user.setPassword(passwordEncoded); // Actualizamos la contraseña
		return repository.save(user);
	}

	public User updateUser(User user, Integer id) {
		Optional<User> optionalUser = repository.findByUsername(user.getUsername());
		if (optionalUser.isPresent()) {
			User dbUser = optionalUser.orElseThrow();
			System.out.print("Ids: " + dbUser.getId() + "," + id + " Usernames: " + dbUser.getUsername() + ", " + user.getUsername());
			System.out.print("Validacion:" + (dbUser.getId() != id && dbUser.getUsername() == user.getUsername()));
			/*Extraño: Validación no es true si valido: (dbUser.getId() != id && dbUser.getUsername() == user.getUsername())
				Pero si es true si valido dbUser.getId() == id && dbUser.getUsername() != user.getUsername(), pasando los valores
				NO TRUE: 5 != 6 && "ema@gmail.com" == "ema@gmail.com"
				TRUE: 5 == 6 && "ema@gmail.com" != "ema@gmail.com"
				
				Solucion: Para comparar la igualdad de dos strings en JAVA se debe user el method equals()
				TRUE: 5 != 6 && "ema@gmail.com".equals("ema@gmail.com")
			 */
			if(dbUser.getId() != id && dbUser.getUsername().equals(user.getUsername())) { 
				return null;
			}else {
				Optional<User> optionalUserToUpdate = repository.findById(id);
				if (optionalUserToUpdate.isPresent()) {
					User dbUserToUpdate = optionalUserToUpdate.orElseThrow(null);
					dbUserToUpdate.setUsername(user.getUsername());
					dbUserToUpdate.setPassword(user.getPassword());
					dbUserToUpdate.setRoles(user.getRoles());
					return repository.save(dbUserToUpdate);
				}
			}			
		}else {
			Optional<User> optionalUserToUpdate = repository.findById(id);
			if (optionalUserToUpdate.isPresent()) {
				User dbUserToUpdate = optionalUserToUpdate.orElseThrow(null);
				dbUserToUpdate.setUsername(user.getUsername());
				dbUserToUpdate.setPassword(user.getPassword());
				dbUserToUpdate.setRoles(user.getRoles());
				return repository.save(dbUserToUpdate);
			}
		}
		return null;

	}

	public Boolean authenticateUser(User user) {
		Optional<User> optionalUser = repository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
		if (optionalUser.isPresent()) {
	        System.out.print("Aqui en User Service:" + user.getUsername());
			return true;
		}
		return false;
	}

	public List<Role> getRoles() {
		return roleRepository.findAll();
	}

}
