package com.tieto.food.domain.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tieto.food.domain.dao.UserDao;
import com.tieto.food.domain.entity.User;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Transactional(readOnly = true)
    public List<User> listAll() {
        return userDao.listAll();
    }

    @Transactional
    public User loadByEmail(String email) {
        return userDao.loadByEmail(email);
    }

    @Transactional
    public List<User> listUsersWhoJoinedBefore(Date until) {
        return userDao.listUsersWhoJoinedBefore(until);
    }

    @Transactional(readOnly = true)
    public List<User> listByEventId(Long eventId) {
        return userDao.listByEventId(eventId);
    }

    @Transactional(readOnly = true)
    public Long getPaticipantCount(Long eventId) {
        return userDao.getPaticipantCount(eventId);
    }

    @Transactional
    public User merge(User user) {
        return userDao.merge(user);
    }

    @Transactional
    public User loadById(Long id) {
        return userDao.loadById(id);
    }

    @Transactional
    public void remove(User user) {
        userDao.remove(user);
    }

    @Transactional
    public List<User> listOnlyExisting() {
        return userDao.listOnlyExisting();
    }

    @Transactional
    public List<String> listOnlyExistingToString() {
        return userDao.listOnlyExistingToString();
    }
}