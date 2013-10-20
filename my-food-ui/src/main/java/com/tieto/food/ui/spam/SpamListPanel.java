package com.tieto.food.ui.spam;

import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.tieto.food.domain.entity.Event;

@SuppressWarnings("all") 
public class SpamListPanel extends Panel {
    private static final long serialVersionUID = -7330174940880635021L;
    private final IModel<List<Event>> eventsModel;
    private FeedbackPanel feedbackPanel;

    public SpamListPanel(String id, IModel<List<Event>> model) {
        super(id);
        eventsModel = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(initFeedbackPanel("feedback", eventsModel));
        add(initSpamEventList("repeatingView", "eventItem", eventsModel));
    }

    private FeedbackPanel initFeedbackPanel(String wicketId,
            final IModel<List<Event>> eventsModel) {
        return new FeedbackPanel(wicketId) {
            private static final long serialVersionUID = 3201931978979266786L;

            @Override
            protected void onInitialize() {
                super.onInitialize();
                if (eventsModel.getObject().size() == 0) {
                    info("There are no threated events in the system");
                }
            }
        };
    }

    private ListView<Event> initSpamEventList(String wicketId,
            final String wicketItemId, IModel<List<Event>> eventsModel) {
        return new ListView<Event>(wicketId, eventsModel) {
            private static final long serialVersionUID = -2124920661210988388L;

            @Override
            protected void populateItem(ListItem<Event> item) {
                item.add(new SpamListItemPanel(wicketItemId, item
                        .getModelObject(), item.getIndex()));
            }
        };
    }

    public FeedbackPanel getFeedbackPanel() {
        return feedbackPanel;
    }

    public void setFeedbackPanel(FeedbackPanel feedbackPanel) {
        this.feedbackPanel = feedbackPanel;
    }
}