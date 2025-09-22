package com.batista.tickets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import com.batista.tickets.filters.UserProvisioningFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, UserProvisioningFilter userProvisioningFilter)
      throws Exception {
    System.out.println(">>> SecurityFilterChain bean creado");
    http
        .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.GET, "/api/v1/events").permitAll()
            .anyRequest().authenticated())
        .csrf(crsf -> crsf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(
            Customizer.withDefaults()))
        .addFilterAfter(userProvisioningFilter, BearerTokenAuthenticationFilter.class);

    return http.build();
  }
}
