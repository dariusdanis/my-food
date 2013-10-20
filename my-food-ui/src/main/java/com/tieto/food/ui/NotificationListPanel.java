package com.tieto.food.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.User;
import com.tieto.food.ui.event.EventPage;
import com.tieto.food.ui.utils.EventNotificationService;
import com.tieto.food.ui.utils.MyFoodSession;

@SuppressWarnings("all") 
public class NotificationListPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public NotificationListPanel(String id, boolean isAuthenticated) {
        super(id);
        if (isAuthenticated) {
            initializeNotificationPanel();
        } else {
            initializeMockPanel();
        }
    }

    private void initializeNotificationPanel() {
        final EventNotificationService notificationService = new EventNotificationService();
        List<Event> list = notificationService
                .getSubscribedEvents(MyFoodSession.get().getUser());
        final User currentUser = MyFoodSession.get().getUser();
        final Label countLabel;
        final WebMarkupContainer wmc = new WebMarkupContainer("markupContainer");
        wmc.setOutputMarkupId(true);
        if (notificationService.hasNotifications(currentUser)) {
            countLabel = new Label("countlbl", list.size());
        } else {
            countLabel = new Label("countlbl", "0");
        }

        final ListView<Event> listView = initListView(list);
        wmc.add(listView);
        listView.setVisible(false);
        listView.setOutputMarkupId(true);

        final AjaxLink<String> notificationLink = initializeAjaxLink(wmc,
                listView, countLabel);

        wmc.add(notificationLink);
        notificationLink.add(countLabel);
        add(wmc);

    }

    private AjaxLink<String> initializeAjaxLink(final WebMarkupContainer wmc,
            final ListView<Event> listView, final Label countLabel) {
        return new AjaxLink<String>("notificationLink") {
            private static final long serialVersionUID = 1L;
            private EventNotificationService notificationService = new EventNotificationService();

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(wmc);
                listView.setVisible(!listView.isVisible());
                countLabel.setDefaultModelObject(""
                        + notificationService.getSubscribedEvents(
                                MyFoodSession.get().getUser()).size());
                if (listView.isVisible()
                        && notificationService.hasNotifications(MyFoodSession
                                .get().getUser())) {
                    listView.setDefaultModelObject(notificationService
                            .getSubscribedEvents(MyFoodSession.get().getUser()));
                }
            }
        };
    }

    private ListView<Event> initListView(List<Event> list) {
        return new ListView<Event>("notificationList", list) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Event> item) {
                final Event event = (Event) item.getModelObject();
                Link<?> linkToEvent = new Link<Object>("eventLink") {
                    private static final long serialVersionUID = 1L;
                    private EventNotificationService notificationService = new EventNotificationService();

                    @Override
                    public void onClick() {
                        notificationService.removeNotification(MyFoodSession
                                .get().getUser(), event);
                        setResponsePage(EventPage.class,
                                EventPage.parametersWith(event.getEventId()));
                    }
                };
                linkToEvent.add(new Label("eventTitle", "New Event -  "
                        + event.getTitle()));
                item.add(linkToEvent);
            }
        };
    }

    private void initializeMockPanel() {
        ListView<Event> listView = new ListView<Event>("notificationList",
                new ArrayList<Event>()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Event> item) {
            }
        };
        final Link<String> notificationBtnLabel = new Link<String>(
                "notificationButtonlabel") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {

            }
        };
        WebMarkupContainer wmc = new WebMarkupContainer("markupContainer");
        Link<Object> linkToEvent = new Link<Object>("eventLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {

            }

        };
        linkToEvent.add(new Label("eventTitle", ""));

        wmc.add(listView);
        wmc.add(notificationBtnLabel);
        notificationBtnLabel.add(new Label("testLbl", ""));
        wmc.setVisible(false);
        add(wmc);

    }
}