package com.example.userservice.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HibernateUtilTest {

    @Test
    void testSessionFactoryBuildsConfigurationWithoutRealDB() {
        assertDoesNotThrow(() -> {
            Configuration cfg = new Configuration();

            // создаём конфигурацию вручную (не используем hibernate.cfg.xml)
            cfg.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
            cfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
            cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
            cfg.setProperty("hibernate.show_sql", "false");

            // добавляем наши аннотированные сущности
            cfg.addAnnotatedClass(com.example.userservice.model.User.class);

            SessionFactory factory = cfg.buildSessionFactory();
            assertNotNull(factory);
            factory.close();
        });
    }
}
