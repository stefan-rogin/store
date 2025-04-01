package com.example.store;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.store.security.service.UserDetailServiceImpl;

@SpringBootApplication
public class StoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreApplication.class, args);

	}

	// FIXME: Remove debug code
	@Bean
	public CommandLineRunner users(UserDetailServiceImpl users) {
		
		return (args) -> {
			UserDetails userDetails = users.loadUserByUsername("user");
			System.out.println(userDetails.toString());
		};
	}

}
