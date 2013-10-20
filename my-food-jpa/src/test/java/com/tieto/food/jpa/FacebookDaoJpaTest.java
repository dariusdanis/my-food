package com.tieto.food.jpa;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.entity.Facebook;
import com.tieto.food.jpa.FacebookDaoJpa;

@Transactional
public class FacebookDaoJpaTest extends DaoJpaTestBase {

    @Autowired
    private FacebookDaoJpa facebookDaoJpa;

    @Test
    public void savesFindsAndRemovesFacebook() {
        Facebook facebook = new Facebook();
        facebook.setFacebookId(1L);
        facebook.setId(1L);
        Facebook merged = facebookDaoJpa.merge(facebook);
        assertNotNull(facebookDaoJpa.findByFacebookId(facebook.getFacebookId()));
        assertNotNull(facebookDaoJpa.findByUserId(facebook.getId()));
        facebookDaoJpa.remove(merged);
        assertNull(facebookDaoJpa.findByFacebookId(facebook.getFacebookId()));
        assertNull(facebookDaoJpa.findByUserId(facebook.getId()));
    }
}