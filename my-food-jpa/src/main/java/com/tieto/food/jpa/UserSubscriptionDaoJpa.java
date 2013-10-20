package com.tieto.food.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.tieto.food.domain.dao.UserSubscriptionDao;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.entity.UserSubscription;

@Repository
public class UserSubscriptionDaoJpa implements UserSubscriptionDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void merge(UserSubscription userSubscription) {
        em.merge(userSubscription);
    }

    @Override
    public void merge(List<User> userList, User subscriber) {
        for (User subscribedUser : userList) {
            em.merge(new UserSubscription(subscriber, subscribedUser));
        }
    }

    @Override
    public void remove(List<User> userList, User subscriber) {
        Query query = em.createQuery("DELETE FROM UserSubscription us "
                + "WHERE us.subscriber.userId = :subscriberId "
                + "AND us.subscribedUser.userId" + " = :subscribedUserId");
        for (User subscribedUser : userList) {
            query.setParameter("subscribedUserId", subscribedUser.getUserId());
            query.setParameter("subscriberId", subscriber.getUserId());
            query.executeUpdate();
        }
    }

    @Override
    public void removeAllByUser(User user) {
        Query query = em.createQuery("DELETE FROM UserSubscription us "
                + "WHERE us.subscriber.userId = :userId");
        query.setParameter("userId", user.getUserId());
        query.executeUpdate();
    }

    @Override
    public List<User> getSubscribedUsersByUser(User user) {
        TypedQuery<User> listOfUsers = em.createQuery(
                "SELECT us.subscribedUser " + "FROM UserSubscription us "
                        + "WHERE us.subscriber.userId = :userId", User.class);
        listOfUsers.setParameter("userId", user.getId());
        return listOfUsers.getResultList();
    }

    @Override
    public List<User> getUsersThatSubscribeMe(User user) {
        TypedQuery<User> listOfUsers = em.createQuery("SELECT us.subscriber "
                + "FROM UserSubscription us "
                + "WHERE us.subscribedUser.userId = :userId", User.class);
        listOfUsers.setParameter("userId", user.getId());
        return listOfUsers.getResultList();
    }
}