package com.tieto.food.domain.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Types")
public class Type {

    @Id
    @GeneratedValue
    private Long typeId;

    @Column(nullable = false)
    private String type;

    @OneToMany(targetEntity = Event.class, cascade = CascadeType.ALL, mappedBy = "eventType")
    private List<Event> event;

    @OneToMany(mappedBy = "type")
    private List<TypeSubscription> typeSubscription;

    public Type() {
    }

    public Type(String type) {
        this.type = type;
    }

    public Type(Long typeId) {
        this.typeId = typeId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Event> getEvent() {
        return event;
    }

    public void setEvent(List<Event> event) {
        this.event = event;
    }

    public List<TypeSubscription> getTypeSubscription() {
        return typeSubscription;
    }

    public void setTypeSubscription(List<TypeSubscription> typeSubscription) {
        this.typeSubscription = typeSubscription;
    }

    @Override
    public String toString() {
        return getType();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Type) {
            return typeId == ((Type) obj).getTypeId();
        }
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 2;
    }
}