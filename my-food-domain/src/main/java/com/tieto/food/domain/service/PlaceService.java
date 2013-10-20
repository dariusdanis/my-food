package com.tieto.food.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.dao.PlaceDao;
import com.tieto.food.domain.entity.Place;

@Service
public class PlaceService {

    @Autowired
    private PlaceDao placeDao;

    @Transactional(readOnly = true)
    public List<Place> listAll() {
        return placeDao.listAll();
    }

    @Transactional(readOnly = true)
    public List<String> listAllToString() {
        return placeDao.allToString();
    }

    @Transactional
    public Place merge(Place place) {
        return placeDao.merge(place);
    }

    @Transactional
    public void remove(Long id) {
        placeDao.remove(id);
    }

    @Transactional
    public List<Place> listOnlyExisting() {
        return placeDao.listOnlyExisting();
    }

    @Transactional
    public List<String> listOnlyExistingToString() {
        return placeDao.listOnlyExistingToString();
    }

    @Transactional
    public Place findByPlaceName(String place) {
        return placeDao.findByPlaceName(place);
    }
}