package com.tieto.food.ui.event;

import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.tieto.food.domain.entity.Event;

@SuppressWarnings("all") 
public class EventsListPanel extends Panel {
    private static final long serialVersionUID = -7932351604422967303L;
    private final IModel<List<Event>> eventsModel;

    public EventsListPanel(String id, IModel<List<Event>> eventsModel) {
        super(id);
        this.eventsModel = eventsModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(initEventList("repeatingView", eventsModel));
    }

    private ListView<Event> initEventList(String wicketId,
            IModel<List<Event>> eventsModel) {
        return new ListView<Event>(wicketId, eventsModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Event> item) {
                item.add(new EventsListItemPanel("eventItem", item
                        .getModelObject()));
            }
        };
    }
}