package com.tieto.food.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class TextSubscription {

    @Id
    @GeneratedValue
    private Long subscriptionId;

    @ManyToOne
    @JoinColumn(name = "subscriberFk")
    private User subscriber;

    @Column(nullable = false)
    private String textLine;

    public TextSubscription() {
    }

    public TextSubscription(User subscriber, String textLine) {
        this.subscriber = subscriber;
        this.textLine = textLine;
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

    public String getTextLine() {
        return textLine;
    }

    public void setTextLine(String textLine) {
        this.textLine = textLine;
    }
}