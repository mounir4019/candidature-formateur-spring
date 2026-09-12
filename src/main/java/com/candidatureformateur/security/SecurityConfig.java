package com.candidatureformateur.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private JwtAuthConverter jwtAuthConverter;
    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;
    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf->csrf.disable())
                .headers(h->h.frameOptions(fo->fo.disable()))
                
                

                // api candidats  
                .authorizeHttpRequests(ar->ar.requestMatchers(HttpMethod.GET,"/api/candidats").hasAnyRole("ACAD","LEADER")
                                             .requestMatchers(HttpMethod.GET,"/api/candidats/listeDocumentPdfMerge/**").hasAnyRole("ACAD","LEADER")
                                             .requestMatchers(HttpMethod.DELETE,"/api/candidats/**").hasAnyRole("ACAD","LEADER")
                                             .requestMatchers(HttpMethod.POST,"/api/candidats/uploadEvaluationsCandidatures/**").hasAnyRole("ACAD","LEADER"))
                // concours
                //.authorizeHttpRequests(ar->ar.requestMatchers("/api/concours/structure/**").hasAnyRole("ACAD","LEADER"))
                .authorizeHttpRequests(ar->ar.requestMatchers( "/api/concours/**").hasAnyRole("ACAD","LEADER"))
                //sessions
                .authorizeHttpRequests(ar->ar.requestMatchers("/api/sessions/byStructure/**").permitAll()
                                            .requestMatchers(HttpMethod.GET, "/api/sessions/session/**").permitAll()
                                            .requestMatchers(HttpMethod.GET, "/api/sessions/session/**").permitAll()
                                            .requestMatchers(HttpMethod.GET, "/api/sessions/sessionByRef/**").permitAll()
                                            .requestMatchers(HttpMethod.POST, "/api/sessions/**").hasAnyRole("ACAD","LEADER")
                                            .requestMatchers(HttpMethod.PUT, "/api/sessions/**").hasAnyRole("ACAD","LEADER")
                                            .requestMatchers(HttpMethod.DELETE, "/api/sessions/**").hasAnyRole("ACAD","LEADER")
                                            .requestMatchers(HttpMethod.GET, "/api/sessions/**").hasAnyRole("ACAD","LEADER")
                                            )
                //documentSessions
                .authorizeHttpRequests(ar->ar.requestMatchers( "/api/documentSessions/**").hasAnyRole("ACAD","LEADER"))
                //validationCandidat
                .authorizeHttpRequests(ar->ar.requestMatchers( "/api/validationCandidat/**").hasAnyRole("ACAD","LEADER"))
                // swagger
                .authorizeHttpRequests(ar->ar.requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll())
                // .authorizeHttpRequests(ar->ar.anyRequest().authenticated())
               .authorizeHttpRequests(ar->ar.anyRequest().permitAll())
               // .oauth2ResourceServer(o2->o2.jwt(Customizer.withDefaults()
               .oauth2ResourceServer(o2->o2.jwt(jwt->jwt.jwtAuthenticationConverter(jwtAuthConverter) ))
                .build();
    }
 
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigin)); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
 
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
 
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
  
}