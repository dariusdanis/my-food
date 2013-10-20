package com.tieto.food.domain.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "Users")
public class User implements Comparable<Object> {
    @Id
    @GeneratedValue
    private Long userId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "userJoinEvent", joinColumns = { @JoinColumn(name = "userId") }, inverseJoinColumns = { @JoinColumn(name = "eventId") })
    private List<Event> events;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "userReportEvent", joinColumns = { @JoinColumn(name = "userId") }, inverseJoinColumns = { @JoinColumn(name = "eventId") })
    private List<Event> reportedEvents;

    @Temporal(TemporalType.TIMESTAMP)
    private Date joinDate;
    private String email;
    private String name;
    private String surname;
    private String password;
    private boolean admin;
    private boolean sendJoinLeaveNotifications;
    private boolean sendUpcomingEventNotifications;
    private boolean orExist;

    @OneToMany(targetEntity = Notification.class, mappedBy = "user", fetch = FetchType.LAZY)
    private List<Notification> notification;

    @OneToMany(targetEntity = TypeSubscription.class, mappedBy = "user", fetch = FetchType.LAZY)
    private List<TypeSubscription> typeSubscription;

    @OneToMany(targetEntity = UserSubscription.class, mappedBy = "subscribedUser", fetch = FetchType.LAZY)
    private List<UserSubscription> userSubscription;

    @OneToMany(targetEntity = UserSubscription.class, mappedBy = "subscriber", fetch = FetchType.LAZY)
    private List<UserSubscription> subscribers;

    @OneToMany(targetEntity = TextSubscription.class, mappedBy = "subscriber", fetch = FetchType.LAZY)
    private List<TextSubscription> textSubscriptions;

    @OneToMany(targetEntity = Comment.class, mappedBy = "user", fetch = FetchType.LAZY)
    private List<Comment> comments;

    public User() {
    }

    public User(String email, String name, String surname) {
        admin = false;
        this.email = email;
        this.name = name;
        this.surname = surname;
    }

    public User(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return userId;
    }

    public void setId(Long id) {
        this.userId = id;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public boolean isOrExist() {
        return orExist;
    }

    public void setOrExist(boolean orExist) {
        this.orExist = orExist;
    }

    public List<Notification> getNotification() {
        return notification;
    }

    public void setNotification(List<Notification> notification) {
        this.notification = notification;
    }

    public List<TypeSubscription> getTypeSubscription() {
        return typeSubscription;
    }

    public void setTypeSubscription(List<TypeSubscription> typeSubscription) {
        this.typeSubscription = typeSubscription;
    }

    public boolean isSendJoinLeaveNotifications() {
        return sendJoinLeaveNotifications;
    }

    public void setSendJoinLeaveNotifications(boolean sendJoinLeaveNotifications) {
        this.sendJoinLeaveNotifications = sendJoinLeaveNotifications;
    }

    public List<UserSubscription> getUserSubscription() {
        return userSubscription;
    }

    public void setUserSubscription(List<UserSubscription> userSubscription) {
        this.userSubscription = userSubscription;
    }

    public List<UserSubscription> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<UserSubscription> subscribers) {
        this.subscribers = subscribers;
    }

    public List<TextSubscription> getTextSubscriptions() {
        return textSubscriptions;
    }

    public void setTextSubscriptions(List<TextSubscription> textSubscriptions) {
        this.textSubscriptions = textSubscriptions;
    }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }

    @Override
    public int compareTo(Object o) {
        User u = (User) o;
        if (u.getId() != this.getId()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return userId == ((User) obj).getId();
        }
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 2;
    }

    public boolean isSendUpcomingEventNotifications() {
        return sendUpcomingEventNotifications;
    }

    public void setSendUpcomingEventNotifications(
            boolean sendUpcomingEventNotifications) {
        this.sendUpcomingEventNotifications = sendUpcomingEventNotifications;
    }

    public List<Event> getReportedEvents() {
        return reportedEvents;
    }

    public void setReportedEvents(List<Event> reportedEvents) {
        this.reportedEvents = reportedEvents;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}