package com.bolsadeideas.springboot.blogpost.app.services;

import com.bolsadeideas.springboot.blogpost.app.entities.Blog;
import com.bolsadeideas.springboot.blogpost.app.entities.Role;
import com.bolsadeideas.springboot.blogpost.app.entities.User;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bolsadeideas.springboot.blogpost.app.repositories.RoleRepository;
import com.bolsadeideas.springboot.blogpost.app.repositories.UserRepository;

import jakarta.mail.MessagingException;

import com.bolsadeideas.springboot.blogpost.app.entities.User;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	public EmailService emailSender;
	
	@Autowired
    private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	public List<User> listUsers(String term) {
		if(term != null && !term.equals("")) {
			return repository.searchByTerm(term);
		}
		return repository.findAll();
	}
	
	public Page<User> listUsersPageable(Pageable pageable, String term){
		if(term != null && !term.equals("")) {
			List<User> users = repository.searchByTerm(term);
			
			int start = (int) pageable.getOffset();
		    int end = Math.min((start + pageable.getPageSize()), users.size());

		    List<User> pageContent = users.subList(start, end);
		    return new PageImpl<>(pageContent, pageable, users.size());
		}
		return repository.findAll(pageable);
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
        User userSaved = repository.save(user);
        if(userSaved != null) {
        	StringBuilder message = new StringBuilder("<h1>Welcome to Blogs Post</h1>");
			message.append("<br>");
			message.append("<img src='https://wallpaperaccess.com/full/6810534.jpg' style='width:20em; height:10em; border-radius:1em;'>");
			message.append("<br>");
			message.append("<p>Bienvenido ");
			user.getRoles().forEach(role -> {
				message.append("<strong>");
				message.append(role.getName());
				message.append("</strong>");
			});
			message.append("<strong> ");
			message.append(user.getUsername()); 
			message.append("</strong>");
			message.append(" Ahora eres parte de nuestra comunidad de Bloggeros ");
			message.append("esperamos y disfrutes de tu experiencia!");
			message.append("</p>");

        	try {
				emailSender.sendHtmlEmail("blogspost253@gmail.com", "Welcome to Blogs Post", message.toString());
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		return userSaved;
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
					if(user.getFoto() == null) {
						dbUserToUpdate.setFoto(dbUserToUpdate.getFoto());
					}else {
						dbUserToUpdate.setFoto(user.getFoto());
					}
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
				if(user.getFoto() == null) {
					dbUserToUpdate.setFoto(dbUserToUpdate.getFoto());
				}else {
					dbUserToUpdate.setFoto(user.getFoto());
				}
				return repository.save(dbUserToUpdate);
			}
		}
		return null;

	}
	
	public Boolean deleteUser(int id) {
		Optional<User> optionalUser = repository.findById(id);
		System.out.print(optionalUser.isPresent());
		if(optionalUser.isPresent()) {
			User dBUser = optionalUser.orElseThrow();
			System.out.print("Usuario existente en base de datos: " + dBUser);
			if(dBUser.getFoto() != null && dBUser.getFoto().length() > 0) {
				Path pathFoto = Paths.get("src/main/resources/static/uploads").resolve(dBUser.getFoto()).toAbsolutePath();
				File archivo = pathFoto.toFile();
				if(archivo.exists() && archivo.canRead()) {
					archivo.delete();
				}
			}
			repository.deleteById(id);
        	emailSender.sendEmail("blogspost253@gmail.com", "¿Tan Pronto te vas?", "Lamentamos que hayas decidio irte, esperamos que vuelvas pronto");
			return true;
		}else {
			return false;
		}
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
