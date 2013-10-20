package com.tieto.food.ui.spam;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.service.EventService;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.event.EventPage;
import com.tieto.food.ui.utils.EmailClient;

@SuppressWarnings("all") 
public class SpamListItemPanel extends Panel {
    private static final long serialVersionUID = 7395494076129901863L;
    private int numberInList;
    private Event event;

    @SpringBean
    private EventService eventService;

    @SpringBean
    private UserService userService;

    public SpamListItemPanel(String id, Event event, int numInList) {
        super(id);
        this.event = event;
        this.numberInList = numInList + 1;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        if (eventIsSpam()) {
            add(new AttributeAppender("class", "error"));
        }

        add(new Label("eventNo", numberInList));
        add(initTitleLinking("eventTitleA", "eventTitleSpan"));
        add(new Label("eventAuthor", getAuthorString()));
        add(new Label("eventDate", event.getEventDate()));
        add(new Label("eventThreats", getThreatAmount()));
        add(initActionButton("eventActionA", "eventActionSpan"));
    }

    private boolean eventIsSpam() {
        return event.isSpam();
    }

    private String getAuthorString() {
        String email = userService.loadById(event.getCreatedBy()).getEmail();
        String name = userService.loadById(event.getCreatedBy()).getName();
        return name + " (" + email + ")";
    }

    private String getThreatAmount() {
        long reportAmount = event.getTimesReportedAsSpam();
        return Integer.toString((int) reportAmount);
    }

    private void performEventBlocking() {
        event.setSpam(true);
        eventService.merge(event);
        EmailClient.getInstanceOfEmailClient().sendEventBlockedEmail(
                userService.loadById(event.getCreatedBy()), event);
    }

    private void performEventAllowing() {
        event.setSpam(false);
        eventService.merge(event);
        EmailClient.getInstanceOfEmailClient().sendEventAllowedEmail(
                userService.loadById(event.getCreatedBy()), event);
    }

    @SuppressWarnings({ "rawtypes", "serial" })
    private Link initTitleLinking(String wicketId, final String wicketLabelId) {
        return new Link(wicketId) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(new Label(wicketLabelId, getModifiedTitle()));
            }

            private String getModifiedTitle() {
                return (eventIsSpam()
                    ? event.getTitle() + " (blocked)"
                    : event.getTitle());
            }

            @Override
            public void onClick() {
                setResponsePage(EventPage.class,
                        EventPage.parametersWith(event.getEventId()));
            }
        };
    }

    @SuppressWarnings("all") 
    private Link initActionButton(String wicketId, final String wicketLabelId) {
        return new Link(wicketId) {
            private String initLabelText() {
                return (eventIsSpam() ? "allow" : "block");
            }

            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(new Label(wicketLabelId, initLabelText()));
                add(new AttributeModifier("class",
                        (eventIsSpam() ? "btn-success btn-small"
                                : "btn-danger btn-small")));
            }

            @Override
            public void onClick() {
                if (eventIsSpam())
                    performEventAllowing();
                else
                    performEventBlocking();
            }
        };
    }
}