package com.tieto.food.domain.dao;

import java.util.List;

import com.tieto.food.domain.entity.Place;

public interface PlaceDao {
    List<Place> listAll();

    List<Place> listOnlyExisting();

    List<String> listOnlyExistingToString();

    List<String> allToString();

    Place merge(Place place);

    void remove(Long id);

    Place findByPlaceName(String place);
}