package com.tieto.food.domain.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.dao.EventDao;
import com.tieto.food.domain.entity.Event;

@Service
public class EventService {

    @Autowired
    private EventDao eventDao;

    @Transactional(readOnly = true)
    public Event loadById(Long eventId) {
        return eventDao.loadById(eventId);
    }

    @Transactional
    public List<Event> listAllWithParticipant(Long userId) {
        return eventDao.listAllWithParticipant(userId);
    }

    @Transactional
    public List<Event> listAll() {
        return eventDao.listAll();
    }

    @Transactional
    public List<Event> listAllWhereCreatedBy(Long userId) {
        return eventDao.listAllWhereCreatedBy(userId);
    }

    @Transactional
    public Event merge(Event event) {
        return eventDao.merge(event);
    }

    @Transactional
    public List<Event> listEventsThatHappenedBefore(Date beforeDate) {
        return eventDao.listEventsThatHappenedBefore(beforeDate);
    }

    @Transactional
    public void remove(Event event) {
        eventDao.remove(event);
    }

    @Transactional
    public List<Event> listValidFutureEvents() {
        return eventDao.listValidFutureEvents();
    }

    @Transactional
    public List<Event> searchEvents(String text, String user, String place) {
        return eventDao.searchEvents(text, user, place);
    }

    @Transactional
    public List<Event> listAllThreatedEventsInDescendingOrder() {
        List<Event> eventsList = eventDao.listAllThreatedEvents();
        Collections.sort(eventsList, new SpamThreatComparator());
        return eventsList;
    }

    private class SpamThreatComparator implements Comparator<Event> {
        @Override
        public int compare(Event event1, Event event2) {
            return (int) (event2.getTimesReportedAsSpam() - event1
                    .getTimesReportedAsSpam());
        }
    }
}