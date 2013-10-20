package com.tieto.food.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.dao.CommentDao;
import com.tieto.food.domain.entity.Comment;
import com.tieto.food.domain.entity.Event;

@Service
public class CommentService {
    
    @Autowired
    private CommentDao commentDao;

    @Transactional
    public Comment merge(Comment comment) {
        return commentDao.merge(comment);
    }

    @Transactional
    public List<Comment> listByEvent(Event event) {
        return commentDao.listByEvent(event);
    }

    @Transactional
    public void remove(Comment comment) {
        commentDao.remove(comment);
    }
}