package com.tieto.food.domain.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Notification {

    @Id
    @GeneratedValue
    private Long notificationId;

    @JoinColumn(name = "eventFk")
    @ManyToOne(fetch = FetchType.EAGER)
    private Event event;

    @JoinColumn(name = "userFk")
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    public Notification() {
    }

    public Notification(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Notification(Event event, User user) {
        this.event = event;
        this.user = user;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
