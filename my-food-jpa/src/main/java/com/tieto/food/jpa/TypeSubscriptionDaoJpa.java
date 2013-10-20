package com.tieto.food.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.tieto.food.domain.dao.TypeSubscriptionDao;
import com.tieto.food.domain.entity.Type;
import com.tieto.food.domain.entity.TypeSubscription;
import com.tieto.food.domain.entity.User;

@Repository
public class TypeSubscriptionDaoJpa implements TypeSubscriptionDao {

    private static final String USER_ID = "userId";

    @PersistenceContext
    private EntityManager em;

    @Override
    public TypeSubscription merge(TypeSubscription typeSubscription) {
        return em.merge(typeSubscription);
    }

    @Override
    public List<Type> getTypesByUser(User user) {
        TypedQuery<Type> listOfType = em.createQuery(
                "Select ts.type FROM TypeSubscription ts WHERE "
                        + "ts.user.userId = :userId", Type.class);
        listOfType.setParameter(USER_ID, user.getId());
        return listOfType.getResultList();
    }

    @Override
    public List<String> getTypesNameByUser(User user) {
        TypedQuery<String> listOfType = em.createQuery(
                "SELECT ts.type.type FROM TypeSubscription ts WHERE"
                        + " ts.user.userId = :userId", String.class);
        listOfType.setParameter(USER_ID, user.getId());
        return listOfType.getResultList();
    }

    @Override
    public void removeAllByUser(User user) {
        Query query = em.createQuery("DELETE FROM TypeSubscription ts WHERE "
                + "ts.user.userId = :userId");
        query.setParameter(USER_ID, user.getId());
        query.executeUpdate();
    }

    @Override
    public void remove(List<Type> list, User user) {
        Query query = em.createQuery("DELETE FROM TypeSubscription ts WHERE "
                + "ts.user.userId = " + ":userId AND ts.type.typeId = :typeId");
        for (Type type : list) {
            query.setParameter("typeId", type.getTypeId());
            query.setParameter(USER_ID, user.getUserId());
            query.executeUpdate();
        }
    }

    @Override
    public void merge(List<Type> list, User user) {
        for (Type type : list) {
            em.merge(new TypeSubscription(user, type));
        }
    }
}