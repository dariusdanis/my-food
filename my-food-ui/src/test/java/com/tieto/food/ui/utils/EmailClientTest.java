package com.tieto.food.ui.utils;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.EventService;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.BaseWebTest;

@Transactional
public class EmailClientTest extends BaseWebTest {
    private User mockUser;
    private Event mockEvent;

    @Autowired
    private UserService userService;
    
    @Autowired
    private EventService eventService;
        
    @Before
    public void initMockData() {
        mockUser = userService.loadById(0L);
        mockEvent = eventService.loadById(0L);
    }
    
    @Test
    public void testSendNotificationAboutUserEventEmail() {
        Thread thread = EmailClient.getInstanceOfEmailClient()
                .sendNotificationAboutUserEventEmail(mockEvent, 
                        mockUser, 0);
        assertTrue(thread.isAlive());
    }
    
    @Test
    public void testSendUpcomingEventNotificationEmail() {
        Thread thread = EmailClient.getInstanceOfEmailClient()
                .sendUpcomingEventNotificationEmail(
                        Arrays.asList(mockUser), mockEvent);
        assertTrue(thread.isAlive());
    }
    
    @Test
    public void testSendRemoveParticipantEmail() {
        Thread thread = EmailClient.getInstanceOfEmailClient()
                .sendRemoveParticipantEmail(mockUser, mockEvent);
        assertTrue(thread.isAlive());
    }
    
    @Test
    public void testSendEventBlockedEmail() {
        Thread thread = EmailClient.getInstanceOfEmailClient()
                .sendEventBlockedEmail(mockUser, mockEvent);
        assertTrue(thread.isAlive());
    }
    
    @Test
    public void testSendEventAllowedEmail() {
        Thread thread = EmailClient.getInstanceOfEmailClient()
                .sendEventAllowedEmail(mockUser, mockEvent);
        assertTrue(thread.isAlive());
    }
    
    @Test
    public void testSendInvitationEmailOne() {
        Thread thread = EmailClient.getInstanceOfEmailClient()
                .sendInvitationEmail(mockUser, mockEvent);
        assertTrue(thread.isAlive());
    }
    
    @Test
    public void testSendInvitationEmailTwo() {
        Thread thread = EmailClient.getInstanceOfEmailClient()
                .sendInvitationEmail("test@test.com", mockEvent);
        assertTrue(thread.isAlive());
    }
}