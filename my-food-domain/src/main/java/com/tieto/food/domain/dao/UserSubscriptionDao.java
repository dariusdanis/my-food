package com.tieto.food.domain.dao;

import java.util.List;

import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.entity.UserSubscription;

public interface UserSubscriptionDao {
    void merge(UserSubscription userSubscription);

    void merge(List<User> userList, User subscriber);

    void remove(List<User> userList, User subscriber);

    void removeAllByUser(User user);

    List<User> getSubscribedUsersByUser(User user);

    List<User> getUsersThatSubscribeMe(User user);
}