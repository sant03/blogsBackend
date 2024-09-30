package com.bolsadeideas.springboot.blogpost.app.security.filters;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bolsadeideas.springboot.blogpost.app.entities.User;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import static com.bolsadeideas.springboot.blogpost.app.security.TokenJwtConfig.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private AuthenticationManager authenticationManager;
    

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
        System.out.print("Request" + request);
		// Inicializamos variables de usuario
		User user = null;
        String username = null;
        String password = null;
        
        // Recuperar el usuario del request (usuario con el que solicita el cliente la autenticacion)
        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            username = user.getUsername();
            password = user.getPassword();
        } catch (StreamReadException e) { // Capturamos exepcion al leer el request
            e.printStackTrace();
        } catch (DatabindException e) {// Capturamos exepction al convertir a la clase User
            e.printStackTrace();
        } catch (IOException e) {// Capturamos cualquier otra expection
            e.printStackTrace();
        }
        
        System.out.print("Autenticacion exitosa username o password correcto Username:" + username + "Password: " + password);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);// Creamos el token
        // el methodo authenticate valida el usuario del request con el usuario JPA
        return authenticationManager.authenticate(authenticationToken); // Llamamos al metodo de autenticacion
        
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		// TODO Auto-generated method stub
		
		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult.getPrincipal();
        String username = user.getUsername();
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
        Claims claims = Jwts.claims()
        .add("authoritiens", roles)
        .add("username", username)
        .build();
        
        String token = Jwts.builder()
                .subject(username)
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .issuedAt(new Date())
                .signWith(SECRET_KEY)
                .compact();
                
                response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token );
                Map<String, String> body = new HashMap<>();
                body.put("token", token);
                body.put("username", username);
                body.put("message", String.format("Hola %s has iniciado sesion con exito", username));
                response.getWriter().write(new ObjectMapper().writeValueAsString(body));
                response.setContentType(CONTENT_TYPE);
                response.setStatus(200);
        // Guardar el token en una sesión para enviarlo a la vista
        //request.getSession().setAttribute("jwtToken", token);
        //System.out.print("Autenticacion exitos username o password correcto Token:" + token);
        // Redirigir al usuario a una página con el token en el modelo
        //response.sendRedirect("/blogs/listar");  // O la página que necesites
		
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		
		Map<String, String> body = new HashMap<>();
        body.put("message", "Error en la autenticacion username o password incorrecto");
        body.put("error",failed.getMessage());
        System.out.print("Error en la autenticacion username o password incorrecto");
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType(CONTENT_TYPE);
        //response.sendRedirect("/signUp");  // O la página que necesites
	}

}
