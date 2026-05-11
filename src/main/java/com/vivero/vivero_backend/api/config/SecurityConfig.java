package com.vivero.vivero_backend.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//carpeta: config/SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
@Autowired
private JwtRequestFilter jwtRequestFilter;

 @Bean
 public BCryptPasswordEncoder passwordEncoder() {
     return new BCryptPasswordEncoder();
 }

 @Bean
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     http
         .csrf(csrf -> csrf.disable()) 
         .authorizeHttpRequests(auth -> auth
        		    .requestMatchers("/api/auth/login").permitAll()    // SOLO el login es público
        		    .requestMatchers("/api/auth/register").authenticated() // El registro ahora pide TOKEN
        		    .anyRequest().authenticated()
        		)
         .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
     
     http.addFilterBefore(jwtRequestFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
     
     return http.build();
 }
}
