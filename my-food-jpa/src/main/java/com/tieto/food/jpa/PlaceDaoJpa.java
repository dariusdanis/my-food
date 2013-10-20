package com.tieto.food.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.tieto.food.domain.dao.PlaceDao;
import com.tieto.food.domain.entity.Place;

@Repository
public class PlaceDaoJpa implements PlaceDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Place merge(Place place) {
        return em.merge(place);
    }

    @Override
    public List<Place> listAll() {
        TypedQuery<Place> query = em.createQuery("SELECT p FROM Place p",
                Place.class);
        return query.getResultList();
    }

    @Override
    public List<String> allToString() {
        TypedQuery<String> query = em.createQuery("SELECT place FROM Place",
                String.class);
        return query.getResultList();
    }

    public void remove(Long id) {
        Place place = em.find(Place.class, id);
        place.setOrExist(false);
        em.merge(place);
    }

    @Override
    public List<Place> listOnlyExisting() {
        TypedQuery<Place> query = em.createQuery(
                "SELECT p FROM Place p WHERE p.orExist = true", Place.class);
        return query.getResultList();
    }

    @Override
    public List<String> listOnlyExistingToString() {
        TypedQuery<String> query = em.createQuery(
                "SELECT p.place FROM Place p WHERE p.orExist = true",
                String.class);
        return query.getResultList();
    }

    @Override
    public Place findByPlaceName(String place) {
        TypedQuery<Place> query = em.createQuery(
                "SELECT p FROM Place p WHERE p.place = :place", Place.class);
        query.setParameter("place", place);
        Place p;
        try {
            p = query.getSingleResult();
        } catch (Exception e) {
            p = null;
        }
        return p;
    }
}