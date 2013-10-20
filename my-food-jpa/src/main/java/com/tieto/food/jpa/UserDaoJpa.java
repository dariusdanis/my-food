package com.tieto.food.jpa;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.tieto.food.domain.dao.UserDao;
import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.User;

@Repository
public class UserDaoJpa implements UserDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public User loadById(Long userId) {
        return em.find(User.class, userId);
    }

    @Override
    public List<User> listByEventId(Long eventId) {
        Event e = em.find(Event.class, eventId);
        return e.getUsers();
    }

    @Override
    public List<User> listAll() {
        return em.createQuery("Select c FROM User c", User.class)
                .getResultList();
    }

    @Override
    public User merge(User user) {
        return em.merge(user);
    }

    @Override
    public User loadByEmail(String email) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        User user;
        try {
            user = query.getSingleResult();
        } catch (NoResultException e) {
            user = null;
        }
        return user;
    }

    @Override
    public void remove(User user) {
        user.setOrExist(false);
        user.setEmail(null);
        user.setAdmin(false);
        user.setJoinDate(null);
        user.setPassword(null);
        if (user.getNotification() != null) {
            user.getNotification().clear();
        }
        em.merge(user);
    }

    @Override
    public List<User> listUsersWhoJoinedBefore(Date until) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.joinDate <= :date", User.class);
        query.setParameter("date", until, TemporalType.DATE);
        return query.getResultList();
    }

    @Override
    public Long getPaticipantCount(Long eventId) {
        List<User> users = em.find(Event.class, eventId).getUsers();
        Long counter = 0L;
        for (User u : users) {
            if (u.isOrExist()) {
                counter++;
            }
        }
        return counter;
    }

    @Override
    public List<User> listOnlyExisting() {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.orExist = true", User.class);
        return query.getResultList();
    }

    @Override
    public List<String> listOnlyExistingToString() {
        TypedQuery<String> query = em.createQuery(
                "SELECT u.email FROM User u WHERE u.orExist = true",
                String.class);
        return query.getResultList();
    }
}