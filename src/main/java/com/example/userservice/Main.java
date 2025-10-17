package com.example.userservice;

import com.example.userservice.dao.UserDaoImpl;
import com.example.userservice.service.UserConsoleApp;
import com.example.userservice.service.UserServiceImpl;
import com.example.userservice.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting UserService application...");

        try {
            var sessionFactory = HibernateUtil.getSessionFactory();
            var userDao = new UserDaoImpl(sessionFactory);
            var userService = new UserServiceImpl(userDao);
            var console = new UserConsoleApp(userService);

            logger.info("Initialization completed. Launching console interface...");
            console.start();

        } catch (Exception e) {
            logger.error("Fatal error during application startup", e);
            System.err.println("Application failed to start: " + e.getMessage());
        } finally {
            HibernateUtil.shutdown();
            logger.info("UserService application stopped.");
        }
    }
}
