package com.tieto.food.ui.utils;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

import com.tieto.food.domain.entity.Event;

public class InfoPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public InfoPanel(String id, final Event event) {
        super(id);
        add(new Label("title", event.getTitle()));
        add(new Label("startTime", event.getEventDate()));
        add(new Label("description", event.getDescription()));        
    }
}
