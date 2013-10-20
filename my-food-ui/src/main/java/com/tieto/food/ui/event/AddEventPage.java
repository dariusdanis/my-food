package com.tieto.food.ui.event; // NOSONAR

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.datetime.DateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.joda.time.format.DateTimeFormatter;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.Place;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.EventService;
import com.tieto.food.domain.service.PlaceService;
import com.tieto.food.domain.service.TypeService;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.BasePage;
import com.tieto.food.ui.user.UserLoginPage;
import com.tieto.food.ui.utils.EmailClient;
import com.tieto.food.ui.utils.EventNotificationService;
import com.tieto.food.ui.utils.MyFoodSession;

@SuppressWarnings("all") 
public class AddEventPage extends BasePage {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private EventService eventService;

    @SpringBean
    private UserService userService;

    @SpringBean
    private PlaceService placeService;

    @SpringBean
    private TypeService typeService;
    
    private boolean closeInvitations;

    private String h3Lable = "Add event";
    private Form<?> form;
    private Event event;
    private String lng, lat, invitationContainerMarkupId;
    private WebMarkupContainer invitations;

    public AddEventPage() {
        this(null);
    }

    public AddEventPage(Long eventId) {
        if (eventId != null) {
            this.event = eventService.loadById(eventId);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (!isLoggedIn()) {
            setResponsePage(UserLoginPage.class);
            return;
        }

        FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);
        loadEntity();
        form = new Form<Void>("form");
        form.add(initTextField("inputTitle", "title"));
        form.add(initDateTextFieldFor("inputDate"));
        form.add(initTextArea("inputDescription", "description"));
        form.add(initTextField("placeSelector", "eventPlace.place"));
        form.add(initHiddenTextField("lng", "lng"));
        form.add(initHiddenTextField("lat", "lat"));
        form.add(initAddressTextField("placeAddress", "eventPlace.address"));
        form.add(initList("typeSelector", "eventType.type",
                typeService.listAllToString()));
        form.add(initSubbmitEventButton("add"));
        invitations = initializeInvitationContainer("invitationModal");
        form.add(invitations);
        form.add(initializeInvitationModal("showInvitationLink"));
        add(new Label("h3Lable", new PropertyModel<String>(this, "h3Lable")));
        add(form);

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (event.getEventPlace().getLatitude() == null) {
            response.render(OnDomReadyHeaderItem
                    .forScript("initialize('54.724196', '25.297195');"));
        } else {
            response.render(OnDomReadyHeaderItem.forScript("initialize('"
                    + event.getEventPlace().getLatitude() + "', '"
                    + event.getEventPlace().getLongitude() + "');"));
        }
    }

    private void loadEntity() {
        if (event == null) {
            event = new Event();
            event.setEventPlace(new Place());
        }
    }

