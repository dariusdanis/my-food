package com.tieto.food.ui;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.stereotype.Component;

import com.tieto.food.ui.charts.SiteStatistics;
import com.tieto.food.ui.event.AddEventPage;
import com.tieto.food.ui.event.EditEventPage;
import com.tieto.food.ui.event.EventMapPage;
import com.tieto.food.ui.event.EventPage;
import com.tieto.food.ui.event.ListEventPage;
import com.tieto.food.ui.spam.SpamAdminPage;
import com.tieto.food.ui.user.EditUserInfoPage;
import com.tieto.food.ui.user.UserLoginPage;
import com.tieto.food.ui.user.UserProfilePage;
import com.tieto.food.ui.user.UserRegistrationPage;
import com.tieto.food.ui.utils.EmailClient;
import com.tieto.food.ui.utils.MyFoodSession;
import com.tieto.food.ui.utils.UpcomingEventNotificationThread;

@Component("wicketApplication")
public class Application extends WebApplication {

    @Override
    public Class<? extends Page> getHomePage() {
        return ListEventPage.class;
    }

    @Override
    public final Session newSession(Request request, Response response) {
        return new MyFoodSession(request);
    }

    @Override
    protected void init() {
        super.init();
        getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            @Override
            public IRequestHandler onException(RequestCycle cycle, Exception e) {
                return new RenderPageRequestHandler(new PageProvider(
                        new PageNotFound()));
            }
        });

        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getComponentInstantiationListeners().add(
                new SpringComponentInjector(this));
        mountPage("event/${eventId}", EventPage.class);
        mountPage("event/add", AddEventPage.class);
        mountPage("event/edit/${eventId}", EditEventPage.class);
        mountPage("user/register", UserRegistrationPage.class);
        mountPage("user/login", UserLoginPage.class);
        mountPage("user/profile/${userId}", UserProfilePage.class);
        mountPage("user/profile/edit/${userId}", EditUserInfoPage.class);
        mountPage("/404", PageNotFound.class);
        mountPage("/event/eventMap", EventMapPage.class);
        mountPage("/spam/manage", SpamAdminPage.class);
        mountPage("/statistics", SiteStatistics.class);

        new UpcomingEventNotificationThread(
                EmailClient.getInstanceOfEmailClient()).startThread();
    }
}
