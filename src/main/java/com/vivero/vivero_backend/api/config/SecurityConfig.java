package com.vivero.vivero_backend.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.vivero.vivero_backend.api.service.UserDetailsServiceImpl;

//carpeta: config/SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) 
	        throws Exception {
	    return config.getAuthenticationManager();
	}
	
@Autowired
private JwtRequestFilter jwtRequestFilter;

 @Bean
 public BCryptPasswordEncoder passwordEncoder() {
     return new BCryptPasswordEncoder();
 }

 @Bean
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     http
         .cors(org.springframework.security.config.Customizer.withDefaults())
         .csrf(csrf -> csrf.disable()) 
         .authorizeHttpRequests(auth -> auth
        		    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        		    .requestMatchers("/api/auth/login").permitAll()
        		    .requestMatchers("/error").permitAll()
        		    
        		    // Permitir acceso a Swagger UI sin autenticación
        		    .requestMatchers("/swagger-ui/**").permitAll()
        		    .requestMatchers("/swagger-ui.html").permitAll()
        		    .requestMatchers("/v3/api-docs/**").permitAll()
        		    .requestMatchers("/v3/api-docs").permitAll()

        		    // Solo ADMIN puede gestionar usuarios
        		    .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

        		    // ADMIN y USER pueden ver estadísticas y gestionar productos/clientes
        		    .requestMatchers("/api/estadisticas/**").hasAnyRole("ADMIN", "USER")
        		    .requestMatchers("/api/productos/**").hasAnyRole("ADMIN", "USER")
        		    .requestMatchers("/api/clientes/**").hasAnyRole("ADMIN", "USER")

        		    // Ventas: todos los roles pueden acceder
        		    .requestMatchers("/api/ventas/**").hasAnyRole("ADMIN", "USER", "EMPLOYEE")

        		    // Registro solo ADMIN
        		    .requestMatchers("/api/auth/register").hasRole("ADMIN")

        		    .anyRequest().authenticated()
        		)
         .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
         .userDetailsService(userDetailsService); 
     
     http.addFilterBefore(jwtRequestFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
     
     return http.build();
 }
//3. Definir la política de CORS 
 @Value("${cors.allowed-origins}")
 private String allowedOrigins;
 
 @Bean
 public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
	    org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
	    configuration.setAllowedOrigins(java.util.List.of(allowedOrigins.split(",")));
	    configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
	    configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
	    configuration.setAllowCredentials(true);

	    org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
}
