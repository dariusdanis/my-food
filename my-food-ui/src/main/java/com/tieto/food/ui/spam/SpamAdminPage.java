package com.tieto.food.ui.spam;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.service.EventService;
import com.tieto.food.ui.BasePage;
import com.tieto.food.ui.event.ListEventPage;
import com.tieto.food.ui.utils.MyFoodSession;

@SuppressWarnings("all") 
public class SpamAdminPage extends BasePage {
    private static final long serialVersionUID = -7705103329522480147L;

    @SpringBean
    private EventService eventService;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (MyFoodSession.get().getUser() == null
                || !MyFoodSession.get().getUser().isAdmin()) {
            setResponsePage(ListEventPage.class);
            return;
        }
        add(initInfoMessage("infoMessage"));
        add(new SpamListPanel("threatEventsListPanel", initEventListModel()));
    }

    public Label initInfoMessage(String wicketId) {
        Label infoMessage;
        if (!getSession().getFeedbackMessages().isEmpty()) {
            infoMessage = new Label(wicketId, getSession()
                    .getFeedbackMessages().iterator().next().getMessage()
                    .toString());
            getSession().getFeedbackMessages().clear();
        } else {
            infoMessage = new Label(wicketId, "");
            infoMessage.setVisible(false);
        }
        return infoMessage;
    }

    private IModel<List<Event>> initEventListModel() {
        return new LoadableDetachableModel<List<Event>>() {
            private static final long serialVersionUID = 5485640712319836631L;

            @Override
            protected List<Event> load() {
                return eventService.listAllThreatedEventsInDescendingOrder();
            }
        };
    }
}