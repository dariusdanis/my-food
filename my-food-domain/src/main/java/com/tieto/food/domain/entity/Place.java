package com.tieto.food.domain.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Places")
public class Place {

    @Id
    @GeneratedValue
    private Long placeId;

    @Column(nullable = false)
    private String place;

    @OneToMany(targetEntity = Event.class, mappedBy = "eventPlace")
    private List<Event> event;

    @Column(nullable = false)
    private boolean orExist;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String address;

    public Place() {
    }

    public Place(String place) {
        this.place = place;
    }

    public Place(Long placeId) {
        this.placeId = placeId;
    }

    public Place(String place, boolean orExist) {
        this.place = place;
        this.orExist = orExist;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public List<Event> getEvent() {
        return event;
    }

    public void setEvent(List<Event> event) {
        this.event = event;
    }

    public boolean isOrExist() {
        return orExist;
    }

    public void setOrExist(boolean orExist) {
        this.orExist = orExist;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return getPlace();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Place) {
            return placeId == ((Place) obj).getPlaceId();
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + 2;
    }
}