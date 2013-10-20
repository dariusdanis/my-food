package com.tieto.food.jpa;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
import com.tieto.food.jpa.PlaceDaoJpa;
import com.tieto.food.jpa.TypeDaoJpa;
import com.tieto.food.jpa.UserDaoJpa;

@Transactional
public class EventDaoJpaTest extends DaoJpaTestBase {

    @Autowired
    private EventDaoJpa eventDaoJpa;

    @Autowired
    private PlaceDaoJpa placeDaoJpa;

    @Autowired
    private TypeDaoJpa typeDaoJpa;

    @Autowired
    private UserDaoJpa userDaoJpa;

    @Test
    public void searchesEvents() {
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
        eventDaoJpa.merge(event);

        Event event2 = new Event();
        User user2 = new User();
        user2.setEmail("epastas@epastas.lt");
        user2.setJoinDate(new Date());
        user2.setName("Petras");
        user2.setSurname("Petrauskas");
        user2.setPassword("123");
        user2 = userDaoJpa.merge(user2);
        event2.setCreatedBy(user2.getUserId());
        users.clear();
        users.add(user2);
        event2.setUsers(users);
        event2.setDescription("aprasymas");
        event2.setEventDate(new Date(new GregorianCalendar(2111, 11, 11)
                .getTimeInMillis()));
        Place place2 = new Place();
        place2.setPlace("Kebabine");
        place2.setLatitude(13.25d);
        place2.setLongitude(24.9d);
        place2.setAddress("test address");
        event2.setEventPlace(placeDaoJpa.merge(place2));
        event2.setTitle("pavadinimas");
        Type type2 = new Type();
        type2.setType("On place");
        event2.setTimesReportedAsSpam(0L);
        event2.setEventType(typeDaoJpa.merge(type2));
        eventDaoJpa.merge(event2);
        assertEquals(
                eventDaoJpa.searchEvents("description", null, null).size(), 1);
        assertTrue(eventDaoJpa.searchEvents("description", "", "").get(0)
                .getDescription().equals("descriptionas"));
        assertEquals(
                eventDaoJpa.searchEvents("aprasymas", "Petras Petrauskas",
                        "Kebabine").size(), 1);
        assertEquals(eventDaoJpa.searchEvents("asd", "Petras", "Kebabai")
                .size(), 0);
        assertEquals(eventDaoJpa.searchEvents(null, null, null)
                .size(), eventDaoJpa.listValidFutureEvents().size());
        assertEquals(eventDaoJpa.searchEvents(null, null, "Kebabine")
                .size(), 1);
        assertEquals(eventDaoJpa.searchEvents(null, "Petras Petrauskas", null)
                .size(), 1);
        assertEquals(eventDaoJpa.searchEvents(null, "Petras Petrauskas", "Kebabine")
                .size(), 1);
    }

    @Test
    public void loadsSavedEventAndRemovesIt() {
        int counter = eventDaoJpa.listAll().size();
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
		event.setTimesReportedAsSpam(0L);
		Type type = new Type();
		type.setType("Take away");
		event.setEventType(typeDaoJpa.merge(type));
		Event merged = eventDaoJpa.merge(event);
		Long eventId = merged.getEventId();
		Event loaded = eventDaoJpa.loadById(eventId);
		assertNotNull(loaded);
		assertEquals(eventDaoJpa.listAll().size(), counter+1);
		assertEquals(loaded.getCreatedBy(), event.getCreatedBy());
		assertEquals(loaded.getDescription(), event.getDescription());
		assertEquals(loaded.getEventDate(), event.getEventDate());
		assertEquals(loaded.getTitle(), event.getTitle());
		assertEquals(loaded.getEventPlace().getPlace(), event.getEventPlace()
				.getPlace());
		assertEquals(loaded.getEventType().getType(), event.getEventType()
				.getType());
		assertEquals(loaded.getUsers().get(0).getUserId(), event.getUsers()
				.get(0).getUserId());
		eventDaoJpa.remove(merged);
		assertNull(eventDaoJpa.loadById(eventId));
	}


