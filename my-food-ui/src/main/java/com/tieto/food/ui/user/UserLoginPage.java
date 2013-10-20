package com.tieto.food.ui.user;

import java.util.Date;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import com.tieto.food.domain.entity.Facebook;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.FacebookService;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.BasePage;
import com.tieto.food.ui.PageNotFound;
import com.tieto.food.ui.event.ListEventPage;
import com.tieto.food.ui.utils.FacebookClient;
import com.tieto.food.ui.utils.LoginValidator;
import com.tieto.food.ui.utils.MyFoodSession;

@SuppressWarnings("all") 
public class UserLoginPage extends BasePage {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private UserService userService;

    @SpringBean
    private FacebookService facebookService;

    private String email;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (isLoggedIn()) {
            setResponsePage(ListEventPage.class);
        }
        Form<?> form = new Form<Void>("form");
        PasswordTextField password = initPasswordTextField("passwordTF");
        form.add(initEmailTextField("emailTF", password));
        form.add(password);
        form.add(initFbLoginLink("fb"));
        form.add(initLoginButton("login"));
        form.add(initRegisterLink("register"));
        form.add(new Label("formTitle", "Sign in"));
        add(new FeedbackPanel("feedback"));
        add(form);
        loginWithFacebook();
    }

    private void loginWithFacebook() {
        StringValue code = RequestCycle.get().getRequest()
                .getRequestParameters().getParameterValue("code");
        if (!code.isEmpty()) {
            com.face4j.facebook.entity.User fbUser = FacebookClient
                    .getUser(code);
            if (fbUser != null) {
                Facebook facebook = facebookService.findByFacebookId(Long
                        .valueOf(fbUser.getId()));
                if (facebook == null) {
                    User u = userService.loadByEmail(fbUser.getEmail());
                    if (u != null) {
                        setResponsePage(UserLoginPage.class);
                        return;
                    }
                    User user = new User(fbUser.getEmail(),
                            fbUser.getFirstName(), fbUser.getLastName());
                    user.setJoinDate(new Date());
                    user.setOrExist(true);
                    user.setSendJoinLeaveNotifications(false);
                    user = userService.merge(user);
                    facebook = new Facebook(user.getId(), (Long.valueOf(fbUser
                            .getId())));
                    facebookService.merge(facebook);
                    MyFoodSession.get().setUser(user);
                } else {
                    User user = userService.loadById(facebook.getId());
                    user.setEmail(fbUser.getEmail());
                    user.setName(fbUser.getFirstName());
                    user.setSurname(fbUser.getLastName());
                    user = userService.merge(user);
                    MyFoodSession.get().setUser(user);
                }
                redirectToARightPage();
            } else {
                // TODO bad code error
            }
        }
    }

    private void redirectToARightPage() {
        // By Default, we redirect to a list even page
        if (MyFoodSession.get().getCachedPageClass() == null) {
            setResponsePage(ListEventPage.class);
            // If the page does not have any page parameters
        } else if (MyFoodSession.get().getCachedPageClass() != null
                && MyFoodSession.get().getCachedPageParameters() == null) {
            setResponsePage(MyFoodSession.get().getCachedPageClass());
            MyFoodSession.get().setCachedPage(null, null);
            // If page has parameters, load the page with them
        } else {
            setResponsePage(MyFoodSession.get().getCachedPageClass(),
                    MyFoodSession.get().getCachedPageParameters());
            MyFoodSession.get().setCachedPage(null, null);
        }
    }

    private TextField<String> initEmailTextField(String wicketId,
            PasswordTextField password) {
        LoginValidator loginValidator = new LoginValidator(userService,
                password);
        TextField<String> emailTF = new TextField<String>(wicketId,
                new PropertyModel<String>(this, "email"));
        emailTF.add(loginValidator);
        return emailTF;
    }

    private PasswordTextField initPasswordTextField(String wicketId) {
        PasswordTextField password = new PasswordTextField(wicketId,
                Model.of(""));
        password.setRequired(false);
        return password;
    }

    private Link<Object> initFbLoginLink(String wicketId) {
        return new Link<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                throw new RedirectToUrlException(
                        FacebookClient.getRedirectURL());
            }
        };
    }

    private Button initLoginButton(String wicketId) {
        return new Button(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                User user = userService.loadByEmail(email);
                if (user != null) {
                    MyFoodSession.get().setUser(user);
                    redirectToARightPage();
                } else {
                    setResponsePage(PageNotFound.class);
                }
            }
        };
    }

    public Link<Object> initRegisterLink(String wicketId) {
        return new Link<Object>(wicketId) {
            private static final long serialVersionUID = 8837432573787831059L;

            @Override
            public void onClick() {
                setResponsePage(UserRegistrationPage.class);
            }
        };
    }
}