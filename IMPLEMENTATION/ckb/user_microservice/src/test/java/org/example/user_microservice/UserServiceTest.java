package org.example.user_microservice;

import org.example.user_microservice.model.UserModel;
import org.example.user_microservice.repository.UserRepository;
import org.example.user_microservice.service.UserService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserServiceTest.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    @Order(1)
    void testSaveUser() {
        UserModel user = new UserModel();
        user.setUsername("testUser");
        user.setRole(1);
        user.setNotification("testNotification");

        userService.saveUser(user);
        // Assert
        verify(userRepository).save(user);
    }

    @Test
    @Order(2)
    public void testGetAllUsers() {
        List<UserModel> expectedUsers = Arrays.asList(
                new UserModel("John", 0),
                new UserModel ("Jane", 0),
                new UserModel("Doe", 0)
        );
        when(userRepository.findAll()).thenReturn(expectedUsers);
        List<UserModel> actualUsers = userService.getAllUsers();
        // Assert
        assertEquals(expectedUsers, actualUsers);
        // Verify that userRepository.findAll() was called exactly once
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @Order(3)
    public void testGetUserRole() {
        String username = "john.doe";
        Integer expectedUserRole = 1;
        when(userRepository.getUserRole(username)).thenReturn(expectedUserRole);
        Integer actualUserRole = userService.getUserRole(username);
        // Assert
        assertEquals(expectedUserRole, actualUserRole);
        // Verify that userRepository.getUserRole(username) was called exactly once with the provided username
        verify(userRepository, times(1)).getUserRole(username);
    }

    @Test
    @Order(4)
    public void testGetUserModel() {
        String username = "John Doe";
        UserModel expectedUserModel = new UserModel( "John Doe", 1);
        when(userRepository.getUserModel(username)).thenReturn(expectedUserModel);
        UserModel actualUserModel = userService.getUserModel(username);
        // Assert
        assertEquals(expectedUserModel, actualUserModel);
        // Verify that userRepository.getUserModel(username) was called exactly once with the provided username
        verify(userRepository, times(1)).getUserModel(username);
    }

    @Test
    @Order(5)
    public void testIsUserExists_whenUserExists() {
        String existingUsername = "john.doe";
        when(userRepository.existsByUsername(existingUsername)).thenReturn(true);
        boolean userExists = userService.isUserExists(existingUsername);
        // Assert
        assertTrue(userExists);
        // Verify that userRepository.existsByUsername(existingUsername) was called exactly once with the provided username
        verify(userRepository, times(1)).existsByUsername(existingUsername);
    }

    @Test
    @Order(6)
    public void testIsUserExists_whenUserDoesNotExist() {
        String nonExistingUsername = "nonexistent.user";
        when(userRepository.existsByUsername(nonExistingUsername)).thenReturn(false);
        boolean userExists = userService.isUserExists(nonExistingUsername);
        // Assert
        assertFalse(userExists);
        // Verify that userRepository.existsByUsername(nonExistingUsername) was called exactly once with the provided username
        verify(userRepository, times(1)).existsByUsername(nonExistingUsername);
    }

    @Test
    @Order(7)
    public void testGetNotifications_whenNotificationsFound() {
        String username = "John Doe";
        List<UserModel> mockUsers = List.of(
                new UserModel( "John Doe", 1, "Notification 1"),
                new UserModel( "John Doe", 1, "Notification 2")
        );
        when(userRepository.findAllByUsername(username)).thenReturn(mockUsers);
        List<String> notifications = userService.getNotifications(username);
        // Assert
        assertEquals(List.of("Notification 1", "Notification 2"), notifications);
        // Verify that userRepository.findAllByUsername(username) was called exactly once with the provided username
        verify(userRepository, times(1)).findAllByUsername(username);
    }

    @Test
    @Order(8)
    public void testGetNotifications_whenNoNotificationsFound() {
        String username = "john.doe";
        when(userRepository.findAllByUsername(username)).thenReturn(new ArrayList<>());
        List<String> notifications = userService.getNotifications(username);
        // Assert
        assertEquals(List.of("No notifications found"), notifications);
        // Verify that userRepository.findAllByUsername(username) was called exactly once with the provided username
        verify(userRepository, times(1)).findAllByUsername(username);
    }


}
