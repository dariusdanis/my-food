package com.tieto.food.ui.utils;

import java.util.List;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.tieto.food.domain.entity.Place;

public class PlaceValidator implements IValidator<String> {
    private static final long serialVersionUID = 1L;

    private TextField<String> textField;
    private List<Place> places;

    public PlaceValidator(TextField<String> textField, List<Place> places) {
        this.textField = textField;
        this.places = places;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        for (Place p : places) {
            if (p.getPlace().equals(textField.getInput()) && p.isOrExist()) {
                error(validatable, "Place already exists");
                break;
            }
        }
        places.add(new Place(textField.getInput()));
    }

    private void error(IValidatable<String> validatable, String errorKey) {
        ValidationError error = new ValidationError();
        error.addKey(getClass().getSimpleName() + "." + errorKey);
        error.setMessage(errorKey);
        validatable.error(error);
    }

}
