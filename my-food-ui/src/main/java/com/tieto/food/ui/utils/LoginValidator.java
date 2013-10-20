package com.tieto.food.ui.utils;

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.UserService;

public class LoginValidator implements IValidator<String> {

    private static final long serialVersionUID = 1L;

    private UserService userService;
    private PasswordTextField passwordTF;

    public LoginValidator(UserService userService, PasswordTextField passwordTF) {
        this.userService = userService;
        this.passwordTF = passwordTF;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String email = validatable.getValue().toString();
        User userObj = userService.loadByEmail(email);
        if (userObj == null) {
            error(validatable, "User with email " + email + " does not exist.");
        } else {
            if (!correctPassword(userObj)) {
                error(validatable, "Incorrect password.");
            }
        }
    }

    private void error(IValidatable<String> validatable, String errorKey) {
        ValidationError error = new ValidationError();
        error.addKey(getClass().getSimpleName() + "." + errorKey);
        error.setMessage(errorKey);
        validatable.error(error);
    }

    private boolean correctPassword(User user) {
        if (user.getPassword() != null
                && user.getPassword().equals(
                        PasswordEncryption.encrypt(passwordTF.getInput()))) {
            return true;
        }
        return false;
    }
}
