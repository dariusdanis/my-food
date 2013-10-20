package com.tieto.food.ui.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.gmap.GMap;
import org.wicketstuff.gmap.api.GClientGeocoder;
import org.wicketstuff.gmap.api.GEvent;
import org.wicketstuff.gmap.api.GEventHandler;
import org.wicketstuff.gmap.api.GInfoWindow;
import org.wicketstuff.gmap.api.GLatLng;
import org.wicketstuff.gmap.api.GMarker;
import org.wicketstuff.gmap.api.GMarkerOptions;
import org.wicketstuff.gmap.event.DragEndListener;
import org.wicketstuff.gmap.event.LoadListener;
import org.wicketstuff.gmap.event.ZoomChangedListener;
import org.wicketstuff.gmap.geocoder.GeocoderStatus;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.service.EventService;
import com.tieto.food.ui.BasePage;

@SuppressWarnings("all") 
public class EventMapPage extends BasePage {

    private static final long serialVersionUID = 1L;
    
    private static final double SW_LAT = 54.69930648803711;
    private static final double SW_LNG = 25.255138397216797;
    private static final double NE_LAT = 54.74887466430664;
    private static final double NE_LNG = 25.34096908569336;

    @SpringBean
    private EventService eventService;
    private GMap map;
    private EventsListPanel eventList;

    public EventMapPage() {

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        initializeEventMap();
        add(map);
        eventList = new EventsListPanel("eventsListPanel", initEventListModel());
        eventList.setOutputMarkupId(true);
        add(eventList);
    }

    private IModel<List<Event>> initEventListModel() {
        return new LoadableDetachableModel<List<Event>>() {
            private static final long serialVersionUID = 7973708256192453464L;

            @Override
            protected List<Event> load() {
                ArrayList<Event> events = new ArrayList<Event>();
                for (Event event : eventService.listValidFutureEvents()) {
                    if (inBounds(event.getEventPlace().getLatitude(), event
                            .getEventPlace().getLongitude())) {
                        events.add(event);
                    }
                }
                return events;
            }

        };
    }
    
    private boolean inBounds(Double lat, Double lng) {
        if (lat == null || lng == null) {
            return false;
        }
        // Happens when the map loads for the first time
        if (map.getBounds() == null) {
            return (lat > SW_LAT
                    && lat < NE_LAT
                    && lng > SW_LNG
                    && lng < NE_LNG); 
        }
        
        return (lat > map.getBounds().getSW().getLat()
                && lat < map.getBounds().getNE().getLat()
                && lng > map.getBounds().getSW().getLng()
                && lng < map.getBounds().getNE().getLng());
    }
    
    private void initializeEventMap() {
        // Tieto stogas 54,724196, 25,297195
        map = new GMap("map");
        map.setCenter(new GLatLng(54.724196, 25.297195));
        map.setStreetViewControlEnabled(true);
        map.setScaleControlEnabled(true);
        map.setScrollWheelZoomEnabled(true);
        map.setCenter(new GLatLng(54.724196, 25.297195));
        populateMap();
        map.setOutputMarkupId(true);
        map.add(new LoadListener() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onLoad(AjaxRequestTarget target) {
                eventList.setDefaultModel(initEventListModel());
                target.add(eventList);
            }
        });

        map.add(new ZoomChangedListener() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onZoomChanged(AjaxRequestTarget target) {
                eventList.setDefaultModel(initEventListModel());
                target.add(eventList);
            }
        });

        map.add(new DragEndListener() {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onDragEnd(AjaxRequestTarget target) {
                eventList.setDefaultModel(initEventListModel());
                target.add(eventList);
            }

        });
    }

    private void populateMap() {
        for (final Event event : eventService.listValidFutureEvents()) {
            final GLatLng latLng = new GLatLng(event.getEventPlace()
                    .getLatitude(), event.getEventPlace().getLongitude());
            GMarker marker = new GMarker(new GMarkerOptions(map, latLng));

            marker.addListener(GEvent.click, new GEventHandler() {
                private static final long serialVersionUID = 3285576853400529192L;
                private String infoWidnowText = "Event - " + event.getTitle()
                        + "<br/>Start date - " + event.getEventDate()
                        + "<br/>Where? - " + event.getEventPlace().getPlace();

                @Override
                public void onEvent(AjaxRequestTarget target) {
                    GInfoWindow tmpInfoWindow = new GInfoWindow(latLng,
                            infoWidnowText);
                    map.addOverlay(tmpInfoWindow);
                }

            });

            map.addOverlay(marker);
        }
    }

}