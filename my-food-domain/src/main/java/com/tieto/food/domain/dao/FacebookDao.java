package com.tieto.food.domain.dao;

import com.tieto.food.domain.entity.Facebook;

public interface FacebookDao {
    Facebook merge(Facebook facebook);

    Facebook findByUserId(Long id);

    Facebook findByFacebookId(Long id);

    void remove(Facebook fb);
}
