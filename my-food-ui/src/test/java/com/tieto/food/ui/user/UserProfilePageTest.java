package com.tieto.food.ui.user;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.Notification;
import com.tieto.food.domain.entity.Place;
import com.tieto.food.domain.entity.Type;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.EventService;
import com.tieto.food.domain.service.PlaceService;
import com.tieto.food.domain.service.TypeService;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.BaseWebTest;
import com.tieto.food.ui.utils.MyFoodSession;

@Transactional
public class UserProfilePageTest extends BaseWebTest{

    @Autowired
    private EventService eventService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PlaceService placeService;
    
    @Autowired
    private TypeService typeService;
    
    @Before
    public void initMockEventPlaceAndType() {
        User tempUser = new User("tomas@rupsys.com", "tomas", "rupsys");
        tempUser.setNotification(new ArrayList<Notification>()); 
        tempUser.setAdmin(true);
        MyFoodSession.get().setUser(userService
                .merge(tempUser));
        Event mockEvent = new Event();
        mockEvent.setTitle("!!!");
        mockEvent.setCreatedBy(userService.merge(
                new User("tprupsys@gmail.com",
                "Tomas", "Rupsys")).getId());
        mockEvent.setDescription("Geras description");
        mockEvent.setEventDate(new Date());
        mockEvent.setTimesReportedAsSpam(0L);
        mockEvent.setEventType(typeService.merge(new Type("Picnic")));
        Place mockPlace = new Place("Cili");
        mockPlace.setLatitude(24.35d);
        mockPlace.setLongitude(14.58d);
        mockPlace.setAddress("test address");
        mockEvent.setEventPlace(placeService.merge(mockPlace));
        mockEvent.setSpam(true);
        mockEvent.setTimesReportedAsSpam(8L);
        Event oneMoreEvent = mockEvent;
        mockEvent = eventService.merge(mockEvent);
        oneMoreEvent.setSpam(true);
        oneMoreEvent.setTimesReportedAsSpam(5L);
        eventService.merge(oneMoreEvent);
    }
    
    @Test
    public void rendersSuccessfully() {
        tester.startPage(UserProfilePage.class, UserProfilePage
                .parametersWith(userService.merge(
                        new User("tprupsys@gmail.com", "Tomas", "Rupsys"))
                        .getId()));
    }
}