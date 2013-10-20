package com.tieto.food.ui.utils;

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.tieto.food.domain.entity.User;

public class PasswordValidator implements IValidator<String> {

    private static final long serialVersionUID = 1L;

    private User user;
    private PasswordTextField passwordTF;

    public PasswordValidator(User user, PasswordTextField passwordTF) {
        this.user = user;
        this.passwordTF = passwordTF;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        if (!user.getPassword().equals(
                PasswordEncryption.encrypt(passwordTF.getInput()))) {
            error(validatable, "Incorrect password");
        }
    }

    private void error(IValidatable<String> validatable, String errorKey) {
        ValidationError error = new ValidationError();
        error.addKey(getClass().getSimpleName() + "." + errorKey);
        error.setMessage(errorKey);
        validatable.error(error);
    }

}
