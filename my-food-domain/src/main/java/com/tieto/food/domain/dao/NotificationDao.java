package com.tieto.food.domain.dao;

import java.util.List;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.Notification;
import com.tieto.food.domain.entity.User;

public interface NotificationDao {
    List<Event> getEventsByUser(User user);

    Notification merge(Notification notification);

    void remove(Notification notification);

    Notification findById(Long id);
}
