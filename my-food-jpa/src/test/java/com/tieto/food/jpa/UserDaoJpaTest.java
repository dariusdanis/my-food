package com.tieto.food.jpa;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.Place;
import com.tieto.food.domain.entity.Type;
import com.tieto.food.domain.entity.User;
import com.tieto.food.jpa.EventDaoJpa;
import com.tieto.food.jpa.UserDaoJpa;

@Transactional
public class UserDaoJpaTest extends DaoJpaTestBase {

    @Autowired
    private EventDaoJpa eventDaoJpa;

    @Autowired
    private PlaceDaoJpa placeDaoJpa;

    @Autowired
    private TypeDaoJpa typeDaoJpa;

    @Autowired
    private UserDaoJpa userDaoJpa;

    @Test
    public void testRemove() {
        User user = new User();
        user.setEmail("email@email.com");
        user.setJoinDate(new Date());
        user.setName("Vardenis");
        user.setSurname("Pavardenis");
        user.setPassword("123");
        user = userDaoJpa.merge(user);
        userDaoJpa.remove(user);       
        assertFalse(userDaoJpa.loadById(user.getUserId()).isOrExist());
    }
    
    @Test
    public void loadsSavedUser() {
        User user = new User();
        user.setEmail("email@email.com");
        user.setJoinDate(new Date());
        user.setName("Vardenis");
        user.setSurname("Pavardenis");
        user.setPassword("123");
        User merged = userDaoJpa.merge(user);
        Long userId = merged.getUserId();
        User loaded = userDaoJpa.loadById(userId);
        int count = userDaoJpa.listAll().size();
        assertNotNull(loaded);
        assertEquals(userDaoJpa.listAll().size(), count);
        assertEquals(loaded.getEmail(), user.getEmail());
        assertEquals(loaded.getJoinDate(), user.getJoinDate());
        assertEquals(loaded.getName(), user.getName());
        assertEquals(loaded.getPassword(), user.getPassword());
        assertEquals(loaded.getSurname(), user.getSurname());
    }

    @Test
    public void loadsByEmail() {
        User user = new User();
        user.setEmail("email@email.com");
        user.setJoinDate(new Date());
        user.setName("Vardenis");
        user.setSurname("Pavardenis");
        user.setPassword("123");
        userDaoJpa.merge(user);
        User loaded = userDaoJpa.loadByEmail(user.getEmail());
        assertNotNull(loaded);
        assertEquals(loaded.getEmail(), user.getEmail());
    }

    @Test
    public void testAddingRemovingAndCounting() {
        // Initial data
        List<User> users = new ArrayList<User>();
        Event event = new Event();
        User user = new User();
        user.setEmail("email@email.com");
        user.setJoinDate(new Date());
        user.setName("Vardenis");
        user.setSurname("Pavardenis");
        user.setPassword("123");
        user = userDaoJpa.merge(user);
        event.setCreatedBy(user.getUserId());
        users.add(user);
        event.setUsers(users);
        event.setDescription("descriptionas");
        event.setEventDate(new Date(new GregorianCalendar(2100, 11, 20)
                .getTimeInMillis()));
        Place place = new Place();
        place.setPlace("Cili kaimas");
        place.setLatitude(13.25d);
        place.setLongitude(24.9d);
        place.setAddress("test address");
        event.setEventPlace(placeDaoJpa.merge(place));
        event.setTitle("title");
        Type type = new Type();
        type.setType("Take away");
        event.setEventType(typeDaoJpa.merge(type));
        event.setTimesReportedAsSpam(0L);
        event = eventDaoJpa.merge(event);
        
        // Test listing
        boolean userContainsEvent = false;
        for (User userItem : userDaoJpa.listByEventId(event.getEventId())) {
            if (user.equals(userItem)) {
                userContainsEvent = true;
            }
        }
        assertTrue(userContainsEvent);
        
        Long firstCount = userDaoJpa.getPaticipantCount(event.getEventId());
        
        // Addition
        User user2 = new User();
        user2.setEmail("email@email.com");
        user2.setJoinDate(new Date());
        user2.setName("Vardenis");
        user2.setSurname("Pavardenis");
        user2.setPassword("123");
        user2.setOrExist(true);
        user2 = userDaoJpa.merge(user2);
        
        event.getUsers().add(user2);
        event = eventDaoJpa.merge(event);
        
        Long secondCount = userDaoJpa.getPaticipantCount(event.getEventId());
        assertTrue(firstCount == secondCount - 1);
        
        // Removal
        event.getUsers().remove(user2);
        Long thirdCount = userDaoJpa.getPaticipantCount(event.getEventId());
        assertTrue(thirdCount == firstCount);
        
        // Test those who joined before
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
        
        List<User> usersBefore = userDaoJpa
                .listUsersWhoJoinedBefore(calendar.getTime());
 
        boolean userContainsUsersBefore = false;
        for (User userItem : usersBefore) {
            if (userItem.equals(user)) {
                userContainsUsersBefore = true;
                break;
            }
        }
        assertTrue(userContainsUsersBefore);
    }
    
    @Test
    public void testOnlyExistingToString() {
        User user = new User();
        user.setEmail("email@email.com");
        user.setJoinDate(new Date());
        user.setName("Vardenis");
        user.setSurname("Pavardenis");
        user.setPassword("123");
        user.setOrExist(true);
        userDaoJpa.merge(user);
        
        boolean existsWithThisEmail = false;
        for (String userEmail : userDaoJpa.listOnlyExistingToString()) {
            if (user.getEmail().equals(userEmail)) {
                existsWithThisEmail = true;
                break;
            }
        }
        assertTrue(existsWithThisEmail);
    }
    
    @Test
    public void testOnlyExisting() {
        User user = new User();
        user.setEmail("email@email.com");
        user.setJoinDate(new Date());
        user.setName("Vardenis");
        user.setSurname("Pavardenis");
        user.setPassword("123");
        userDaoJpa.merge(user);
        
        for (User itemUser : userDaoJpa.listOnlyExisting()) {
            assertTrue(itemUser.isOrExist());
        }
    }
}
