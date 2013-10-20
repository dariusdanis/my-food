package com.tieto.food.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.dao.TypeSubscriptionDao;
import com.tieto.food.domain.entity.Type;
import com.tieto.food.domain.entity.TypeSubscription;
import com.tieto.food.domain.entity.User;

@Service
public class TypeSubscriptionService {

    @Autowired
    private TypeSubscriptionDao typeSubscriptionDao;

    @Transactional
    public TypeSubscription merge(TypeSubscription typeSubscription) {
        return typeSubscriptionDao.merge(typeSubscription);
    }

    @Transactional
    public List<Type> getTypesByUser(User user) {
        return typeSubscriptionDao.getTypesByUser(user);
    }

    @Transactional
    public List<String> getTypeNameByUser(User user) {
        return typeSubscriptionDao.getTypesNameByUser(user);
    }

    @Transactional
    public void removeAllByUser(User user) {
        typeSubscriptionDao.removeAllByUser(user);
    }

    @Transactional
    public void remove(List<Type> list, User user) {
        typeSubscriptionDao.remove(list, user);
    }

    @Transactional
    public void merge(List<Type> list, User user) {
        typeSubscriptionDao.merge(list, user);
    }
}