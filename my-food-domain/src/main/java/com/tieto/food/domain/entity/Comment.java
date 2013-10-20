package com.tieto.food.domain.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Comment {

    @Id
    @GeneratedValue
    private Long commentId;

    @Column(nullable = false)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "userFk")
    private User user;

    @ManyToOne
    @JoinColumn(name = "eventFk")
    private Event event;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date commentDate;

    public Comment() {
    }

    public Comment(String comment, User user, Event event, Date commentDate) {
        this.comment = comment;
        this.user = user;
        this.event = event;
        this.commentDate = commentDate;
    }

    public Comment(String comment, User user, Event event) {
        this.comment = comment;
        this.user = user;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

}
