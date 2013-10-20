package com.tieto.food.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.dao.UserSubscriptionDao;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.entity.UserSubscription;

@Service
public class UserSubscriptionService {

    @Autowired
    private UserSubscriptionDao userSubscriptionDao;

    @Transactional
    public void merge(UserSubscription userSubscription) {
        userSubscriptionDao.merge(userSubscription);
    }

    @Transactional
    public void merge(List<User> userList, User subscriber) {
        userSubscriptionDao.merge(userList, subscriber);
    }

    @Transactional
    public void remove(List<User> userList, User subscriber) {
        userSubscriptionDao.remove(userList, subscriber);
    }

    @Transactional
    public void removeAllByUser(User user) {
        userSubscriptionDao.removeAllByUser(user);
    }

    @Transactional
    public List<User> getSubscribedUsersByUser(User user) {
        return userSubscriptionDao.getSubscribedUsersByUser(user);
    }

    @Transactional
    public List<User> getUsersThatSubscribeMe(User user) {
        return userSubscriptionDao.getUsersThatSubscribeMe(user);
    }
}