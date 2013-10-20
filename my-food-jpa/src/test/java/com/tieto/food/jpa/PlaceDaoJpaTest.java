package com.tieto.food.jpa;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.entity.Place;
import com.tieto.food.jpa.PlaceDaoJpa;

@Transactional
public class PlaceDaoJpaTest extends DaoJpaTestBase {
    private Place place;
    private List<Place> initialPlaceList;
    private static final String MOCK_PLACE_NAME = "Cili kaimas";
    
    @Autowired
    private PlaceDaoJpa placeDaoJpa;

    @Before
    public void initAPlace() {
        initialPlaceList = placeDaoJpa.listAll();
        place = new Place();
        place.setOrExist(true);
        place.setPlace(MOCK_PLACE_NAME);
        place.setLatitude(13.25d);
        place.setLongitude(24.9d);
        place.setAddress("test address");
        place = placeDaoJpa.merge(place);
    }
    
    @Test
    public void testAllToString() {
        assertTrue(placeDaoJpa.allToString().contains(MOCK_PLACE_NAME));
    }
    
    @Test
    public void testSavesListsAndRemovesPlaces() {
        placeDaoJpa.remove(place.getPlaceId());
        assertTrue(!place.isOrExist());
        placeDaoJpa.remove(place.getPlaceId());
        assertFalse(place.isOrExist());
    }

    @Test
    public void testFindByName() {
        Place foundPlace = placeDaoJpa.findByPlaceName(MOCK_PLACE_NAME);
        assertTrue(foundPlace.equals(place));
    }
    
    @Test
    public void testOnlyExistingToString() {
        boolean placeContainsTheList = false;
        for (String placeNames : placeDaoJpa.listOnlyExistingToString()) {
            if (placeNames.equals(place.getPlace())) {
                placeContainsTheList = true;
                break;
            }
        }
        assertTrue(placeContainsTheList);
    }
    
    @Test
    public void testListOnlyExisting() {
        initialPlaceList.add(place);
        List<Place> newPlaceListAfterMerge = placeDaoJpa.listAll();
        
        boolean allAreSame = true;
        for (Place newPlacesItem : newPlaceListAfterMerge) {
            if (!initialPlaceList.contains(newPlacesItem)) {
                allAreSame = false;
                break;
            }
        }
        assertTrue(allAreSame);
    }
}
