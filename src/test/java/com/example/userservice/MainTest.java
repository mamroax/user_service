package com.example.userservice;

import com.example.userservice.dao.UserDaoImpl;
import com.example.userservice.service.UserConsoleApp;
import com.example.userservice.service.UserServiceImpl;
import com.example.userservice.util.HibernateUtil;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

class MainTest {

    @Test
    @DisplayName("Успешный запуск приложения вызывает console.start()")
    void testMainSuccess() {
        SessionFactory sessionFactory = mock(SessionFactory.class);
        UserDaoImpl userDao = mock(UserDaoImpl.class);
        UserServiceImpl userService = mock(UserServiceImpl.class);
        UserConsoleApp console = mock(UserConsoleApp.class);

        try (MockedStatic<HibernateUtil> hibernateMock = mockStatic(HibernateUtil.class);
             MockedConstruction<UserDaoImpl> daoCtor = mockConstruction(UserDaoImpl.class,
                     (mock, context) -> {});
             MockedConstruction<UserServiceImpl> serviceCtor = mockConstruction(UserServiceImpl.class,
                     (mock, context) -> {});
             MockedConstruction<UserConsoleApp> consoleCtor = mockConstruction(UserConsoleApp.class,
                     (mock, context) -> {})
        ) {
            hibernateMock.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            hibernateMock.when(HibernateUtil::shutdown).thenAnswer(inv -> null);

            Main.main(new String[]{});

            // Проверяем, что console.start() вызван
            verify(consoleCtor.constructed().get(0), times(1)).start();

            // Проверяем, что shutdown вызван
            hibernateMock.verify(HibernateUtil::shutdown, times(1));
        }
    }

    @Test
    @DisplayName("Ошибка при инициализации корректно обрабатывается и вызывает shutdown()")
    void testMainFailure() {
        try (MockedStatic<HibernateUtil> hibernateMock = mockStatic(HibernateUtil.class)) {
            hibernateMock.when(HibernateUtil::getSessionFactory)
                    .thenThrow(new RuntimeException("DB init failed"));
            hibernateMock.when(HibernateUtil::shutdown).thenAnswer(inv -> null);

            Main.main(new String[]{});

            // Проверяем, что shutdown вызван
            hibernateMock.verify(HibernateUtil::shutdown, times(1));
        }
    }

    @Test
    @DisplayName("При любом завершении вызывается HibernateUtil.shutdown()")
    void testAlwaysShutdownCalled() {
        // Мокаем зависимость Hibernate
        SessionFactory sessionFactory = mock(SessionFactory.class);

        // Мокаем статические вызовы HibernateUtil и конструкторы сервисов
        try (MockedStatic<HibernateUtil> hibernateMock = mockStatic(HibernateUtil.class);
             MockedConstruction<UserDaoImpl> daoCtor = mockConstruction(UserDaoImpl.class);
             MockedConstruction<UserServiceImpl> serviceCtor = mockConstruction(UserServiceImpl.class);
             MockedConstruction<UserConsoleApp> consoleCtor = mockConstruction(UserConsoleApp.class,
                     (mock, context) -> doNothing().when(mock).start()) // предотвращаем зависание
        ) {
            // Настраиваем HibernateUtil
            hibernateMock.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            hibernateMock.when(HibernateUtil::shutdown).thenAnswer(inv -> null);

            // Запускаем main()
            Main.main(new String[]{});

            // Проверяем, что shutdown был вызван хотя бы раз
            hibernateMock.verify(HibernateUtil::shutdown, atLeastOnce());
        }
    }
}
