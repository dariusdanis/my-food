package com.tieto.food.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.dao.TextSubscriptionDao;
import com.tieto.food.domain.entity.TextSubscription;
import com.tieto.food.domain.entity.User;

@Service
public class TextSubscriptionService {

    @Autowired
    private TextSubscriptionDao textSubscriptionDao;

    @Transactional
    public void merge(TextSubscription textSubscription) {
        textSubscriptionDao.merge(textSubscription);
    }

    @Transactional
    public void merge(List<String> textList, User user) {
        textSubscriptionDao.merge(textList, user);
    }

    @Transactional
    public void remove(List<String> textList, User user) {
        textSubscriptionDao.remove(textList, user);
    }

    @Transactional
    public void removeAllByUser(User user) {
        textSubscriptionDao.removeAllByUser(user);
    }

    @Transactional
    public List<String> getTextsByUser(User user) {
        return textSubscriptionDao.getTextsByUser(user);
    }

    @Transactional
    public List<User> getUsersByText(List<String> stringList) {
        return textSubscriptionDao.getUsersByText(stringList);
    }
}