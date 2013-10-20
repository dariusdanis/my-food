package com.tieto.food.jpa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.tieto.food.domain.dao.TextSubscriptionDao;
import com.tieto.food.domain.entity.TextSubscription;
import com.tieto.food.domain.entity.User;

@Repository
public class TextSubscriptionDaoJpa implements TextSubscriptionDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void merge(TextSubscription textSubscription) {
        em.merge(textSubscription);
    }

    @Override
    public void merge(List<String> textList, User user) {
        for (String textLine : textList) {
            em.merge(new TextSubscription(user, textLine));
        }
    }

    @Override
    public void remove(List<String> textList, User user) {
        Query query = em.createQuery("DELETE FROM TextSubscription ts "
                + "WHERE ts.subscriber.userId = :userId "
                + "AND ts.textLine = :textLine");
        for (String textLine : textList) {
            query.setParameter("textLine", textLine);
            query.setParameter("userId", user.getUserId());
            query.executeUpdate();
        }
    }

    @Override
    public void removeAllByUser(User user) {
        Query query = em.createQuery("DELETE FROM TextSubscription ts "
                + "WHERE ts.subscriber.userId = :userId");
        query.setParameter("userId", user.getUserId());
        query.executeUpdate();
    }

    @Override
    public List<String> getTextsByUser(User user) {
        TypedQuery<String> listOfTextLines = em.createQuery(
                "SELECT ts.textLine FROM TextSubscription ts WHERE"
                        + " ts.subscriber.userId = :userId", String.class);
        listOfTextLines.setParameter("userId", user.getId());
        return listOfTextLines.getResultList();
    }

    @Override
    public List<User> getUsersByText(List<String> stringList) {
        TypedQuery<User> listOfTextLines = em.createQuery(
                "SELECT DISTINCT ts.subscriber " + "FROM TextSubscription ts "
                        + "WHERE ts.textLine LIKE :textLine", User.class);
        Set<User> tempResultingSet = new HashSet<User>();
        for (String textLine : stringList) {
            listOfTextLines.setParameter("textLine", textLine);
            tempResultingSet.addAll(listOfTextLines.getResultList());
        }
        return new ArrayList<User>(tempResultingSet);
    }
}