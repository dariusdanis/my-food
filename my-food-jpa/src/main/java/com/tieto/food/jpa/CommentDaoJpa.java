package com.tieto.food.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.tieto.food.domain.dao.CommentDao;
import com.tieto.food.domain.entity.Comment;
import com.tieto.food.domain.entity.Event;

@Repository
public class CommentDaoJpa implements CommentDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Comment merge(Comment comment) {
        return em.merge(comment);
    }

    @Override
    public List<Comment> listByEvent(Event event) {
        TypedQuery<Comment> query = em.createQuery(
                "SELECT comment FROM Comment comment WHERE "
                        + "comment.event.eventId = :eventId "
                        + "ORDER BY comment.commentDate ASC", Comment.class);
        query.setParameter("eventId", event.getEventId());
        return query.getResultList();
    }

    @Override
    public void remove(Comment comment) {
        try {
            Comment managedComment = em.find(Comment.class,
                    comment.getCommentId());
            em.remove(managedComment);
        } catch (Exception e) {
         // Do nothing
        }
    }
}
