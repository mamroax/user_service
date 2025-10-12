package com.example.userservice;


import com.example.userservice.service.UserConsoleService;
import com.example.userservice.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting user-service...");
        UserConsoleService consoleService = new UserConsoleService();

        try {
            consoleService.start();
        } finally {
            logger.info("Shutting down Hibernate SessionFactory...");
            HibernateUtil.shutdown();
        }
    }
}
