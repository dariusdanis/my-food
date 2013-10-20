package com.tieto.food.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.tieto.food.domain.dao.EventDao;
import com.tieto.food.domain.entity.Event;

@Repository
public class EventDaoJpa implements EventDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Event> listAll() {
        TypedQuery<Event> query = em.createQuery("SELECT u FROM Event u",
                Event.class);
        return query.getResultList();
    }

    @Override
    public Event loadById(final Long eventId) {
        return em.find(Event.class, eventId);
    }

    @Override
    public Event merge(Event event) {
        return em.merge(event);
    }

    @Override
    public void remove(Event event) {
        Event managedEvent = em.find(Event.class, event.getEventId());
        em.remove(managedEvent);
    }

    @Override
    public List<Event> listValidFutureEvents() {
        TypedQuery<Event> query = em.createQuery("SELECT e " + "FROM Event e "
                + "WHERE e.eventDate >= CURRENT_TIMESTAMP "
                + "AND e.isSpam IS FALSE " + "ORDER BY e.eventDate ASC",
                Event.class);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("all") 
    public List<Event> searchEvents(String text, 
            String user, String place) {
        if (isEmpty(text, user, place)) {
            return listValidFutureEvents();
        }
        TypedQuery<Event> query1 = em.createQuery(
                "SELECT DISTINCT e FROM Event e WHERE (e.description LIKE "
                        + ":text OR e.title LIKE :text) AND e.eventDate >= "
                        + "CURRENT_TIMESTAMP", Event.class);

        TypedQuery<Event> query2 = em.createQuery(
                "SELECT DISTINCT e FROM Event e JOIN FETCH e.users u "
                        + "WHERE CONCAT(u.name, ' ' ,u.surname) LIKE :user "
                        + "AND e.eventDate >= CURRENT_TIMESTAMP", Event.class);

        TypedQuery<Event> query3 = em.createQuery(
                "SELECT DISTINCT e FROM Event e JOIN FETCH e.eventPlace p "
                        + "WHERE p.place LIKE :place AND "
                        + "e.eventDate >= CURRENT_TIMESTAMP", Event.class);

        if (text != null) {
            String trimmedText = text.trim();
            text = "%" + trimmedText + "%";
            text = text.replaceAll("\\s+", " ");
            query1.setParameter("text", text);
        }
        if (user != null) {
            String trimmedUser = user.trim();
            user = "%" + trimmedUser + "%";
            user = user.replaceAll("\\s+", " ");
            query2.setParameter("user", user.replaceAll("\\s+", " "));
        }
        if (place != null) {
            String trimmedPlace = place.trim();
            place = "%" + trimmedPlace + "%";
            place = place.replaceAll("\\s+", " ");
            query3.setParameter("place", place);
        }
        List<Event> list;
        if (text != null && text.length() > 2) {
            list = query1.getResultList();
            if (user != null && user.length() > 2) {
                list.retainAll(query2.getResultList());
            }
            if (place != null && place.length() > 2) {
                list.retainAll(query3.getResultList());
            }
            return list;
        } else if (user != null && user.length() > 2) {
            list = query2.getResultList();
            if (place != null && place.length() > 2) {
                list.retainAll(query3.getResultList());
            }
            return list;
        } else {
            list = query3.getResultList();
            return list;
        }
    }

    @SuppressWarnings("all") 
    private boolean isEmpty(String text, String user, String place) {
        return ((text == null 
                || text.trim().length() == 0)
                && (user == null 
                || user.trim().length() == 0) 
                && (place == null || place
                    .trim().length() == 0));
    }

    @Override
    public List<Event> listAllWithParticipant(Long userId) {
        TypedQuery<Event> query = em.createQuery(

        "SELECT e FROM Event e JOIN FETCH e.users u WHERE u.id = :id"
                + " AND e.createdBy != :id ORDER BY e.eventDate DESC",
                Event.class);
        query.setParameter("id", userId);
        return query.getResultList();
    }

    @Override
    public List<Event> listAllWhereCreatedBy(Long userId) {
        TypedQuery<Event> query = em.createQuery(
                "SELECT e FROM Event e WHERE e.createdBy "
                        + "= :id ORDER BY e.eventDate DESC", Event.class);
        query.setParameter("id", userId);
        return query.getResultList();
    }

    @Override
    public List<Event> listAllThreatedEvents() {
        TypedQuery<Event> query = em.createQuery("SELECT e " + "FROM Event e "
                + "WHERE e.timesReportedAsSpam > 0", Event.class);
        return query.getResultList();
    }

    @Override
    public List<Event> listEventsThatHappenedBefore(Date beforeDate) {
        TypedQuery<Event> query = em
                .createQuery(
                        "SELECT e FROM Event e WHERE e.eventDate <= :date",
                        Event.class);
        query.setParameter("date", beforeDate, TemporalType.DATE);
        return query.getResultList();
    }
}