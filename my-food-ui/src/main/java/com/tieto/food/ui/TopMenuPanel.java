package com.tieto.food.ui;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

import com.tieto.food.ui.charts.SiteStatistics;
import com.tieto.food.ui.event.AddEventPage;
import com.tieto.food.ui.event.EventMapPage;
import com.tieto.food.ui.event.ListEventPage;
import com.tieto.food.ui.spam.SpamAdminPage;
import com.tieto.food.ui.user.UserProfilePage;
import com.tieto.food.ui.utils.MyFoodSession;

@SuppressWarnings("all") 
public class TopMenuPanel extends Panel {
    private static final long serialVersionUID = 5856704068908376948L;

    public TopMenuPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        boolean isAuthenticated = MyFoodSession.get().isAuthenticated();

        add(initUserNameLable("username", isAuthenticated));
        add(initProfileLink("profile", isAuthenticated));
        add(initLogOutLink("logOut", isAuthenticated));
        add(initMapLink("map", isAuthenticated));
        add(initStatisticLink("statistic", isAuthenticated));
        add(initButton("login", !isAuthenticated));
        add(initButton("add", isAuthenticated));
        add(initManageSpamLink("manageSpam"));
        add(initHomeLink("home"));
        add(new NotificationListPanel("notifications", isAuthenticated));
        add(initAdminStarLabel("adminStarIcon", isAuthenticated));
    }

    private Label initAdminStarLabel(String wicketId, boolean isAuthenticated) {
        Label lbl = new Label(wicketId, "");
        if (!(isAuthenticated && MyFoodSession.get().getUser().isAdmin())) {
            lbl.setVisible(false);
        }
        return lbl;
    }

    private Label initUserNameLable(String wicketId, boolean isAuthenticated) {
        Label userLabel;
        if (isAuthenticated) {
            userLabel = new Label(wicketId, MyFoodSession.get().getUser()
                    .getName());
        } else {
            userLabel = new Label(wicketId, "");
        }
        return userLabel;
    }

    private Link<Object> initButton(String wicketId, boolean isAuthenticated) {
        Link<Object> addEventButton = new Link<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(AddEventPage.class);
            }
        };
        addEventButton.setVisible(isAuthenticated);
        return addEventButton;
    }

    private Link<Object> initMapLink(String wicketId, boolean isAuthenticated) {
        Link<Object> mapLink = new Link<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(EventMapPage.class);
            }
        };
        
        mapLink.setVisible(isAuthenticated);
        return mapLink;
    }

    private Link<Object> initStatisticLink(String wicketId,
            boolean isAuthenticated) {
        Link<Object> statisticLink = new Link<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(SiteStatistics.class);
            }
        };
        
        statisticLink.setVisible(isAuthenticated);
        return statisticLink;
    }

    private Link<Object> initHomeLink(String wicketId) {
        return new Link<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(ListEventPage.class);
            }
        };
    }

    private Link<Object> initProfileLink(String wicketId,
            boolean isAuthenticated) {
        Link<Object> profileLink = new Link<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(
                        UserProfilePage.class,
                        UserProfilePage.parametersWith(MyFoodSession.get()
                                .getUser().getId()));
            }
        };
        profileLink.setVisible(isAuthenticated);
        return profileLink;
    }

    private Link<Object> initLogOutLink(String wicketId, boolean isAuthenticated) {
        Link<Object> logOutLink = new Link<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                MyFoodSession.get().setUser(null);
                setResponsePage(ListEventPage.class);
            }
        };
        logOutLink.setVisible(isAuthenticated);
        return logOutLink;
    }

    private Link<Object> initManageSpamLink(String wicketId) {
        Link<Object> spamAdminLink = new Link<Object>(wicketId) {
            private static final long serialVersionUID = 8547475298180135256L;

            @Override
            public void onClick() {
                setResponsePage(SpamAdminPage.class);
            }
        };

        if (MyFoodSession.get().getUser() == null) {
            spamAdminLink.setVisible(false);
        } else {
            spamAdminLink.setVisible(MyFoodSession.get().getUser().isAdmin());
        }
        return spamAdminLink;
    }
}
