package com.tieto.food.ui.event;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.Place;
import com.tieto.food.domain.entity.Type;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.EventService;
import com.tieto.food.domain.service.PlaceService;
import com.tieto.food.domain.service.TypeService;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.BaseWebTest;

@Transactional
public class EventMapPageTest extends BaseWebTest {
   
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
        mockEvent = eventService.merge(mockEvent);
    }
    
    @Test
    public void testIfMapPageRenders() {        
        tester.startPage(EventMapPage.class);
    }
}