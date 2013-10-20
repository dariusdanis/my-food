package com.tieto.food.ui.user;

import java.util.Date;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.BasePage;
import com.tieto.food.ui.PageNotFound;
import com.tieto.food.ui.event.ListEventPage;
import com.tieto.food.ui.utils.EmailExistsValidator;
import com.tieto.food.ui.utils.MyFoodSession;
import com.tieto.food.ui.utils.PasswordEncryption;

@SuppressWarnings("all") 
public class UserRegistrationPage extends BasePage {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private UserService userService;
    private User user;

    public UserRegistrationPage() {
    }

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		if (isLoggedIn()) {
            setResponsePage(ListEventPage.class);
        }
		
		user = new User();
		FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
		add(feedbackPanel);
		Form<?> form = new Form<Void>("form");
		form.add(new TextField<String>("nameTF", new PropertyModel<String>(
				user, "name")));
		form.add(new TextField<String>("lastNameTF", new PropertyModel<String>(
				user, "surname")));

        PasswordTextField password = new PasswordTextField("passwordTF",
                new PropertyModel<String>(user, "password"));
        password.setRequired(false);
        form.add(password);
        PasswordTextField checkPassword = new PasswordTextField(
                "confirmPasswordTF", Model.of(""));
        checkPassword.setRequired(false);
        form.add(checkPassword);
        form.add(initRegisterButton("register"));
        form.add(initEmailTextField());
        add(new Label("formTitle", "Create an account"));
        add(form);
    }


    private TextField<String> initEmailTextField() {
        TextField<String> email = new TextField<String>("emailTF",
                new PropertyModel<String>(user, "email"));
        email.add(new EmailExistsValidator(userService));
        return email;
    }

    private Button initRegisterButton(String wicketId) {
        return new Button(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                try {
                    user.setPassword(PasswordEncryption.encrypt(user
                            .getPassword()));
                    user.setOrExist(true);
                    user.setJoinDate(new Date());
                    user.setSendJoinLeaveNotifications(false);
                    user = userService.merge(user);
                    MyFoodSession.get().setUser(user);
                    setResponsePage(ListEventPage.class);
                } catch (Exception e) {
                    setResponsePage(PageNotFound.class);
                }
            }
        };
    }
}