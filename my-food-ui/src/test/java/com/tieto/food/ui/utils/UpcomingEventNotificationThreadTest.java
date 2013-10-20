package com.tieto.food.ui.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.Place;
import com.tieto.food.domain.entity.Type;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.EventService;
import com.tieto.food.domain.service.PlaceService;
import com.tieto.food.domain.service.TypeService;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.BaseWebTest;

public class UpcomingEventNotificationThreadTest extends BaseWebTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private TypeService typeService;

    @Test
    public void EventNotificationsSent() {
        List<User> users = new ArrayList<User>();
        Event event = new Event();
        User user = new User();
        user.setEmail("email@email.com");
        user.setJoinDate(new Date());
        user.setName("Vardenis");
        user.setSurname("Pavardenis");
        user.setPassword("123");
        user = userService.merge(user);
        event.setCreatedBy(user.getUserId());
        users.add(user);
        event.setUsers(users);
        event.setDescription("descriptionas");
        long closeFutureDate = new Date().getTime() + 10000;
        event.setEventDate(new Date(closeFutureDate));
        Place place = new Place();
        place.setPlace("Cili kaimas");
        place.setLatitude(13.25d);
        place.setLongitude(24.9d);
        place.setAddress("test address");
        event.setTimesReportedAsSpam(0L);
        event.setTitle("title");
        Type type = new Type();
        type.setType("Take away");
        event.setEventType(typeService.merge(type));
        Place mockPlace = new Place("Cili");
        mockPlace.setLatitude(24.35d);
        mockPlace.setLongitude(14.58d);
        mockPlace.setAddress("test address");
        event.setEventPlace(placeService.merge(mockPlace));
        event = eventService.merge(event);
        assertFalse(eventService.loadById(event.getEventId())
                .isEventUpcomingNotificationSent());
        new UpcomingEventNotificationThread(
                EmailClient.getInstanceOfEmailClient()).notifyUsers();
        assertTrue(eventService.loadById(event.getEventId())
                .isEventUpcomingNotificationSent());
    }
}