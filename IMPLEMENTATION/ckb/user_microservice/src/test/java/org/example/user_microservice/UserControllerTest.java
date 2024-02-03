package org.example.user_microservice;

import org.example.user_microservice.controller.UserController;
import org.example.user_microservice.model.UserModel;
import org.example.user_microservice.service.UserService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserControllerTest.class)

public class UserControllerTest {
    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    @Test
    @Order(1)
    void testGetUserRole() {
        String username = "testUser";
        Integer userRole = 1;
        when(userService.getUserRole(username)).thenReturn(userRole);
        ResponseEntity<Integer> responseEntity = userController.getUserRole(username);
        // Verify the interaction with userService
        verify(userService, times(1)).getUserRole(username);
        // Verify the ResponseEntity
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userRole, responseEntity.getBody());
    }

    @Test
    @Order(2)
    void testCheckUserExists() {
        String username = "testUser";
        boolean userExists = true;
        when(userService.isUserExists(username)).thenReturn(userExists);
        ResponseEntity<Boolean> responseEntity = userController.checkUserExists(username);
        // Verify the interaction with userService
        verify(userService, times(1)).isUserExists(username);
        // Verify the ResponseEntity
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userExists, responseEntity.getBody());
    }

    @Test
    @Order(3)
    void testSaveUser() {
        String username = "testUser";
        int role = 1;
        userController.saveUser(username, role);
        // Verify the interaction with userService
        verify(userService, times(1)).saveUser(new UserModel(username, role));
    }

    @Test
    @Order(4)
    void testGetNotifications() {
        String username = "testUser";
        List<String> mockNotifications = Arrays.asList("Notification 1", "Notification 2");
        when(userService.getNotifications(username)).thenReturn(mockNotifications);
        List<String> result = userController.getNotifications(username);
        // Assert the result
        assertEquals(mockNotifications, result);
    }






}
