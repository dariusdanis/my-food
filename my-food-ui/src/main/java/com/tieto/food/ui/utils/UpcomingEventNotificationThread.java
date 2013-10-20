package com.tieto.food.ui.utils;

import java.util.Date;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.service.EventService;

public class UpcomingEventNotificationThread {

    private static final int NOTIFY_BEFORE_HOURS = 3;

    @SpringBean
    private EventService eventService;

    private EmailClient email;

    public UpcomingEventNotificationThread(EmailClient email) {
        Injector.get().inject(this);
        this.email = email;
    }

    public void startThread() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    notifyUsers();
                    try {
                        Thread.sleep(300000);
                    } catch (InterruptedException e) {
                        // Do nothing
                    }
                }
            }

        }.start();
    }

    public void notifyUsers() {
        for (Event event : eventService.listValidFutureEvents()) {
            Date currentDate = new Date();
            long diff = event.getEventDate().getTime() - currentDate.getTime();
            diff /= 1000;
            diff /= 60;
            diff /= 60;
            if (diff < NOTIFY_BEFORE_HOURS
                    && !event.isEventUpcomingNotificationSent()) {
                email.sendUpcomingEventNotificationEmail(event.getUsers(),
                        event);
                event.setEventUpcomingNotificationSent(true);
                eventService.merge(event);
            }
        }
    }

}
