package com.tieto.food.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.tieto.food.domain.dao.TypeDao;
import com.tieto.food.domain.entity.Type;

@Repository
public class TypeDaoJpa implements TypeDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Type> listAll() {
        TypedQuery<Type> query = em.createQuery("SELECT t FROM Type t",
                Type.class);
        return query.getResultList();
    }

    @Override
    public List<String> allToString() {
        TypedQuery<String> query = em.createQuery("SELECT type FROM Type",
                String.class);
        return query.getResultList();
    }

    @Override
    public Type merge(Type type) {
        return em.merge(type);
    }

    @Override
    public Type findType(Long id) {
        return em.find(Type.class, id);
    }
}