package com.tieto.food.ui.event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.StringValidator;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.EventService;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.user.UserLoginPage;
import com.tieto.food.ui.user.UserProfilePage;
import com.tieto.food.ui.utils.EmailClient;
import com.tieto.food.ui.utils.MyFoodSession;

@SuppressWarnings("all")
public class EventsListItemPanel extends Panel {
    private static final long serialVersionUID = 8409450871656106316L;
    private static final String STATE_JOIN = "Join!";
    private static final String STATE_LEAVE = "Leave";

    @SpringBean
    private UserService userService;

    @SpringBean
    private EventService eventService;

    private List<User> users = new ArrayList<User>();
    private String participantsConteinerMarkupId;
    private WebMarkupContainer showItems;
    private WebMarkupContainer showParticipants;
    private WebMarkupContainer showReportConfirmation;
    private String state = STATE_JOIN;
    private Event event;
    private WebMarkupContainer invitationContainer;

    // DEMO fix ---
    private boolean add;
    private boolean remove;

    // ------------

    public EventsListItemPanel(String id, Event event) {
        super(id);
        this.event = event;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(initTitleLink("eventTitleA"));
        add(initDate("eventDate"));

        @SuppressWarnings("unchecked")
        Link<Object> link = (Link<Object>) initializeProfileLink("profileLink",
                event.getCreatedBy()).add(initCreatedByLabel("eventUsername"));
        if (!userService.loadById(event.getCreatedBy()).isOrExist()) {
            link.setEnabled(false);
        }
        add(link);
        add(new Label("eventPlace", event.getEventPlace().getPlace()));
        add(new Label("eventType", event.getEventType().getType()));
        add(new Label("eventDescription", event.getDescription()));
        add(initParticipantsShowingContainer("showItems"));
        add(new ListParticipantsContainer("addItems"));
        add(new ReportConfirmationShowingContainer("showReportEventAsSpam"));
        initSessionStateIsJoinOrLeave();
        add(initJoinLeaveShowingForm("joinLeaveForm", "joinLeaveButton",
                "label"));
        add(initializeEditButton("eventEditButton"));
        Form<?> form = new Form<Void>("form");
        form.add(initializeInvitationContainer("invitationModal"));
        form.add(initializeInvitationModal("showInvitationLink"));
        add(form);
        add(initializeReportingContainer("reportingContainer", "reportingLink",
                "reportingModal"));
    }

    @SuppressWarnings("serial")
    private class ReportConfirmationShowingContainer extends WebMarkupContainer {
        @SuppressWarnings("rawtypes")
        public ReportConfirmationShowingContainer(String id) {
            super(id);
            this.setOutputMarkupId(true);
            add(new Link("submitSpam") {
                @Override
                public void onClick() {
                    setEventAsReportedAndIncrementThreat();
                }
            }).setOutputMarkupId(true);
            showReportConfirmation = this;
        }
    }

    private void setEventAsReportedAndIncrementThreat() {
        event.setTimesReportedAsSpam(event.getTimesReportedAsSpam() + 1);
        event.getUsersWhoReported().add(MyFoodSession.get().getUser());
        eventService.merge(event);
    }

    private WebMarkupContainer initializeReportingContainer(String wicketId,
            String wicketLinkId, String wicketModalId) {
        WebMarkupContainer reportingContainer = new WebMarkupContainer(wicketId);
        reportingContainer.setOutputMarkupId(true);
        User currentUser = MyFoodSession.get().getUser();
        Link<String> reportingLink = initReportingLink(wicketLinkId);
        Component reportingModal = new Label(wicketModalId,
                "<i class=\"icon-minus-sign\"></i> Reported")
                .setEscapeModelStrings(false);
        if (event.isSpam()) {
            reportingModal.setVisible(false);
        }
        if (currentUser == null) {
            reportingContainer.setVisible(false);
        } else {

            if (event.getUsersWhoReported().contains(currentUser)) {
                reportingLink.setVisible(false);
                reportingModal.setVisible(true);
                reportingContainer.setVisible(true);
            } else {
                reportingLink.setVisible(true);
                reportingModal.setVisible(false);
                reportingContainer.setVisible(true);
            }
        }

        reportingContainer.add(reportingLink);
        reportingContainer.add(reportingModal);
        return reportingContainer;
    }

