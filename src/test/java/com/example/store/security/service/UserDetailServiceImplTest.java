package com.example.store.security.service;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.store.security.model.Role;
import com.example.store.security.model.User;
import com.example.store.security.repository.UserRepository;

class UserDetailServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUser() {
        Role userRole = new Role();
        userRole.setName("User");        
        Role adminRole = new Role();
        adminRole.setName("Administrator");

        String username = "testadmin";
        String password = "testpass";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles(Set.of(userRole, adminRole));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_User")));
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_Administrator")));
        verify(userRepository).findByUsername(username);
    }

    @Test
    void failLoadUserMissing() {
        String username = "nonexistinguser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailService.loadUserByUsername(username));
        verify(userRepository).findByUsername(username);
    }
}