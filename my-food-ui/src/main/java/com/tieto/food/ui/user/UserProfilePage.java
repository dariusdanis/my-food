package com.tieto.food.ui.user;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.EventService;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.BasePage;
import com.tieto.food.ui.PageNotFound;
import com.tieto.food.ui.event.EventsListPanel;
import com.tieto.food.ui.event.ListEventPage;
import com.tieto.food.ui.utils.MyFoodSession;

@SuppressWarnings("all") 
public class UserProfilePage extends BasePage {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private UserService userService;
    @SpringBean
    private EventService eventService;

    public static final String USER_ID = "userId";
    private User user;

    public static PageParameters parametersWith(Long userId) {
        return new PageParameters().add(USER_ID, userId);
    }

    public UserProfilePage(PageParameters params) {
        user = userService.loadById(params.get(USER_ID).toLong());
        if (user == null) {
            setResponsePage(PageNotFound.class);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (!isLoggedIn()) {
            setResponsePage(UserLoginPage.class);
            return;
        } else if (!user.isOrExist()) {
            setResponsePage(ListEventPage.class);
            return;
        }
        add(new Label("headerNameLabel", user.getName() + " "
                + user.getSurname()));
        add(new Label("name", user.getName()));
        add(new Label("surname", user.getSurname()));
        add(new Label("email", user.getEmail()));
        Link<?> edit = initializeEditLink("edit");

        if (!(user.getId() == MyFoodSession.get().getUser().getId() || MyFoodSession
                .get().getUser().isAdmin())) {
            edit.setVisible(false);
        }
        add(edit);
        add(new EventsListPanel("createdEventsPanel",
                initEventListModel(eventService.listAllWhereCreatedBy(user
                        .getId()))));
        add(new EventsListPanel("participatedEventsPanel",
                initEventListModel(eventService.listAllWithParticipant(user
                        .getId()))));
    }

    private IModel<List<Event>> initEventListModel(final List<Event> eventList) {
        return new LoadableDetachableModel<List<Event>>() {
            private static final long serialVersionUID = 7973708256192453464L;

            @Override
            protected List<Event> load() {
                return eventList;
            }
        };
    }

    private Link<?> initializeEditLink(String wicketId) {
        return new Link<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(EditUserInfoPage.class,
                        EditUserInfoPage.parametersWith(user.getId()));
            }
        };
    }
}