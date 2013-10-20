package com.tieto.food.ui.user;

import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import com.tieto.food.domain.entity.Facebook;
import com.tieto.food.domain.entity.TextSubscription;
import com.tieto.food.domain.entity.Type;
import com.tieto.food.domain.entity.TypeSubscription;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.entity.UserSubscription;
import com.tieto.food.domain.service.FacebookService;
import com.tieto.food.domain.service.TextSubscriptionService;
import com.tieto.food.domain.service.TypeService;
import com.tieto.food.domain.service.TypeSubscriptionService;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.domain.service.UserSubscriptionService;
import com.tieto.food.ui.BasePage;
import com.tieto.food.ui.event.ListEventPage;
import com.tieto.food.ui.utils.EmailExistsValidator;
import com.tieto.food.ui.utils.MyFoodSession;
import com.tieto.food.ui.utils.PasswordEncryption;
import com.tieto.food.ui.utils.PasswordValidator;

@SuppressWarnings("all") 
public class EditUserInfoPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private List<Type> availableType;
    private List<Type> subscriptionType;
    private Type selectedTypeToAdd;
    private Type selectedTypeToRemove;

    private List<User> availableUser;
    private List<User> subscriptionUser;
    private User selectedUserToAdd;
    private User selectedUserToRemove;

    private String textSubscription;

    @SpringBean
    private TextSubscriptionService textSubscriptionService;

    @SpringBean
    private UserService userService;

    @SpringBean
    private FacebookService fbService;

    @SpringBean
    private TypeService typeService;

    @SpringBean
    private TypeSubscriptionService typeSubscriptionService;

    @SpringBean
    private UserSubscriptionService userSubscriptionService;

    public static final String USER_ID = "userId";
    private User user;
    private User tempUser;

    public static PageParameters parametersWith(Long userId) {
        return new PageParameters().add(USER_ID, userId);
    }

    public EditUserInfoPage(PageParameters params) {
        try {
            user = userService.loadById(params.get(USER_ID).toLong());
        } catch (Exception e) {
            user = null;
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (!isLoggedIn()) {
            setResponsePage(UserLoginPage.class);
            return;
        }
        if (user == null) {
            setResponsePage(ListEventPage.class);
            return;
        }
        if (!(user.getId() == MyFoodSession.get().getUser().getId() || MyFoodSession
                .get().getUser().isAdmin())) {
            setResponsePage(ListEventPage.class);
            return;
        }
        initTypesList();
        initUserList();
        initSubscriptionText();
        tempUser = loadUserInfo();
        Form<?> form2 = initChangePasswordForm();
        if (fbService.findByUserId(user.getId()) != null) {
            form2.setVisible(false);
        }
        add(initSubscriptionForm("subscriptionForm"));
        add(initContactInfoForm());
        add(initDeleteAccountForm());
        add(initNotificationsType("eventTypeForm"));
        add(initNotificationsUser("userForm"));
        add(form2);

    }

    private Form<?> initSubscriptionForm(String wicketId) {
        Form<?> form = new Form<Void>(wicketId);
        form.add(initTextAria("textSubscription"));
        form.add(initUpdateSubscriptionsButton("updateSubscriptions"));
        return form;
    }

    private TextArea<String> initTextAria(String wicketId) {
        TextArea<String> textAria = new TextArea<String>(wicketId,
                new PropertyModel<String>(this, "textSubscription"));
        textAria.add(new StringValidator(0, 255));
        return textAria;
    }

    private Button initUpdateSubscriptionsButton(String wicketId) {
        return new Button(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                User u = MyFoodSession.get().getUser();
                typeSubscriptionService.removeAllByUser(u);
                userSubscriptionService.removeAllByUser(u);
                for (Type type : subscriptionType) {
                    typeSubscriptionService
                            .merge(new TypeSubscription(u, type));
                }

                for (User subscriber : subscriptionUser) {
                    userSubscriptionService.merge(new UserSubscription(u,
                            subscriber));
                }
                if (textSubscription != null) {
                    String[] array = textSubscription.split(",");
                    for (String text : array) {
                        text = text.trim();
                        if (text.length() != 0) {
                            textSubscriptionService.merge(new TextSubscription(
                                    u, text));
                        }
                    }
                }
                setResponsePage(
                        EditUserInfoPage.class,
                        EditUserInfoPage.parametersWith(MyFoodSession.get()
                                .getUser().getId()));
            }

        };
    }

    @SuppressWarnings("unchecked")
    private void initTypesList() {
        List<Type> allTypes = typeService.listAll();
        subscriptionType = typeSubscriptionService.getTypesByUser(MyFoodSession
                .get().getUser());
        availableType = ListUtils.subtract(allTypes, subscriptionType);
    }

    @SuppressWarnings("unchecked")
    private void initUserList() {
        List<User> allUsers = userService.listOnlyExisting();
        subscriptionUser = userSubscriptionService
                .getSubscribedUsersByUser(MyFoodSession.get().getUser());
        availableUser = ListUtils.subtract(allUsers, subscriptionUser);
        availableUser.remove(MyFoodSession.get().getUser());
    }

    private void initSubscriptionText() {
        List<String> subText = textSubscriptionService
                .getTextsByUser(MyFoodSession.get().getUser());
        if (subText.size() != 0) {
            textSubscription = "";
            for (String text : subText) {
                textSubscription += text + ", ";
            }

            textSubscription = textSubscription.substring(0,
                    textSubscription.length() - 2);
        }
    }

    private Form<?> initNotificationsType(String wicketId) {
        Form<?> form = new Form<Void>(wicketId);
        DropDownChoice<Type> listOfAvailable = initListOfAvailableType(
                "availableTypes", "selectedTypeToAdd", availableType);
        DropDownChoice<Type> listOfSubscriptions = initListOfSubscriptionsType(
                "subscriptionsTypes", "selectedTypeToRemove", subscriptionType);
        form.add(initButtonAddToSubscriptionsType("notificationTypeAdd",
                listOfAvailable, listOfSubscriptions));
        form.add(initButtonremoveFromSubscriptionsType(
                "notificationTypeRemove", listOfAvailable, listOfSubscriptions));
        form.add(listOfAvailable);
        form.add(listOfSubscriptions);
        return form;
    }

    private Form<?> initNotificationsUser(String wicketId) {
        Form<?> form = new Form<Void>(wicketId);
        DropDownChoice<User> listOfAvailable = initListOfAvailableUser(
                "availableUser", "selectedUserToAdd", availableUser);
        DropDownChoice<User> listOfSubscriptions = initListOfSubscriptionsUser(
                "subscriptionsUser", "selectedUserToRemove", subscriptionUser);
        form.add(initButtonAddToSubscriptionsUser("notificationUserAdd",
                listOfAvailable, listOfSubscriptions));
        form.add(initButtonremoveFromSubscriptionsUser(
                "notificationUserRemove", listOfAvailable, listOfSubscriptions));
        form.add(listOfAvailable);
        form.add(listOfSubscriptions);
        return form;
    }

    private DropDownChoice<User> initListOfSubscriptionsUser(String wicketId,
            String exspression, List<User> subscriptions) {
        DropDownChoice<User> listOfSubscriptions = new DropDownChoice<User>(
                wicketId, new PropertyModel<User>(this, exspression),
                subscriptions) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {
                return "";
            }

        };
        listOfSubscriptions.setOutputMarkupId(true);
        return listOfSubscriptions;
    }

    private DropDownChoice<User> initListOfAvailableUser(String wicketId,
            String exspression, List<User> available) {
        DropDownChoice<User> listOfAvailable = new DropDownChoice<User>(
                wicketId, new PropertyModel<User>(this, exspression), available) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {
                return "";
            }

        };
        listOfAvailable.setOutputMarkupId(true);
        return listOfAvailable;
    }

    private DropDownChoice<Type> initListOfAvailableType(String wicketId,
            String exspression, List<Type> available) {
        DropDownChoice<Type> listOfAvailable = new DropDownChoice<Type>(
                wicketId, new PropertyModel<Type>(this, exspression), available) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {
                return "";
            }

        };
        listOfAvailable.setOutputMarkupId(true);
        return listOfAvailable;
    }

    private DropDownChoice<Type> initListOfSubscriptionsType(String wicketId,
            String exspression, List<Type> subscriptions) {
        DropDownChoice<Type> listOfSubscriptions = new DropDownChoice<Type>(
                wicketId, new PropertyModel<Type>(this, exspression),
                subscriptions) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {
                return "";
            }

        };
        listOfSubscriptions.setOutputMarkupId(true);
        return listOfSubscriptions;
    }

    private AjaxButton initButtonremoveFromSubscriptionsType(String wicketId,
            final DropDownChoice<Type> listOfAvailable,
            final DropDownChoice<Type> listOfSubscriptions) {
        return new AjaxButton(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (selectedTypeToRemove != null) {
                    subscriptionType.remove(selectedTypeToRemove);
                    availableType.add(selectedTypeToRemove);
                    target.add(listOfAvailable);
                    target.add(listOfSubscriptions);
                }
            }

        };
    }

    private AjaxButton initButtonAddToSubscriptionsType(String wicketId,
            final DropDownChoice<Type> listOfAvailable,
            final DropDownChoice<Type> listOfSubscriptions) {
        return new AjaxButton(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (selectedTypeToAdd != null) {
                    availableType.remove(selectedTypeToAdd);
                    subscriptionType.add(selectedTypeToAdd);
                    target.add(listOfAvailable);
                    target.add(listOfSubscriptions);
                }
            }

        };
    }

    private AjaxButton initButtonAddToSubscriptionsUser(String wicketId,
            final DropDownChoice<User> listOfAvailable,
            final DropDownChoice<User> listOfSubscriptions) {
        return new AjaxButton(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (selectedUserToAdd != null) {
                    availableUser.remove(selectedUserToAdd);
                    subscriptionUser.add(selectedUserToAdd);
                    target.add(listOfAvailable);
                    target.add(listOfSubscriptions);
                }
            }

        };
    }

    private AjaxButton initButtonremoveFromSubscriptionsUser(String wicketId,
            final DropDownChoice<User> listOfAvailable,
            final DropDownChoice<User> listOfSubscriptions) {
        return new AjaxButton(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (selectedUserToRemove != null) {
                    subscriptionUser.remove(selectedUserToRemove);
                    availableUser.add(selectedUserToRemove);
                    target.add(listOfAvailable);
                    target.add(listOfSubscriptions);
                }
            }

        };
    }

    private User loadUserInfo() {
        User placeHolderUser = new User();
        placeHolderUser.setName(user.getName());
        placeHolderUser.setSurname(user.getSurname());
        placeHolderUser.setEmail(user.getEmail());
        placeHolderUser.setPassword(user.getPassword());
        placeHolderUser.setSendJoinLeaveNotifications(user
                .isSendJoinLeaveNotifications());
        placeHolderUser.setAdmin(user.isAdmin());
        return placeHolderUser;
    }

    private Button initContactInfoSubmitButton(String wicketId) {
        return new Button(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                user.setName(tempUser.getName());
                user.setSurname(tempUser.getSurname());
                user.setEmail(tempUser.getEmail());
                user.setSendJoinLeaveNotifications(tempUser
                        .isSendJoinLeaveNotifications());
                user.setAdmin(tempUser.isAdmin());
                userService.merge(user);
                info("Profile updated successfully");
            }
        };
    }

    private Button initPasswordSubmitButton(String wicketId) {
        return new Button(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                String password = PasswordEncryption.encrypt(tempUser
                        .getPassword());
                user.setPassword(password);
                userService.merge(user);
                info("Password changed successfully");
            }
        };
    }

    private Button initializeDeleteButton(String wicketId) {
        return new Button(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                Facebook fb = fbService.findByUserId(user.getUserId());
                if (fb != null) {
                    User fbUser = userService.loadById(fb.getId());
                    fbService.remove(fb);
                    userService.remove(fbUser);
                    if (!MyFoodSession.get().getUser().isAdmin()) {
                        MyFoodSession.get().setUser(null);
                    }
                } else {
                    userService.remove(user);
                    if (!MyFoodSession.get().getUser().isAdmin()) {
                        MyFoodSession.get().setUser(null);
                    } else {
                        if (MyFoodSession.get().getUser().getId() == user
                                .getId()) {
                            MyFoodSession.get().setUser(null);
                        }
                    }
                }
                setResponsePage(ListEventPage.class);
            }
        };
    }

    private Form<?> initDeleteAccountForm() {
        Form<?> form3 = new Form<Object>("form3");
        Label form3Label = new Label("form3Label", "Enter password");

        PasswordTextField password = new PasswordTextField("password",
                Model.of(""));
        password.setRequired(false);
        if (fbService.findByUserId(user.getId()) != null) {
            password.add(new PasswordValidator(user, password));
            form3Label.setVisible(false);
        }
        form3.add(form3Label);
        form3.add(initializeDeleteButton("deleteAccountBtn"));
        form3.add(password);
        FeedbackPanel feedbackPanel3 = new FeedbackPanel("feedback3");
        feedbackPanel3.setFilter(new ContainerFeedbackMessageFilter(form3));
        form3.add(feedbackPanel3);
        if (fbService.findByUserId(user.getId()) != null) {
            password.setVisible(false);
        }
        return form3;
    }

    private Form<?> initChangePasswordForm() {
        Form<?> form2 = new Form<Object>("form2");
        PasswordTextField oldPassword = new PasswordTextField("oldPasswordTF",
                Model.of(""));
        oldPassword.add(new PasswordValidator(user, oldPassword));
        oldPassword.setRequired(false);
        form2.add(oldPassword);

        PasswordTextField newPassword = new PasswordTextField("newPasswordTF",
                new PropertyModel<String>(tempUser, "password"));
        newPassword.setRequired(false);
        form2.add(newPassword);
        PasswordTextField checkNewPassword = new PasswordTextField(
                "confirmNewPasswordTF", Model.of(""));
        checkNewPassword.setRequired(false);
        form2.add(checkNewPassword);
        form2.add(initPasswordSubmitButton("changePasswordBtn"));
        FeedbackPanel feedbackPanel2 = new FeedbackPanel("feedback2");
        feedbackPanel2.setFilter(new ContainerFeedbackMessageFilter(form2));
        form2.add(feedbackPanel2);
        add(new Label("headerNameLabel", user.getName() + " "
                + user.getSurname()));
        return form2;

    }

    private Form<?> initContactInfoForm() {
        Form<?> form1 = new Form<Object>("form1");
        TextField<String> nameTF = new TextField<String>("nameTF",
                new PropertyModel<String>(tempUser, "name"));
        form1.add(nameTF);
        TextField<String> lastNameTF = new TextField<String>("lastNameTF",
                new PropertyModel<String>(tempUser, "surname"));
        form1.add(lastNameTF);

        TextField<String> email = new TextField<String>("emailTF",
                new PropertyModel<String>(tempUser, "email"));
        email.add(new EmailExistsValidator(userService, user));
        form1.add(email);
        form1.add(initContactInfoSubmitButton("submitContInfo"));
        FeedbackPanel feedbackPanel1 = new FeedbackPanel("feedback1");
        feedbackPanel1.setFilter(new ContainerFeedbackMessageFilter(form1));
        form1.add(feedbackPanel1);
        CheckBox chk1 = new CheckBox("joinLeaveEventCB",
                new PropertyModel<Boolean>(tempUser,
                        "sendJoinLeaveNotifications"));
        form1.add(chk1);
        CheckBox chk2 = new CheckBox("upcomingEventNotificationsCB",
                new PropertyModel<Boolean>(tempUser,
                        "sendUpcomingEventNotifications"));
        form1.add(chk2);
        if (fbService.findByUserId(user.getId()) != null) {
            nameTF.setEnabled(false);
            lastNameTF.setEnabled(false);
            email.setEnabled(false);
        }
        Label adminRightsLabel;
        CheckBox chk3;
        if (MyFoodSession.get().getUser().isAdmin()) {
            adminRightsLabel = new Label("adminRightsLabel",
                    "User has admin rights: ");
            chk3 = new CheckBox("adminRights", new PropertyModel<Boolean>(
                    tempUser, "admin"));
        } else {
            adminRightsLabel = new Label("adminRightsLabel", "");
            chk3 = new CheckBox("adminRights");
            adminRightsLabel.setVisible(false);
            chk3.setVisible(false);
        }
        form1.add(adminRightsLabel);
        form1.add(chk3);
        return form1;
    }

}