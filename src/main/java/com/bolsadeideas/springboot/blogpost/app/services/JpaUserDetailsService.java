package com.bolsadeideas.springboot.blogpost.app.services;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.blogpost.app.entities.User;
import com.bolsadeideas.springboot.blogpost.app.repositories.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

	
	private final UserRepository repository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.repository = userRepository;
    }
	
	@Override
    @Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Optional<User> optionalUser = repository.findByUsername(username); // Buscamos usuario en db por su username
		if(optionalUser.isEmpty()) {
	        System.out.print("El usuario no EXISTEEEEEEEEEEEEEEE ....." + username);
			throw new UsernameNotFoundException(String.format("Username no existe en el sistema", username)); // Capturamos execption
		}
		
		User user = optionalUser.orElseThrow(); // Obtenemos el usuario
        List<GrantedAuthority> authorities = user.getRoles().stream() // Convertimos cada rol a un objeto del tipo SimpleGrantedAuthority
        .map(role -> new SimpleGrantedAuthority(role.getCode()))
        .collect(Collectors.toList()); //  y los agregamos a una lista del tipo GrantedAuthority
        
        
        System.out.print("Jpa User Detail Service:" + user.getUsername() + "Password: " + user.getPassword() + "Roles: " + authorities);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), // Retornamos el USER de JPA con los datos obtenidos de la base de Datos. username, password, is enabled, authorities (roles)
	        user.getPassword(),
	        true,
	        true,
	        true,
	        true,
	        authorities
        );
        
	}

}
