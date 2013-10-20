package com.tieto.food.ui.utils;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.UserService;

public class EmailExistsValidator implements IValidator<String> {
    private static final long serialVersionUID = 1L;
    private UserService userService;
    private User user;
    // True if user wants to change email
    private boolean updateEmail = false;

    public EmailExistsValidator(UserService userService) {
        this.userService = userService;
    }

    public EmailExistsValidator(UserService userService, User user) {
        this.userService = userService;
        this.user = user;
        updateEmail = true;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        if (updateEmail) {
            String email = validatable.getValue().toString();
            if (userService.loadByEmail(email) != null
                    && !user.getEmail().equals(email)) {
                error(validatable, "User with email " + email
                        + " allready exsists.");
            }
        } else {
            String email = validatable.getValue().toString();
            if (userService.loadByEmail(email) != null) {
                error(validatable, "User with email " + email
                        + " allready exsists.");
            }

        }
    }

    private void error(IValidatable<String> validatable, String errorKey) {
        ValidationError error = new ValidationError();
        error.addKey(getClass().getSimpleName() + "." + errorKey);
        error.setMessage(errorKey);
        validatable.error(error);
    }
}
