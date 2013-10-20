package com.tieto.food.ui.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.Notification;
import com.tieto.food.domain.entity.Type;
import com.tieto.food.domain.entity.TypeSubscription;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.NotificationService;
import com.tieto.food.domain.service.TextSubscriptionService;
import com.tieto.food.domain.service.TypeService;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.domain.service.UserSubscriptionService;

public class EventNotificationService {

    private EmailClient emailClient = EmailClient.getInstanceOfEmailClient();

    @SpringBean
    private UserService userService;

    @SpringBean
    private TypeService typeService;

    @SpringBean
    private UserSubscriptionService userSubscriptionService;

    @SpringBean
    private TextSubscriptionService textSubscriptionService;

    @SpringBean
    private NotificationService notificationService;

    public EventNotificationService() {
        Injector.get().inject(this);
    }

    public boolean hasNotifications(User user) {
        return userService.loadById(user.getId()).getNotification().size() != 0;
    }

    public List<Event> getSubscribedEvents(User user) {
        return notificationService.getEventsByUser(user);
    }

    @SuppressWarnings("unchecked")
    public void newEvent(Event event) {
        List<User> typeSubscriptions = getSubscribersToType(event
                .getEventType());
        List<User> userSubscriptions = getSubscribersToUser();
        List<User> textSubscriptions = getSubscribersToText(
                event.getDescription(), event.getTitle());
        List<User> usersList = ListUtils.sum(typeSubscriptions,
                userSubscriptions);
        usersList = ListUtils.sum(usersList, textSubscriptions);
        for (int i = 0; i < usersList.size(); i++) {
            if (usersList.get(i).getId() == MyFoodSession.get().getUser()
                    .getId()) {
                usersList.remove(i);
                return;
            }
        }
        sendNotificationsTo(usersList, event);

    }

    @SuppressWarnings("unchecked")
    private List<User> getSubscribersToText(String description, String title) {
        List<String> arrayOfDescription = new ArrayList<String>();
        if (description != null) {
            arrayOfDescription = Arrays.asList(description.split(" "));
        }
        List<String> arrayOfTitle = Arrays.asList(title.split(" "));
        return textSubscriptionService.getUsersByText(ListUtils.sum(
                arrayOfDescription, arrayOfTitle));
    }

    private List<User> getSubscribersToUser() {
        return userSubscriptionService.getUsersThatSubscribeMe(MyFoodSession
                .get().getUser());
    }

    public void eventUpdated(Event event) {
        newEvent(event);
    }

    private void sendNotificationsTo(List<User> users, Event event) {
        sendEmailNotifications(users, event);
        sendNotifications(users, event);
    }

    private void sendEmailNotifications(List<User> users, Event event) {
        emailClient.sendNewEventNotificationEmail(users, event);
    }

    private void sendNotifications(List<User> users, Event event) {
        for (User u : users) {
            notificationService.merge(new Notification(event, u));
        }
    }

    private List<User> getSubscribersToType(Type type) {
        Type t = typeService.findType(type.getTypeId());
        List<User> users = new ArrayList<User>();
        for (TypeSubscription subscriptions : t.getTypeSubscription()) {
            users.add(subscriptions.getUser());
        }
        return users;
    }

    public void removeNotification(User user, Event event) {
        User managedUser = userService.loadById(user.getUserId());
        for (Notification n : managedUser.getNotification()) {
            if (n.getEvent().getEventId() == event.getEventId()) {
                notificationService.remove(n);
                return;
            }

        }
    }

}