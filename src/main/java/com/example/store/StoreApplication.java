package com.example.store;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.store.security.model.User;
import com.example.store.security.model.Role;

import com.example.store.security.repository.UserRepository;
import com.example.store.security.repository.RoleRepository;

/**
 * Application class. It contains a start-up bean that creates the demo users and roles.
 */
@SpringBootApplication
public class StoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreApplication.class, args);
	}

	@Value("${default.admin.password}")
	private String ENV_ADMIN_DEFAULT_PASS;
	@Value("${default.user.password}")
	private String ENV_USER_DEFAULT_PASS;

	@Bean
	@Profile("!IntegrationTest")
    public CommandLineRunner insertDefaultUsers(
			UserRepository userRepository, RoleRepository reolRepository, PasswordEncoder passwordEncoder) {
        return args -> {

			Role userRole = new Role();
			userRole.setName("User");
			reolRepository.save(userRole);

			Role adminRole = new Role();
			adminRole.setName("Administrator");
			reolRepository.save(adminRole);

			User admin = new User();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode(ENV_ADMIN_DEFAULT_PASS));
			admin.setRoles(Set.of(userRole, adminRole));
			userRepository.save(admin);

			User user = new User();
			user.setUsername("user");
			user.setPassword(passwordEncoder.encode(ENV_USER_DEFAULT_PASS));
			user.setRoles(Set.of(userRole));
			userRepository.save(user);
			System.out.println("Default users created.");

        };
    }
}
