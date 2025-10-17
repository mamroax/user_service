package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User createUser(String name, String email, Integer age) throws Exception {
        if (name == null || name.isBlank() || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Name and email cannot be empty");
        }
        User user = new User(name, email, age);
        userDao.create(user);
        logger.info("User created via service: {}", user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public User updateUser(Long id, String name, String email, Integer age) throws Exception {
        Optional<User> existing = userDao.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("User not found with id " + id);
        }
        User user = existing.get();
        if (name != null && !name.isBlank()) user.setName(name);
        if (email != null && !email.isBlank()) user.setEmail(email);
        if (age != null) user.setAge(age);

        userDao.update(user);
        logger.info("User updated via service: {}", user);
        return user;
    }

    @Override
    public boolean deleteUser(Long id) throws Exception {
        return userDao.delete(id);
    }
}
