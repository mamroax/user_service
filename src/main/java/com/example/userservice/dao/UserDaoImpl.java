package com.example.userservice.dao;

import com.example.userservice.model.User;
import com.example.userservice.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.HibernateException;


import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);
    private final SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User create(User user) throws Exception {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.save(user);
            tx.commit();
            logger.info("Created user: {}", user);
            return user;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to create user", e);
            throw e;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (HibernateException e) {
            logger.error("Failed to find user by id {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User", User.class).list();
        } catch (HibernateException e) {
            logger.error("Failed to list users", e);
            throw e;
        }
    }

    @Override
    public User update(User user) throws Exception {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.update(user);
            tx.commit();
            logger.info("Updated user: {}", user);
            return user;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to update user", e);
            throw e;
        }
    }

    @Override
    public boolean delete(Long id) throws Exception {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                return false;
            }
            tx = session.beginTransaction();
            session.delete(user);
            tx.commit();
            logger.info("Deleted user with id {}", id);
            return true;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to delete user {}", id, e);
            throw e;
        }
    }
}

