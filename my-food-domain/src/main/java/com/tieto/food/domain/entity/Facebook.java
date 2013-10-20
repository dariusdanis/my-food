package com.tieto.food.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Facebook {

    @Id
    private Long id;

    @Column(nullable = false)
    private Long facebookId;

    public Facebook() {
    }

    public Facebook(Long id, Long facebookId) {
        this.id = id;
        this.facebookId = facebookId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(Long facebookId) {
        this.facebookId = facebookId;
    }
}
