package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        // MockitoExtension handles init
    }

    @Test
    void createUser_validInput_callsDaoAndReturnsUser() throws Exception {
        when(userDao.create(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser("Nikolai", "mamroax@gmail.com", 28);

        assertNotNull(created);
        assertEquals("Nikolai", created.getName());
        assertEquals("mamroax@gmail.com", created.getEmail());
        assertEquals(28, created.getAge());
        verify(userDao, times(1)).create(any(User.class));
    }

    @Test
    void createUser_invalidName_throws() throws Exception{
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(" ", "x@x.com", 20));
        verify(userDao, never()).create(any());
    }

    @Test
    void getAllUsers_delegatesToDao() {
        List<User> stub = List.of(new User("A", "a@example.com", 20));
        when(userDao.findAll()).thenReturn(stub);

        List<User> result = userService.getAllUsers();
        assertEquals(1, result.size());
        verify(userDao, times(1)).findAll();
    }

    @Test
    void getUserById_delegatesToDao() {
        User u = new User("A", "a@example.com", 20);
        when(userDao.findById(5L)).thenReturn(Optional.of(u));


        Optional<User> found = userService.getUserById(5L);
        assertTrue(found.isPresent());
        assertEquals(5L, found.get().getId());
        verify(userDao).findById(5L);
    }

    @Test
    void updateUser_existingUser_updates() throws Exception {
        User existing = new User("Old", "old@example.com", 50);
        when(userDao.findById(10L)).thenReturn(Optional.of(existing));

        User updated = userService.updateUser(10L, "New", "new@example.com", 40);

        assertEquals("New", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
        assertEquals(40, updated.getAge());
        verify(userDao).update(existing);
    }

    @Test
    void updateUser_missingUser_throws() throws Exception{
        when(userDao.findById(123L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(123L, "N", "e@e", 1));
        verify(userDao, never()).update(any());
    }

    @Test
    void deleteUser_delegatesToDao() throws Exception {
        when(userDao.delete(7L)).thenReturn(true);
        boolean res = userService.deleteUser(7L);
        assertTrue(res);
        verify(userDao).delete(7L);
    }
}
