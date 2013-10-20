package com.tieto.food.domain.dao;

import java.util.List;

import com.tieto.food.domain.entity.Type;
import com.tieto.food.domain.entity.TypeSubscription;
import com.tieto.food.domain.entity.User;

public interface TypeSubscriptionDao {
    TypeSubscription merge(TypeSubscription typeSubscription);

    List<Type> getTypesByUser(User user);

    List<String> getTypesNameByUser(User user);

    void removeAllByUser(User user);

    void remove(List<Type> list, User user);

    void merge(List<Type> list, User user);
}