    private AjaxFallbackLink<?> initializeInvitationModal(final String wicketId) {
        AjaxFallbackLink<?> button = new AjaxFallbackLink<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.appendJavaScript("$('#" + invitationContainerMarkupId
                        + "').modal('toggle')");
                if (closeInvitations) {
                    target.add(invitations);
                    closeInvitations = false;
                }
            }
        };

        return button;

    }

    private AjaxFallbackLink<?> initializeSaveButton(final String wicketId) {
        AjaxFallbackLink<?> button = new AjaxFallbackLink<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.appendJavaScript("$('#" + invitationContainerMarkupId
                        + "').modal('hide')");
            }
        };

        return button;

    }
    
    private AjaxFallbackLink<?> initializeCloseButton(final String wicketId) {
        AjaxFallbackLink<?> button = new AjaxFallbackLink<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.appendJavaScript("$('#" + invitationContainerMarkupId
                        + "').modal('hide')");
                closeInvitations = true;
            }
        };

        return button;

    }

    private WebMarkupContainer initializeInvitationContainer(String wicketId) {
        WebMarkupContainer invitationContainer = new WebMarkupContainer(
                wicketId);
        invitationContainer.setOutputMarkupId(true);
        invitationContainer.add(initList("userSelector", "users",
                userService.listOnlyExistingToString()));
        invitationContainer.add(initializeSaveButton("saveButton"));
        invitationContainer.add(initializeCloseButton("closeButton"));
        invitationContainerMarkupId = invitationContainer.getMarkupId();
        return invitationContainer;
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
        selector.add(new StringValidator(MIN_STRING_LENGTH, MAX_STRING_LENGTH));
        return selector;
    }

    private TextField<String> initTextField(String wicketId, String expression) {
        TextField<String> textField = new TextField<String>(wicketId,
                new PropertyModel<String>(event, expression));
        textField.add(new StringValidator(MIN_STRING_LENGTH,
                MAX_STRING_LENGTH * 2));
        return textField;

    }

    private TextField<String> initHiddenTextField(String wicketId,
            String expression) {
        TextField<String> textField = new TextField<String>(wicketId,
                new PropertyModel<String>(this, expression));
        textField.add(new StringValidator(MIN_STRING_LENGTH,
                MAX_STRING_LENGTH * 2));
        return textField;
    }

    private TextField<String> initAddressTextField(String wicketId,
            String expression) {
        TextField<String> textField = new TextField<String>(wicketId,
                new PropertyModel<String>(event, expression));
        textField.add(new StringValidator(MIN_STRING_LENGTH,
                MAX_STRING_LENGTH * 5));
        return textField;
    }

    private TextArea<String> initTextArea(String wicketId, String expression) {
        TextArea<String> textAria = new TextArea<String>(wicketId,
                new PropertyModel<String>(event, expression));
        textAria.add(new StringValidator(0, 255));
        return textAria;
    }

    /** Returns the date component */
    private DateTextField initDateTextFieldFor(String wicketId) {
        DateTextField dateTextField = new DateTextField(wicketId,
                new PropertyModel<Date>(event, "eventDate"), new DateConverter(
                        false) {
                    private static final long serialVersionUID = 1L;
                    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";

                    @Override
                    public String convertToString(Date date, Locale locale) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                DATE_PATTERN);
                        return dateFormat.format(date);
                    }

                    @Override
                    public Date convertToObject(String value, Locale locale) {
                        SimpleDateFormat sdf = new SimpleDateFormat(
                                DATE_PATTERN);
                        try {
                            return sdf.parse(value);
                        } catch (ParseException e) {
                            return null;
                        }
                    }

                    @Override
                    protected DateTimeFormatter getFormat(Locale locale) {
                        return null;
                    }

                    @Override
                    public String getDatePattern(Locale locale) {
                        return DATE_PATTERN;
                    }
                });

        dateTextField.setRequired(true);
        return dateTextField;
    }

    private Button initSubbmitEventButton(String wicketId) {
        return new Button(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                if (addType() && addPlace()) {
                    mergeEvent();
                    setResponsePage(ListEventPage.class);
                }

            }

            private void mergeEvent() {
                if (event.getCreatedBy() == null) {
                    User u = userService.loadById(MyFoodSession.get().getUser()
                            .getId());
                    event.setCreatedBy(u.getId());
                    event.setTimesReportedAsSpam(0L);
                    event = eventService.merge(event);
                    u.getEvents().add(event);
                    userService.merge(u);
                    new EventNotificationService().eventUpdated(event);
                    getSession().info("Event added - " + event.getTitle());
                    String users = getRequest().getRequestParameters()
                            .getParameterValue("invitationModal:userSelector")
                            .toString();
                    EmailClient.getInstanceOfEmailClient()
                            .validateAndSendInvitations(users, event);
                } else {
                    eventService.merge(event);
                    new EventNotificationService().eventUpdated(event);
                    getSession().info("Event updated - " + event.getTitle());
                }
            }

            private boolean addPlace() {
                try {
                    Place p = new Place();
                    p.setPlace(event.getEventPlace().getPlace());
                    p.setAddress(event.getEventPlace().getAddress());
                    p.setLatitude(Double.parseDouble(lat));
                    p.setLongitude(Double.parseDouble(lng));
                    event.setEventPlace(placeService.merge(p));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            private boolean addType() {
                String type = getRequest().getRequestParameters()
                        .getParameterValue("typeSelector").toString();
                try {
                    event.setEventType(typeService.listAll().get(
                            Integer.valueOf(type.split("([\")(\"])")[1])));
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
        };
    }

    protected void merge(Event event) {
        eventService.merge(event);
        setResponsePage(ListEventPage.class);
    }

    protected Component initDeleteButton(String wicketId) {
        return new Button(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                getSession().info("Event removed - " + event.getTitle());
                eventService.remove(event);
                setResponsePage(ListEventPage.class);
            }
        };
    }

    public String getH3Lable() {
        return h3Lable;
    }

    public void setH3Lable(String h3Lable) {
        this.h3Lable = h3Lable;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    public Form<?> getForm() {
        return form;
    }

    public void setForm(Form<?> form) {
        this.form = form;
    }

    public Event getEvent() {
        return event;
    }
}