package com.bolsadeideas.springboot.blogpost.app.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.bolsadeideas.springboot.blogpost.app.repositories.UserRepository;
import com.bolsadeideas.springboot.blogpost.app.security.filters.JwtAuthenticationFilter;
import com.bolsadeideas.springboot.blogpost.app.security.filters.JwtValidationFilter;
import com.bolsadeideas.springboot.blogpost.app.services.JpaUserDetailsService;

@Configuration
public class SpringSecurityConfig {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
    private AuthenticationConfiguration authenticationConfiguration;
	
	@Bean
    AuthenticationManager authenticationManager() throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
	
	@Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
	
	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
	
		http
		.authorizeHttpRequests((authz) -> authz
		.requestMatchers(HttpMethod.POST, "/loginUser").permitAll()
		.requestMatchers(HttpMethod.POST, "/signUp").permitAll()
		//.requestMatchers(HttpMethod.PUT, "*").permitAll()
		//.requestMatchers(HttpMethod.DELETE, "*").permitAll()
        .requestMatchers(HttpMethod.GET, "/blogs/**").permitAll()
        .requestMatchers(HttpMethod.DELETE, "/blogs/**").permitAll()
        .requestMatchers(HttpMethod.PUT, "/blogs/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/blogs/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/users").permitAll()
        .requestMatchers(HttpMethod.POST, "/*").permitAll()
        .requestMatchers(HttpMethod.GET, "/upload/*").permitAll()
        .requestMatchers(HttpMethod.PUT, "/users/**").permitAll()
        .requestMatchers(HttpMethod.PUT, "/user/**").permitAll()
        .requestMatchers(HttpMethod.PUT, "/profile/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/users/*").permitAll()
        .requestMatchers(HttpMethod.GET, "/requestResource/*").permitAll()
        .requestMatchers(HttpMethod.DELETE, "/delete/*").permitAll()
        //.requestMatchers(HttpMethod.POST, "/signUp").permitAll()
        //.requestMatchers(HttpMethod.POST, "/login").permitAll()
        .anyRequest().authenticated())
        .cors(cors -> cors.configurationSource(configurationSource()))
        .addFilter(new JwtAuthenticationFilter(authenticationManager(), repository)) // Add filter for JwtAuthentication (When login)
        //.addFilter(new JwtValidationFilter(authenticationManager()))
        .csrf(config -> config.disable())
        .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
		
    }
	
	@Bean
    FilterRegistrationBean<CorsFilter> corsFilter(){
		    // Instanciamos la clase FilterRegistrationBean pasandole la configuracion que hicimos en el metodo anterior.
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(new CorsFilter(configurationSource()));
        // Establecemos la mas alta prioridad para este filtro
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }
	
	@Bean
    CorsConfigurationSource configurationSource(){
        CorsConfiguration config = new CorsConfiguration(); // Instanciamos la clase
        config.setAllowedOriginPatterns(Arrays.asList("*")); // Especificamos que urls externas tiene acceso a nuestra aplicacion
        // En este caso permitimos el acceso a todas.
        // configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200","https://localhost","*")); // Otra alternativa al metodo de arriba
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT")); // Especificamos a que metodos tienen acceso esas urls
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // Especificamos a que headers tienen acceso esas urls
        config.setAllowCredentials(true); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Especificamos a que rutas se 
        //le aplica la configuración de CORS, en este caso a todas las rutas de la app

        return source; // retornamos la clase de configuración.
    }

}
