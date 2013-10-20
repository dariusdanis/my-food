package com.tieto.food.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.entity.Type;
import com.tieto.food.jpa.TypeDaoJpa;

@Transactional
public class TypeDaoJpaTest extends DaoJpaTestBase {

    @Autowired
    private TypeDaoJpa typeDaoJpa;

    @Test
    public void savesAndListsPlaces() {
        Type type = new Type();
        type.setType("Piknikas");
        int placeCount = typeDaoJpa.listAll().size();
        typeDaoJpa.merge(type);
        assertEquals(typeDaoJpa.listAll().size(), placeCount + 1);
        assertTrue(typeDaoJpa.allToString().contains("Piknikas"));
    }

}
