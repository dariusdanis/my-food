package com.tieto.food.jpa;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.entity.Comment;
import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.Place;
import com.tieto.food.domain.entity.Type;
import com.tieto.food.domain.entity.User;

@Transactional
public class CommeantDaoJpaTest extends DaoJpaTestBase {
    
    @Autowired
    private CommentDaoJpa commentDaoJpa;
    
    @Autowired
    private UserDaoJpa userDaoJpa;
    
    @Autowired
    private PlaceDaoJpa placeDaoJpa;
    
    @Autowired
    private TypeDaoJpa typeDaoJpa;
    
    @Autowired
    private EventDaoJpa eventDaoJpa;
    
    @Test
    public void addAndRemoveNewComment() {
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
        Comment comment = new Comment("test", user, event);
        comment.setCommentDate(new Date());
        comment = commentDaoJpa.merge(comment);
        event = eventDaoJpa.loadById(event.getEventId());
        assertEquals(commentDaoJpa.listByEvent(event).size() ,1);
        commentDaoJpa.remove(comment);
        assertEquals(commentDaoJpa.listByEvent(event).size() ,0);
        
    }
    

    
}
