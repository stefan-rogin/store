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

/**
 * Access control configuration for the application. It's not active under the 
 * integration tests profile, to allow tests run without a login
 */
@Configuration
@EnableWebSecurity
@Profile("!IntegrationTest")
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    // Default password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security filter chain implementation. Rather than configuring roles per each controller method,
    // a more centralized solution was preferred, leveraging the REST approach. All requests are 
    // authenticated (except login/logout), DB chaniging web methods requiring Administrator role.
    // GET calls only require User role. There was no use case for other verbs to be implemented.
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
