package com.example.userservice.service;

import com.example.userservice.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String name, String email, Integer age) throws Exception;
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User updateUser(Long id, String name, String email, Integer age) throws Exception;
    boolean deleteUser(Long id) throws Exception;
}
