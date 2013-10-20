package com.tieto.food.jpa;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.tieto.food.domain.entity.Type;
import com.tieto.food.domain.entity.TypeSubscription;
import com.tieto.food.domain.entity.User;
import com.tieto.food.jpa.TypeDaoJpa;
import com.tieto.food.jpa.TypeSubscriptionDaoJpa;
import com.tieto.food.jpa.UserDaoJpa;

public class TypeSubscriptionDaoJpaTest extends DaoJpaTestBase {
    private static User mockUser;
    private static Type mockTypeOne;
    private static Type mockTypeTwo;
    private static Type mockTypeThree;

    @Autowired
    private UserDaoJpa userDaoJpa;

    @Autowired
    private TypeSubscriptionDaoJpa typeSubscriptionDaoJpa;

    @Autowired
    private TypeDaoJpa typeDaoJpa;

    @BeforeClass
    public static void initMockData() {
        mockUser = new User("tprupsys@gmail.com", "Tomas Petras", "Rupsys");
        mockTypeOne = new Type("Takeaway");
        mockTypeTwo = new Type("On place");
        mockTypeThree = new Type("Picnic");
    }

    private void initMocksViaJpa() {
        mockUser = userDaoJpa.merge(mockUser);
        mockTypeOne = typeDaoJpa.merge(mockTypeOne);
        mockTypeTwo = typeDaoJpa.merge(mockTypeTwo);
        mockTypeThree = typeDaoJpa.merge(mockTypeThree);
    }

    @Test
    public void testMergeViaObjectInject() {
        initMocksViaJpa();
        typeSubscriptionDaoJpa
                .merge(new TypeSubscription(mockUser, mockTypeOne));
        assertTrue(typeSubscriptionDaoJpa.getTypesNameByUser(mockUser).size() == 1);
    }

    @Test
    public void testMergeViaTwoArgs() {
        initMocksViaJpa();
        typeSubscriptionDaoJpa.merge(
                Arrays.asList(mockTypeOne, mockTypeTwo, mockTypeThree),
                mockUser);
        assertTrue(typeSubscriptionDaoJpa.getTypesByUser(mockUser).size() == 3);
    }

    @Test
    public void testGetTypes() {
        initMocksViaJpa();
        typeSubscriptionDaoJpa
                .merge(new TypeSubscription(mockUser, mockTypeOne));
        List<Type> typeList = typeSubscriptionDaoJpa.getTypesByUser(mockUser);
        assertEquals(typeList.get(0), mockTypeOne);
    }

    @Test
    public void testGetTypeNames() {
        initMocksViaJpa();
        typeSubscriptionDaoJpa
                .merge(new TypeSubscription(mockUser, mockTypeOne));
        List<String> stringList = typeSubscriptionDaoJpa
                .getTypesNameByUser(mockUser);
        assertEquals(stringList.get(0), mockTypeOne.getType());
    }

    @Test
    public void testTwoRemoves() {
        // Remove a single item
        initMocksViaJpa();
        typeSubscriptionDaoJpa
                .merge(new TypeSubscription(mockUser, mockTypeOne));
        typeSubscriptionDaoJpa.remove(Arrays.asList(mockTypeOne), mockUser);
        assertTrue(typeSubscriptionDaoJpa.getTypesNameByUser(mockUser).size() == 0);

        // Remove all
        initMocksViaJpa();
        typeSubscriptionDaoJpa.merge(
                Arrays.asList(mockTypeOne, mockTypeTwo, mockTypeThree),
                mockUser);
        typeSubscriptionDaoJpa.removeAllByUser(mockUser);
        assertTrue(typeSubscriptionDaoJpa.getTypesNameByUser(mockUser).size() == 0);
    }
}
