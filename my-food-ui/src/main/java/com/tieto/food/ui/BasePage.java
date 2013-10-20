package com.tieto.food.ui;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.ui.utils.MyFoodSession;

@SuppressWarnings("all") 
public class BasePage extends WebPage {
    private static final long serialVersionUID = 1L;
    protected static final int MAX_STRING_LENGTH = 15;
    protected static final int MIN_STRING_LENGTH = 1;
    protected static final int MAX_PASSWORD_LENGTH = 30;
    protected static final int MAX_EMAIL_LENGTH = 30;
    protected static final int MAX_LAST_NAME_LENGTH = 30;

    private static String absolutePath;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TopMenuPanel("topPanel"));
        if (absolutePath == null) {
            initializeAbsolutePath();
        }
    }

    protected boolean isLoggedIn() {
        return (MyFoodSession.get().isAuthenticated());
    }

    protected boolean isAdmin() {
        return (MyFoodSession.get().getUser().isAdmin());
    }

    protected boolean isAuthenticatedToAcces(Event event) {
        try {
            return (MyFoodSession.get().getUser().isAdmin() || MyFoodSession
                    .get().getUser().getId() == event.getCreatedBy());
        } catch (Exception e) {
            return false;
        }
    }

    private void initializeAbsolutePath() {
        HttpServletRequest req = (HttpServletRequest) ((WebRequest) RequestCycle
                .get().getRequest()).getContainerRequest();
        absolutePath = RequestUtils.toAbsolutePath(req.getRequestURL()
                .toString(), "");
    }

    public static String getAbsolutePath() {
        return absolutePath;
    }
}