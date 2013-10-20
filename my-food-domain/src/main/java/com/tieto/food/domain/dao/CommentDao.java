package com.tieto.food.domain.dao;

import java.util.List;

import com.tieto.food.domain.entity.Comment;
import com.tieto.food.domain.entity.Event;

public interface CommentDao {

    Comment merge(Comment comment);

    List<Comment> listByEvent(Event event);

    void remove(Comment comment);

}
