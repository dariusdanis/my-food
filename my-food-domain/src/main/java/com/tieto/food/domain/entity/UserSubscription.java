package com.tieto.food.domain.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class UserSubscription {

    @Id
    @GeneratedValue
    private Long subscriptionId;

    @ManyToOne
    @JoinColumn(name = "subscriberFk")
    private User subscriber;

    @ManyToOne
    @JoinColumn(name = "subscribedUserFk")
    private User subscribedUser;

    public UserSubscription() {
    }

    public UserSubscription(User subscriber, User subscribedUser) {
        this.subscriber = subscriber;
        this.subscribedUser = subscribedUser;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public User getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(User subscriber) {
        this.subscriber = subscriber;
    }

    public User getSubscribedUser() {
        return subscribedUser;
    }

    public void setSubscribedUser(User subscribedUser) {
        this.subscribedUser = subscribedUser;
    }
}