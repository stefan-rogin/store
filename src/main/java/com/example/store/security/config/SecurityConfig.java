package com.example.store.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@Profile("!IntegrationTest")
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                request -> request
                        .requestMatchers(HttpMethod.POST, "/products/**").hasRole("Administrator")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("Administrator")
                        .requestMatchers(HttpMethod.PATCH, "/products/**").hasRole("Administrator")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("Administrator")
                        .requestMatchers("/products/**").hasRole("User")
                        .anyRequest().authenticated()
        )
                .formLogin(form -> form.permitAll())
                .logout(logout -> logout.permitAll())
                .csrf(csrf -> csrf.disable())
                .userDetailsService(userDetailsService);

        return http.build();
    }
}
