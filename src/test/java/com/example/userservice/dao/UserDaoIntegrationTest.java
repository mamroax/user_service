package com.example.userservice.dao;

import com.example.userservice.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoIntegrationTest {

    private static SessionFactory sessionFactory;
    private static UserDaoImpl userDao;

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15.3")
                    .withDatabaseName("itestdb")
                    .withUsername("test")
                    .withPassword("test");

    @BeforeAll
    static void beforeAll() {
        POSTGRES.start();

        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        cfg.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        cfg.setProperty("hibernate.connection.url", POSTGRES.getJdbcUrl());
        cfg.setProperty("hibernate.connection.username", POSTGRES.getUsername());
        cfg.setProperty("hibernate.connection.password", POSTGRES.getPassword());
        cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        cfg.setProperty("hibernate.show_sql", "false");
        cfg.addAnnotatedClass(User.class);

        sessionFactory = cfg.buildSessionFactory();
        userDao = new UserDaoImpl(sessionFactory);
    }

    @AfterAll
    static void afterAll() {
        if (sessionFactory != null) sessionFactory.close();
        POSTGRES.stop();
    }

    @BeforeEach
    void beforeEach() {
        try (Session s = sessionFactory.openSession()) {
            var tx = s.beginTransaction();
            s.createQuery("delete from User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    @Order(1)
    void createAndFindById() throws Exception {
        User u = new User("Alice", "alice@test", 21);
        userDao.create(u);
        assertNotNull(u.getId());

        Optional<User> opt = userDao.findById(u.getId());
        assertTrue(opt.isPresent());
        assertEquals("Alice", opt.get().getName());
    }

    @Test
    @Order(2)
    void findAll_and_update() throws Exception {
        User u1 = new User("Bob", "bob@test", 30);
        User u2 = new User("Carol", "carol@test", 28);
        userDao.create(u1);
        userDao.create(u2);

        List<User> list = userDao.findAll();
        assertEquals(2, list.size());

        u1.setAge(31);
        userDao.update(u1);

        User reloaded = userDao.findById(u1.getId()).orElseThrow();
        assertEquals(31, reloaded.getAge());
    }

    @Test
    @Order(3)
    void delete_user() throws Exception {
        User u = new User("Dave", "dave@test", 40);
        userDao.create(u);

        boolean deleted = userDao.delete(u.getId());
        assertTrue(deleted);

        assertTrue(userDao.findById(u.getId()).isEmpty());
        assertTrue(userDao.findAll().isEmpty());
    }
}
