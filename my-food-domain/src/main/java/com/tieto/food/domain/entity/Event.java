package com.tieto.food.domain.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "Events")
public class Event {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long createdBy;

    @Column
    private String description;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;

    @ManyToOne
    @JoinColumn(name = "eventPlaceFk")
    private Place eventPlace;

    @ManyToOne
    @JoinColumn(name = "eventTypeFk")
    private Type eventType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "userJoinEvent", joinColumns = { @JoinColumn(name = "eventId") }, inverseJoinColumns = { @JoinColumn(name = "userId") })
    private List<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "userReportEvent", joinColumns = { @JoinColumn(name = "eventId") }, inverseJoinColumns = { @JoinColumn(name = "userId") })
    private List<User> usersWhoReported;

    @OneToMany(targetEntity = Notification.class, mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Notification> notification;

    @OneToMany(targetEntity = Comment.class, mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments;

    @Column
    private boolean eventUpcomingNotificationSent;

    @Column(nullable = false)
    private Long timesReportedAsSpam;

    @Column
    private boolean isSpam;

    public Event() {
    }

    public Event(Long eventId) {
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Place getEventPlace() {
        return eventPlace;
    }

    public void setEventPlace(Place eventPlace) {
        this.eventPlace = eventPlace;
    }

    public Type getEventType() {
        return eventType;
    }

    public void setEventType(Type eventType) {
        this.eventType = eventType;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Notification> getNotification() {
        return notification;
    }

    public void setNotification(List<Notification> notification) {
        this.notification = notification;
    }

    public boolean isEventUpcomingNotificationSent() {
        return eventUpcomingNotificationSent;
    }

    public void setEventUpcomingNotificationSent(
            boolean eventUpcomingNotificationSent) {
        this.eventUpcomingNotificationSent = eventUpcomingNotificationSent;
    }

    public Long getTimesReportedAsSpam() {
        return timesReportedAsSpam;
    }

    public void setTimesReportedAsSpam(Long timesReportedAsSpam) {
        this.timesReportedAsSpam = timesReportedAsSpam;
    }

    public boolean isSpam() {
        return isSpam;
    }

    public void setSpam(boolean isSpam) {
        this.isSpam = isSpam;
    }

    public List<User> getUsersWhoReported() {
        return usersWhoReported;
    }

    public void setUsersWhoReported(List<User> usersWhoReported) {
        this.usersWhoReported = usersWhoReported;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}