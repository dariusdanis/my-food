package com.tieto.food.jpa;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.entity.UserSubscription;
import com.tieto.food.jpa.UserDaoJpa;
import com.tieto.food.jpa.UserSubscriptionDaoJpa;

@Transactional
public class UserSubscriptionDaoJpaTest extends DaoJpaTestBase {
    private static List<User> mockSubscribedUsersList;
    private static User mockSubscriber;

    @Autowired
    private UserSubscriptionDaoJpa userSubscriptionDaoJpa;

    @Autowired
    private UserDaoJpa userDaoJpa;

    @BeforeClass
    public static void initMockData() {
        mockSubscriber = new User("tomas.rupsys@tietocamp.eu", "Tomas Petras",
                "Rupsys");
        mockSubscribedUsersList = Arrays.asList(new User("darius@d.com",
                "Darius", "D"), new User("tomas@k.com", "Tomas", "K"),
                new User("laisvydas@s.com", "Laisvydas", "S"));
    }

    private void initMocksViaJpa() {
        mockSubscriber = userDaoJpa.merge(mockSubscriber);
        mockSubscribedUsersList = Arrays.asList(
                userDaoJpa.merge(mockSubscribedUsersList.get(0)),
                userDaoJpa.merge(mockSubscribedUsersList.get(1)),
                userDaoJpa.merge(mockSubscribedUsersList.get(2)));
    }

    @Test
    public void testAdding() {
        // One type of merge
        initMocksViaJpa();
        int cachedAmountOfUsers = 0;
        for (User subscribedUser : mockSubscribedUsersList) {
            userSubscriptionDaoJpa.merge(new UserSubscription(mockSubscriber,
                    subscribedUser));
            int amountOfUsers = userSubscriptionDaoJpa
                    .getSubscribedUsersByUser(mockSubscriber).size();
            assertTrue(amountOfUsers == ++cachedAmountOfUsers);
        }
        userSubscriptionDaoJpa.removeAllByUser(mockSubscriber);

        // Another type of merge
        mockSubscriber = userDaoJpa.merge(mockSubscriber);
        userSubscriptionDaoJpa.merge(mockSubscribedUsersList, mockSubscriber);
        int amountOfUsers = userSubscriptionDaoJpa.getSubscribedUsersByUser(
                mockSubscriber).size();
        assertTrue(amountOfUsers == mockSubscribedUsersList.size());
    }

    @Test
    public void testRemoveAFew() {
        initMocksViaJpa();
        userSubscriptionDaoJpa.merge(mockSubscribedUsersList, mockSubscriber);
        userSubscriptionDaoJpa.remove(
                Arrays.asList(mockSubscribedUsersList.get(0)), mockSubscriber);
        for (User usersLeftInJpa : userSubscriptionDaoJpa
                .getSubscribedUsersByUser(mockSubscriber)) {
            assertNotEquals(usersLeftInJpa, mockSubscribedUsersList.get(0));
        }
    }

    @Test
    public void testRemoveAllByUser() {
        initMocksViaJpa();
        userSubscriptionDaoJpa.merge(mockSubscribedUsersList, mockSubscriber);
        userSubscriptionDaoJpa.removeAllByUser(mockSubscriber);
        assertTrue(userSubscriptionDaoJpa.getSubscribedUsersByUser(
                mockSubscriber).size() == 0);
    }
}