    private AjaxFallbackLink<String> initReportingLink(String wicketId) {
        return new AjaxFallbackLink<String>(wicketId) {
            private static final long serialVersionUID = 8837432573787831059L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(showReportConfirmation);
                target.appendJavaScript("$('#"
                        + showReportConfirmation.getMarkupId() + "')"
                        + ".modal('toggle')");
            }
        };
    }

    @SuppressWarnings("rawtypes")
    private Link initTitleLink(String wicketId) {
        return new Link(wicketId) {
            private static final long serialVersionUID = 8837432573787831059L;

            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(new Label("eventTitle", getTitleIfBlockedOrNot()));
            }

            private String getTitleIfBlockedOrNot() {
                if (event.isSpam()) {
                    return event.getTitle() + " *BLOCKED*";
                } else {
                    return event.getTitle();
                }
            }

            @Override
            public void onClick() {
                setResponsePage(EventPage.class,
                        EventPage.parametersWith(event.getEventId()));
            }
        };
    }

    private AjaxFallbackLink<?> initializeInvitationModal(final String wicketId) {
        AjaxFallbackLink<?> button = new AjaxFallbackLink<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.appendJavaScript("$('#"
                        + invitationContainer.getMarkupId()
                        + "').modal('toggle')");
                target.add(invitationContainer);
            }
        };
        User user = MyFoodSession.get().getUser();
        button.setVisible(!event.isSpam()
                && (MyFoodSession.get().isAuthenticated() && (user.isAdmin() || event
                        .getCreatedBy() == user.getUserId())));
        return button;
    }

    private Button initializeSaveButton(final String wicketId) {
        Button button = new Button(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                String emails = getRequest().getRequestParameters()
                        .getParameterValue("invitationModal:userSelector")
                        .toString();
                if (EmailClient.getInstanceOfEmailClient()
                        .validateAndSendInvitations(emails, event)) {
                    getSession().info("Invitations sent");
                } else {
                    getSession().error("Please enter only valid emails");
                }
                setResponsePage(ListEventPage.class);
            }
        };
        return button;

    }

    private WebMarkupContainer initializeInvitationContainer(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);
        container.setOutputMarkupId(true);
        container.add(initList("userSelector", "users",
                userService.listOnlyExistingToString()));
        container.add(initializeSaveButton("saveButton"));
        invitationContainer = container;
        return container;
    }

    private ListChoice<String> initList(String wicketId, String expression,
            List<String> list) {
        ListChoice<String> selector = new ListChoice<String>(wicketId,
                new PropertyModel<String>(event, expression), list) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {
                return "";
            }

        };
        selector.add(new StringValidator(1, 15));
        selector.setOutputMarkupId(true);

        selector.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 2604279030812705608L;

            protected void onUpdate(AjaxRequestTarget target) {
            }
        });

        return selector;
    }

    private Form<?> initJoinLeaveShowingForm(String wicketIdOfForm,
            String wicketIdOfButton, String wicketIdLabel) {
        Form<?> joinLeaveForm = new Form<Void>(wicketIdOfForm);
        AjaxFormValidatingBehavior.addToAllFormComponents(joinLeaveForm,
                "onkeyup", Duration.ONE_SECOND);

        PropertyModel<String> model = new PropertyModel<String>(this, "state");
        AjaxButton button = intializeJoinLeaveButton(wicketIdOfButton, model);
        button.add(new Label(wicketIdLabel, model));
        initJoinLeaveButtonColor(button);
        joinLeaveForm.add(button);
        return joinLeaveForm;
    }

    private Label initCreatedByLabel(String wicketId) {
        User u = userService.loadById(event.getCreatedBy());
        return new Label(wicketId, u.getName() + " " + u.getSurname());
    }

    private void initJoinLeaveButtonColor(AjaxButton button) {
        if (state.equals(STATE_LEAVE)) {
            makeJoinLeaveButtonRed(button);
        } else {
            makeJoinLeaveButtonGreen(button);
        }
    }

    private void makeJoinLeaveButtonGreen(AjaxButton button) {
        button.add(new AttributeModifier("class", "btn btn-small btn-success"));
    }

    private void makeJoinLeaveButtonRed(AjaxButton button) {
        button.add(new AttributeModifier("class", "btn btn-small btn-danger"));
    }

    private void initSessionStateIsJoinOrLeave() {
        try {
            List<Event> events = userService.loadById(
                    MyFoodSession.get().getUser().getId()).getEvents();
            for (Event e : events) {
                if (e.getEventId() == event.getEventId()) {
                    setState(STATE_LEAVE);
                    return;
                }
            }
        } catch (Exception e) {
            setState(STATE_JOIN);
        }
    }

    private WebMarkupContainer initParticipantsShowingContainer(String wicketId) {
        WebMarkupContainer participantsShowingContainer = new WebMarkupContainer(
                "showParticipants");
        participantsShowingContainer.setOutputMarkupId(true);
        setParticipantsConteinerMarkupId(participantsShowingContainer
                .getMarkupId());
        showItems = new ItemsContainer(wicketId);
        showItems.setOutputMarkupId(true);
        participantsShowingContainer.add(showItems);
        return participantsShowingContainer;
    }

    private Label initDate(String wicketId) {
        SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm");
        String dateToString = dateformatyyyyMMdd.format(event.getEventDate());
        return new Label(wicketId, dateToString);
    }

    public Link<Object> initializeProfileLink(String wicketId, final Long userId) {
        return new Link<Object>(wicketId) {
            private static final long serialVersionUID = 8837432573787831051L;

            @Override
            public void onClick() {
                setResponsePage(UserProfilePage.class,
                        UserProfilePage.parametersWith(userId));
            }
        };
    }

    public Link<Object> initializeEditButton(String wicketId) {
        Link<Object> link = new Link<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(EditEventPage.class,
                        EditEventPage.parametersWith(event.getEventId()));
            }
        };

        if (MyFoodSession.get().getUser() == null
                || event.getEventDate().before(new Date())) {
            link.setVisible(false);
            return link;
        }

        if (MyFoodSession.get().getUser().isAdmin()
                || MyFoodSession.get().getUser().getId() == event
                        .getCreatedBy()) {
            return link;
        }
        link.setVisible(false);
        return link;
    }

    public AjaxButton intializeJoinLeaveButton(String wicketId,
            PropertyModel<String> model) {
        AjaxButton ab = new AjaxButton(wicketId, model) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (!MyFoodSession.get().isAuthenticated()) {
                    MyFoodSession.get().setCachedPage(EventPage.class,
                            EventPage.parametersWith(event.getEventId()));
                    setResponsePage(UserLoginPage.class);
                    return;
                }
                EmailClient emailClient = EmailClient
                        .getInstanceOfEmailClient();
                if (state.equals(STATE_JOIN)) {
                    setState(STATE_LEAVE);
                    User u = userService.loadById(MyFoodSession.get().getUser()
                            .getId());
                    u.getEvents().add(event);
                    userService.merge(u);
                    makeJoinLeaveButtonRed(this);
                    target.add(this);
                    target.add(showParticipants);
                    u = userService.loadById(event.getCreatedBy());
                    if (u.isSendJoinLeaveNotifications()) {
                        emailClient.sendNotificationAboutUserEventEmail(event,
                                MyFoodSession.get().getUser(), 1);
                    }
                    remove = false;
                    add = true;
                } else {
                    setState(STATE_JOIN);
                    User u = userService.loadById(MyFoodSession.get().getUser()
                            .getId());

                    for (int i = 0; i < u.getEvents().size(); i++) {
                        if (event.getEventId() == u.getEvents().get(i)
                                .getEventId()) {
                            u.getEvents().remove(i);
                            userService.merge(u);
                            makeJoinLeaveButtonGreen(this);
                            target.add(this);
                            target.add(showParticipants);
                            if (userService.loadById(event.getCreatedBy())
                                    .isSendJoinLeaveNotifications()) {
                                emailClient
                                        .sendNotificationAboutUserEventEmail(
                                                event, MyFoodSession.get()
                                                        .getUser(), 0);
                            }
                            remove = true;
                            add = false;
                            return;
                        }
                    }

                }

            }
        };

        if (MyFoodSession.get().getUser() == null) {
            ab.setVisible(true);
            return ab;
        } else if (MyFoodSession.get().getUser().getId() == event
                .getCreatedBy()
                || event.getEventDate().before(new Date())
                || event.isSpam()) {
            ab.setVisible(false);
        }
        return ab;
    }

    public class ItemsContainer extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public ItemsContainer(String id) {
            super(id);
            setOutputMarkupId(true);
            add(new ListView<User>("item", users) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void populateItem(ListItem<User> item) {
                    item.add(initializeRemoveParticipantButton("removeButton",
                            item.getModelObject().getUserId()));
                    item.add(initializeProfileLink("text",
                            item.getModelObject().getUserId())
                            .add(new Label("name", item.getModelObject()
                                    .getName()
                                    + " "
                                    + item.getModelObject().getSurname())));
                }
            });
        }
    }

    public Link<Object> initializeRemoveParticipantButton(String wicketId,
            final Long userId) {
        Link<Object> button = new Link<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                User participant = userService.loadById(userId);
                for (int i = 0; i < participant.getEvents().size(); i++) {
                    if (event.getEventId() == participant.getEvents().get(i)
                            .getEventId()) {
                        participant.getEvents().remove(i);
                        userService.merge(participant);
                        EmailClient.getInstanceOfEmailClient()
                                .sendRemoveParticipantEmail(participant, event);
                        reloadUserList();
                        getSession().info(
                                participant.getName() + " "
                                        + participant.getSurname()
                                        + " removed from event");
                        break;

                    }
                }
                setResponsePage(ListEventPage.class);
            }
        };
        button.setVisible(checkRemoveButtonVisibility(userId));
        return button;
    }

    public boolean checkRemoveButtonVisibility(Long userId) {
        User participant = userService.loadById(userId);
        User user = MyFoodSession.get().getUser();
        return (MyFoodSession.get().isAuthenticated()
                && (user.isAdmin() || event.getCreatedBy() == user.getUserId())
                && participant.getUserId() != event.getCreatedBy() && participant
                    .getUserId() != user.getUserId());
    }

    public class ListParticipantsContainer extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        private Model<String> getParticipantAmountModel() {
            return new Model<String>() {
                private static final long serialVersionUID = -512004728416041654L;

                public String getObject() {
                    Long count = userService.getPaticipantCount(event
                            .getEventId());
                    if (add) {
                        count++;
                        add = false;
                    }
                    if (remove) {
                        count--;
                        remove = false;
                    }
                    return "Participants (" + count + ")";

                }
            };
        }

        public ListParticipantsContainer(String id) {
            super(id);
            setOutputMarkupId(true);
            ShowParticipantsLink showParticipantsLink = new ShowParticipantsLink(
                    "showParticipantsLink");
            Form<?> participantsForm = new Form<Void>("showParticipantsForm");
            Button participantsButton = new Button("participantsButton");
            participantsButton.add(new Label("participantsLabel",
                    getParticipantAmountModel()));
            showParticipantsLink.add(participantsButton);
            participantsForm.add(showParticipantsLink);
            add(participantsForm);
            if (event.isSpam()) {
                participantsForm.setVisible(false);
            }
            showParticipants = this;
        }

        private final class ShowParticipantsLink extends
                AjaxFallbackLink<Object> {
            private static final long serialVersionUID = -57201384326299126L;

            private ShowParticipantsLink(String id) {
                super(id);
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                reloadUserList();
                target.appendJavaScript("$('#" + participantsConteinerMarkupId
                        + "').modal('toggle')");
                target.add(showItems);
                target.add(showParticipants);
            }
        }
    }

    private void reloadUserList() {
        users.clear();
        List<User> usersList = userService.listByEventId(event.getEventId());
        for (User u : usersList) {
            if (u.isOrExist()) {
                users.add(u);
            }
        }
    }

    public Event getEvent() {
        return event;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setParticipantsConteinerMarkupId(
            String participantsConteinerMarkupId) {
        this.participantsConteinerMarkupId = participantsConteinerMarkupId;
    }
}