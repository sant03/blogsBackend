package com.bolsadeideas.springboot.blogpost.app.security.filters;

import static com.bolsadeideas.springboot.blogpost.app.security.TokenJwtConfig.CONTENT_TYPE;
import static com.bolsadeideas.springboot.blogpost.app.security.TokenJwtConfig.HEADER_AUTHORIZATION;
import static com.bolsadeideas.springboot.blogpost.app.security.TokenJwtConfig.PREFIX_TOKEN;
import static com.bolsadeideas.springboot.blogpost.app.security.TokenJwtConfig.SECRET_KEY;

import java.io.IOException;

import org.springframework.http.HttpStatus;
// import org.hibernate.mapping.Collection;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bolsadeideas.springboot.blogpost.app.security.SimpleGrantedAuthorityJsonCreator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Arrays;
// import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;





public class JwtValidationFilter extends BasicAuthenticationFilter{

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override // Get the token from headers
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
                String header = request.getHeader(HEADER_AUTHORIZATION);// Get the token from headers
                if(header == null || !header.startsWith(PREFIX_TOKEN)){
                    chain.doFilter(request, response);
                    return;
                }

                String token = header.replace(PREFIX_TOKEN, "");// Remover Bearer word get only token
                try {
                    Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload(); // Validate token with SECRET_KEY
                    String username = claims.getSubject(); // Get username from token
                    // String username2 = (String) claims.get("username");
                    Object authoritiesClaims = claims.get("authoritiens");// Get the authorities from token
                    System.out.print("CLAIMS: " + authoritiesClaims.toString());
                    
                    
                    
                    Collection<? extends GrantedAuthority> authorities = Arrays.asList( // Convert the authorities Object to Collection of type GrantheAuthority
                        new ObjectMapper()
                        .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                        .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class)
                    );
                    
                    authorities.forEach(authority -> {
                    	authority.toString().replace("authority", "");
                    });

                    // QUESTION: Why do we generate this token? Is this token replacing the already created token when login authentication?
                    // INFO: Upon successful authentication, the user’s details are encapsulated in a UsernamePasswordAuthenticationToken object and stored in the SecurityContextHolder.
                    // The Spring Security Authorization process is automatically invoked.
                    // The request is dispatched to the controller, and a successful JSON response is returned to the user.
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken); // SecurityContextHolder is where Spring Security stores the details of who is authenticated. Spring Security uses that information for authorization.
                    chain.doFilter(request, response);
                    
                } catch (JwtException e) {
                    Map<String, String> body = new HashMap<>();
                    body.put("error", e.getMessage());
                    body.put("message", "El token JWT es invalido");

                    response.getWriter().write(new ObjectMapper().writeValueAsString(body));
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(CONTENT_TYPE);

                }
    }

    

}
