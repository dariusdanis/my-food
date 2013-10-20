package com.tieto.food.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.dao.FacebookDao;
import com.tieto.food.domain.entity.Facebook;

@Service
public class FacebookService {

    @Autowired
    private FacebookDao facebookDao;

    @Transactional
    public Facebook merge(Facebook facebook) {
        return facebookDao.merge(facebook);
    }

    @Transactional
    public Facebook findByUserId(Long id) {
        return facebookDao.findByUserId(id);
    }

    @Transactional
    public Facebook findByFacebookId(Long id) {
        return facebookDao.findByFacebookId(id);
    }

    public void remove(Facebook fb) {
        facebookDao.remove(fb);
    }
}