package com.tieto.food.ui.event;

import java.util.Date;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.ui.user.UserLoginPage;

@SuppressWarnings("all") 
public class EditEventPage extends AddEventPage {
    private static final long serialVersionUID = 1L;
    public static final String EVENT_ID = "eventId";

    public static PageParameters parametersWith(Long eventId) {
        return new PageParameters().add(EVENT_ID, eventId);
    }

    public EditEventPage(PageParameters params) {
        super(params.get(EVENT_ID).toLong());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (!isAuthenticatedToAcces(getEvent())
                || getEvent().getEventDate().before(new Date())) {
            setResponsePage(ListEventPage.class);
            return;
        }

        if (!isLoggedIn()) {
            setResponsePage(UserLoginPage.class);
            return;
        }
        get("form:showInvitationLink").setVisible(false);
        get("form:invitationModal").setVisible(false);
        setH3Lable("Edit event");
        getForm().add(super.initDeleteButton("delete"));
    }

    @Override
    protected void merge(Event event) {
        getEventService().merge(event);
        setResponsePage(ListEventPage.class);
    }
}
