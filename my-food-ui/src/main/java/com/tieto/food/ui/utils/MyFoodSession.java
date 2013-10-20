package com.tieto.food.ui.utils;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.tieto.food.domain.entity.User;

public class MyFoodSession extends WebSession {
    private static final long serialVersionUID = 1L;
    private User user;
    private Class<? extends IRequestablePage> cachedPageClassToLoad;
    private PageParameters cachedPagePageParameters;

    public MyFoodSession(Request request) {
        super(request);
    }

    public static MyFoodSession get() {
        return (MyFoodSession) Session.get();
    }

    public User getUser() {
        return user;
    }

    public boolean isAuthenticated() {
        return (user != null);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCachedPage(Class<? extends IRequestablePage> cachedPage,
            PageParameters cachedPagePageParameters) {
        this.cachedPageClassToLoad = cachedPage;
        this.cachedPagePageParameters = cachedPagePageParameters;
    }

    public Class<? extends IRequestablePage> getCachedPageClass() {
        return cachedPageClassToLoad;
    }

    public PageParameters getCachedPageParameters() {
        return cachedPagePageParameters;
    }
}