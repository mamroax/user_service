package com.example.userservice.service;

import com.example.userservice.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserConsoleApp {

    private static final Logger logger = LogManager.getLogger(UserConsoleApp.class);
    private final UserService userService;
    private final Scanner scanner = new Scanner(System.in);

    public UserConsoleApp(UserService userService) {
        this.userService = userService;
    }

    public void start() {
        printHelp();
        while (true) {
            System.out.print("\n> ");
            String cmd = scanner.nextLine().trim();
            if (cmd.isEmpty()) continue;

            switch (cmd.toLowerCase()) {
                case "create" -> handleCreate();
                case "list" -> handleList();
                case "get" -> handleGet();
                case "update" -> handleUpdate();
                case "delete" -> handleDelete();
                case "help" -> printHelp();
                case "exit" -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Unknown command. Type 'help' for commands.");
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

            User user = userService.createUser(name, email, age);
            System.out.println("Created: " + user);
        } catch (Exception e) {
            logger.error("Error creating user", e);
            System.out.println("Failed: " + e.getMessage());
        }
    }

    private void handleList() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) System.out.println("No users found.");
        else users.forEach(System.out::println);
    }

    private void handleGet() {
        System.out.print("Id: ");
        Long id = parseLong(scanner.nextLine().trim());
        Optional<User> user = userService.getUserById(id);
        user.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("User not found with id " + id)
        );
    }

    private void handleUpdate() {
        try {
            System.out.print("Id: ");
            Long id = parseLong(scanner.nextLine().trim());
            System.out.print("New name (empty to skip): ");
            String name = scanner.nextLine().trim();
            System.out.print("New email (empty to skip): ");
            String email = scanner.nextLine().trim();
            System.out.print("New age (empty to skip): ");
            String ageStr = scanner.nextLine().trim();
            Integer age = ageStr.isEmpty() ? null : parseInteger(ageStr);

            User updated = userService.updateUser(id, name, email, age);
            System.out.println("Updated: " + updated);
        } catch (Exception e) {
            logger.error("Error updating user", e);
            System.out.println("Failed: " + e.getMessage());
        }
    }

    private void handleDelete() {
        try {
            System.out.print("Id: ");
            Long id = parseLong(scanner.nextLine().trim());
            boolean deleted = userService.deleteUser(id);
            if (deleted) System.out.println("Deleted user with id " + id);
            else System.out.println("User not found");
        } catch (Exception e) {
            logger.error("Error deleting user", e);
            System.out.println("Failed: " + e.getMessage());
        }
    }

    private Integer parseInteger(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1L;
        }
    }
}
