package com.tieto.food.jpa;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.entity.TextSubscription;
import com.tieto.food.domain.entity.User;
import com.tieto.food.jpa.TextSubscriptionDaoJpa;
import com.tieto.food.jpa.UserDaoJpa;

@Transactional
public class TextSubscriptionDaoJpaTest extends DaoJpaTestBase {
    private static User mockSubscriber;
    private static List<String> mockTextLines;

    @Autowired
    private TextSubscriptionDaoJpa textSubscriptionDaoJpa;

    @Autowired
    private UserDaoJpa userDaoJpa;

    @BeforeClass
    public static void initializeMockData() {
        mockSubscriber = new User("tomas.rupsys@tietocamp.eu", "Tomas Petras",
                "Rupsys");
        mockTextLines = Arrays.asList("coffee", "tea", "meatballs", "beetroot",
                "pizza");
    }

    @Test
    public void testMergingWithoutConstructor() {
        mockSubscriber = userDaoJpa.merge(mockSubscriber);
        textSubscriptionDaoJpa.merge(mockTextLines, mockSubscriber);
        assertTrue(textSubscriptionDaoJpa.getTextsByUser(mockSubscriber).size() == mockTextLines
                .size());
    }

    @Test
    public void testMergingWithConstructor() {
        mockSubscriber = userDaoJpa.merge(mockSubscriber);
        for (String textLine : mockTextLines) {
            textSubscriptionDaoJpa.merge(new TextSubscription(mockSubscriber,
                    textLine));
        }
        assertTrue(textSubscriptionDaoJpa.getTextsByUser(mockSubscriber).size() == mockTextLines
                .size());
    }

    @Test
    public void testRemoving() {
        mockSubscriber = userDaoJpa.merge(mockSubscriber);
        textSubscriptionDaoJpa.merge(mockTextLines, mockSubscriber);
        textSubscriptionDaoJpa.remove(mockTextLines, mockSubscriber);
        assertTrue(textSubscriptionDaoJpa.getTextsByUser(mockSubscriber).size() == 0);
    }

    @Test
    public void testRemovingAll() {
        mockSubscriber = userDaoJpa.merge(mockSubscriber);
        textSubscriptionDaoJpa.merge(mockTextLines, mockSubscriber);
        textSubscriptionDaoJpa.removeAllByUser(mockSubscriber);
        assertTrue(textSubscriptionDaoJpa.getTextsByUser(mockSubscriber).size() == 0);
    }

    @Test
    public void testSearchByText() {
        mockSubscriber = userDaoJpa.merge(mockSubscriber);
        textSubscriptionDaoJpa.merge(mockTextLines, mockSubscriber);
        List<User> resultList = textSubscriptionDaoJpa
                .getUsersByText(mockTextLines);
        boolean requiredUserWasFound = false;
        for (User tempUser : resultList) {
            if (tempUser.equals(mockSubscriber)) {
                requiredUserWasFound = true;
                break;
            }
        }
        assertTrue(requiredUserWasFound);
    }
}