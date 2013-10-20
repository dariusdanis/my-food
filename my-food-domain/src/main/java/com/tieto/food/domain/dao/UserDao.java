package com.tieto.food.domain.dao;

import java.util.Date;
import java.util.List;

import com.tieto.food.domain.entity.User;

public interface UserDao {
    User loadById(Long id);

    List<User> listByEventId(Long eventId);

    List<User> listAll();

    User merge(User participant);

    User loadByEmail(String email);

    void remove(User user);

    Long getPaticipantCount(Long eventId);

    List<User> listOnlyExisting();

    List<User> listUsersWhoJoinedBefore(Date until);

    List<String> listOnlyExistingToString();

}
