package com.tieto.food.domain.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class TypeSubscription {

    @Id
    @GeneratedValue
    private Long subscriptionId;

    @ManyToOne
    @JoinColumn(name = "userFk")
    private User user;

    @ManyToOne
    @JoinColumn(name = "typeFk")
    private Type type;

    public TypeSubscription() {
    }

    public TypeSubscription(User user, Type type) {
        this.user = user;
        this.type = type;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
