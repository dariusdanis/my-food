package com.tieto.food.domain.dao;

import java.util.Date;
import java.util.List;

import com.tieto.food.domain.entity.Event;

public interface EventDao {
    List<Event> listValidFutureEvents();

    Event loadById(Long eventId);

    Event merge(Event event);

    void remove(Event event);

    List<Event> listAll();

    List<Event> listAllWithParticipant(Long userId);

    List<Event> listAllWhereCreatedBy(Long userId);

    List<Event> searchEvents(String text, String user, String place);

    List<Event> listAllThreatedEvents();

    List<Event> listEventsThatHappenedBefore(Date beforeDate);
}