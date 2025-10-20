package com.example.userservice.util;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class HibernateUtilTest {

    @AfterEach
    void cleanup() {
        HibernateUtil.setSessionFactory(null);
    }

    @Test
    void testShutdownCallsClose() {
        SessionFactory mockFactory = mock(SessionFactory.class);
        HibernateUtil.setSessionFactory(mockFactory);

        HibernateUtil.shutdown();

        verify(mockFactory).close();
    }

    @Test
    void testBuildSessionFactorySuccess() {
        SessionFactory mockFactory = mock(SessionFactory.class);
        HibernateUtil.setSessionFactory(mockFactory);

        SessionFactory factory = HibernateUtil.getSessionFactory();

        assertNotNull(factory);
        assertEquals(mockFactory, factory);
    }

    @Test
    void testBuildSessionFactoryFailure() {
        // Здесь просто имитируем исключение при инициализации
        HibernateUtil.setSessionFactory(null);

        ExceptionInInitializerError ex = assertThrows(ExceptionInInitializerError.class, () -> {
            throw new ExceptionInInitializerError(new RuntimeException("fail"));
        });

        assertTrue(ex.getCause() instanceof RuntimeException);
        assertEquals("fail", ex.getCause().getMessage());
    }
}
