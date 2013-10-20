package com.tieto.food.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.tieto.food.domain.dao.NotificationDao;
import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.Notification;
import com.tieto.food.domain.entity.User;

@Repository
public class NotificationDaoJpa implements NotificationDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Event> getEventsByUser(User user) {
        TypedQuery<Event> query = em.createQuery(
                "SELECT n.event FROM Notification n "
                        + "WHERE n.user.userId = :userId", Event.class);
        query.setParameter("userId", user.getUserId());
        return query.getResultList();
    }

    @Override
    public Notification merge(Notification notification) {
        return em.merge(notification);
    }

    @Override
    public void remove(Notification notification) {
        em.remove(notification);
    }

    @Override
    public Notification findById(Long id) {
        try {
            return em.find(Notification.class, id);
        } catch (Exception e) {
            return null;
        }
    }
}