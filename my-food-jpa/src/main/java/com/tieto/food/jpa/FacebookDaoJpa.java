package com.tieto.food.jpa;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.tieto.food.domain.dao.FacebookDao;
import com.tieto.food.domain.entity.Facebook;

@Repository
public class FacebookDaoJpa implements FacebookDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Facebook merge(Facebook facebook) {
        return em.merge(facebook);
    }

    @Override
    public Facebook findByUserId(Long id) {
        return em.find(Facebook.class, id);
    }

    @Override
    public Facebook findByFacebookId(Long id) {
        TypedQuery<Facebook> query = em.createQuery(
                "SELECT f FROM Facebook f WHERE f.facebookId = :fbId",
                Facebook.class);
        query.setParameter("fbId", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void remove(Facebook fb) {
        Facebook managedFb = em.find(Facebook.class, fb.getId());
        em.remove(managedFb);
    }
}