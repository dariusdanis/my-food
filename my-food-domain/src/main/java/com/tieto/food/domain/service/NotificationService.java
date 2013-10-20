package com.tieto.food.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.dao.NotificationDao;
import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.Notification;
import com.tieto.food.domain.entity.User;

@Service
public class NotificationService {

    @Autowired
    private NotificationDao notificationDao;

    @Transactional
    public List<Event> getEventsByUser(User user) {
        return notificationDao.getEventsByUser(user);
    }

    @Transactional
    public Notification merge(Notification notification) {
        return notificationDao.merge(notification);
    }

    @Transactional
    public void remove(Notification notification) {
        notificationDao.remove(notification);
    }

    @Transactional
    public Notification findById(Long id) {
        return notificationDao.findById(id);
    }
}