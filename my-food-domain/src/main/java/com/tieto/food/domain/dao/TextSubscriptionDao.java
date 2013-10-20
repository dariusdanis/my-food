package com.tieto.food.domain.dao;

import java.util.List;

import com.tieto.food.domain.entity.TextSubscription;
import com.tieto.food.domain.entity.User;

public interface TextSubscriptionDao {
    void merge(TextSubscription textSubscription);

    void merge(List<String> textList, User user);

    void remove(List<String> textList, User user);

    void removeAllByUser(User user);

    List<String> getTextsByUser(User user);

    List<User> getUsersByText(List<String> stringList);
}