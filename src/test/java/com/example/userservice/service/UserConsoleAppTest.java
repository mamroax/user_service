package com.example.userservice.service;

import com.example.userservice.model.User;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


class UserConsoleAppTest {

    private UserService userService;
    private InputStream originalIn;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        originalIn = System.in;
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
    }

    @Test
    void testListCommand() {
        String input = "list\nexit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        when(userService.getAllUsers()).thenReturn(List.of(new User("Alice", "alice@example.com", 25)));

        new UserConsoleApp(userService).start();

        verify(userService).getAllUsers();
    }

    @Test
    void testCreateCommand() throws Exception {
        String input = "create\nJohn\njohn@example.com\n22\nexit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        new UserConsoleApp(userService).start();

        verify(userService).createUser("John", "john@example.com", 22);
    }
}
