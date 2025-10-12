package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.dao.UserDaoImpl;
import com.example.userservice.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserConsoleService {

    private static final Logger logger = LogManager.getLogger(UserConsoleService.class);
    private final UserDao userDao = new UserDaoImpl();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        printHelp();
        while (true) {
            System.out.print("\n> ");
            String cmd = scanner.nextLine().trim();
            if (cmd.isEmpty()) continue;
            switch (cmd.toLowerCase()) {
                case "create": handleCreate(); break;
                case "list": handleList(); break;
                case "get": handleGet(); break;
                case "update": handleUpdate(); break;
                case "delete": handleDelete(); break;
                case "help": printHelp(); break;
                case "exit": System.out.println("Bye!"); return;
                default:
                    System.out.println("Unknown command. Type 'help' for commands.");
            }
        }
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println(" create  - create new user");
        System.out.println(" list    - list all users");
        System.out.println(" get     - get user by id");
        System.out.println(" update  - update user");
        System.out.println(" delete  - delete user by id");
        System.out.println(" help    - show help");
        System.out.println(" exit    - exit");
    }

    private void handleCreate() {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Age: ");
            Integer age = parseInteger(scanner.nextLine().trim());

            User user = new User(name, email, age);
            userDao.create(user);
            System.out.println("Created: " + user);
        } catch (Exception e) {
            logger.error("Error creating user", e);
            System.out.println("Failed to create user: " + e.getMessage());
        }
    }

    private void handleList() {
        try {
            List<User> users = userDao.findAll();
            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                users.forEach(System.out::println);
            }
        } catch (Exception e) {
            logger.error("Error listing users", e);
            System.out.println("Failed to list users: " + e.getMessage());
        }
    }

    private void handleGet() {
        System.out.print("Id: ");
        Long id = parseLong(scanner.nextLine().trim());
        Optional<User> userOpt = userDao.findById(id);
        userOpt.ifPresentOrElse(
                user -> System.out.println(user),
                () -> System.out.println("User not found with id " + id)
        );
    }

    private void handleUpdate() {
        try {
            System.out.print("Id: ");
            Long id = parseLong(scanner.nextLine().trim());
            Optional<User> userOpt = userDao.findById(id);
            if (userOpt.isEmpty()) {
                System.out.println("User not found with id " + id);
                return;
            }
            User user = userOpt.get();
            System.out.println("Current: " + user);
            System.out.print("New name (empty to skip): ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) user.setName(name);
            System.out.print("New email (empty to skip): ");
            String email = scanner.nextLine().trim();
            if (!email.isEmpty()) user.setEmail(email);
            System.out.print("New age (empty to skip): ");
            String ageStr = scanner.nextLine().trim();
            if (!ageStr.isEmpty()) user.setAge(parseInteger(ageStr));

            userDao.update(user);
            System.out.println("Updated: " + user);
        } catch (Exception e) {
            logger.error("Error updating user", e);
            System.out.println("Failed to update user: " + e.getMessage());
        }
    }

    private void handleDelete() {
        try {
            System.out.print("Id: ");
            Long id = parseLong(scanner.nextLine().trim());
            boolean deleted = userDao.delete(id);
            if (deleted) System.out.println("Deleted user with id " + id);
            else System.out.println("User not found with id " + id);
        } catch (Exception e) {
            logger.error("Error deleting user", e);
            System.out.println("Failed to delete user: " + e.getMessage());
        }
    }

    private Integer parseInteger(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException ex) {
            return -1L;
        }
    }
}