    @Test
    public void listsFutureEvents() {
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
        event.setEventDate(new Date(new GregorianCalendar(1999, 11, 20)
                .getTimeInMillis()));
        Place place = new Place();
        place.setPlace("Cili kaimas");
        place.setLatitude(20.42d);
        place.setLongitude(15.81d);
        place.setAddress("test address");
        event.setEventPlace(placeDaoJpa.merge(place));
        event.setTitle("title");
        Type type = new Type();
        type.setType("Take away");
        event.setEventType(typeDaoJpa.merge(type));
        event.setTimesReportedAsSpam(0L);
        event = eventDaoJpa.merge(event);

        Event event2 = new Event();
        User user2 = new User();
        user2.setEmail("epastas@epastas.lt");
        user2.setJoinDate(new Date());
        user2.setName("Petras");
        user2.setSurname("Petrauskas");
        user2.setPassword("123");
        user2 = userDaoJpa.merge(user2);
        event2.setCreatedBy(user2.getUserId());
        users.clear();
        users.add(user2);
        event2.setUsers(users);
        event2.setDescription("aprasymas");
        event2.setEventDate(new Date(new GregorianCalendar(2111, 11, 11)
                .getTimeInMillis()));
        Place place2 = new Place();
        place2.setPlace("Kebabine");
        place2.setLatitude(13.25d);
        place2.setLongitude(24.9d);
        place2.setAddress("test address");
        event2.setEventPlace(placeDaoJpa.merge(place2));
        event2.setTitle("pavadinimas");
        Type type2 = new Type();
        type2.setType("On place");
        event2.setEventType(typeDaoJpa.merge(type2));
        event2.setTimesReportedAsSpam(0L);
        event2 = eventDaoJpa.merge(event2);
        event2.setTimesReportedAsSpam(0L);

        List<Event> events = eventDaoJpa.listValidFutureEvents();
        assertEquals(false, events.contains(event));
        assertEquals(true, events.contains(event2));
    }

    @Test
    public void listsAllWithParticipantAndAllWhereCreatedBy() {
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
        event.setEventDate(new Date(new GregorianCalendar(2013, 11, 20)
                .getTimeInMillis()));
        Place place = new Place();
        place.setPlace("Cili kaimas");
        place.setLatitude(13.25d);
        place.setLongitude(24.9d);
        place.setAddress("test address");
        event.setTimesReportedAsSpam(0L);
        event.setEventPlace(placeDaoJpa.merge(place));
        event.setTitle("title");
        Type type = new Type();
        type.setType("Take away");
        event.setEventType(typeDaoJpa.merge(type));
        eventDaoJpa.merge(event);

        Event event2 = new Event();
        User user2 = new User();
        user2.setEmail("epastas@epastas.lt");
        user2.setJoinDate(new Date());
        user2.setName("Petras");
        user2.setSurname("Petrauskas");
        user2.setPassword("123");
        user2 = userDaoJpa.merge(user2);
        event2.setCreatedBy(user2.getUserId());
        users.add(user2);
        event2.setUsers(users);
        event2.setDescription("aprasymas");
        event2.setEventDate(new Date(new GregorianCalendar(2014, 11, 11)
                .getTimeInMillis()));
        event2.setTimesReportedAsSpam(0L);
        Place place2 = new Place();
        place2.setPlace("Kebabine");
        place2.setLatitude(13.25d);
        place2.setLongitude(24.9d);
        place2.setAddress("test address");
		event2.setEventPlace(placeDaoJpa.merge(place2));
		event2.setTitle("pavadinimas");
		Type type2 = new Type();
		type2.setType("On place");
		event2.setEventType(typeDaoJpa.merge(type2));
		eventDaoJpa.merge(event2);
		assertEquals(eventDaoJpa.listAllWithParticipant(user.getUserId())
				.size(), 1);
		assertEquals(eventDaoJpa.listAllWithParticipant(user2.getUserId())
				.size(), 0);
		assertEquals(eventDaoJpa.listAllWithParticipant(17L)
				.size(), 0);
		assertEquals(eventDaoJpa.listAllWhereCreatedBy(user.getUserId()).size(), 1);
		assertEquals(eventDaoJpa.listAllWhereCreatedBy(17L).size(), 0);		
	}
    
    @Test
    public void listsAllThreatedEvents() {
        int size = eventDaoJpa.listAllThreatedEvents().size();
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
        event.setTimesReportedAsSpam(1L);
        eventDaoJpa.merge(event);
        assertEquals(eventDaoJpa.listAllThreatedEvents().size(), size+1);
    }
    
    @Test
    public void listsEventsThatHappenedBefore() {
        Date before = new Date((new GregorianCalendar(2013, 9, 12)
        .getTimeInMillis()));
        int size = eventDaoJpa.listEventsThatHappenedBefore(before).size();
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
        event.setEventDate(new Date(new GregorianCalendar(2013, 8, 12)
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
        event.setTimesReportedAsSpam(1L);
        eventDaoJpa.merge(event);
        assertEquals(eventDaoJpa.listEventsThatHappenedBefore(before).size(), size+1);
    }
}
