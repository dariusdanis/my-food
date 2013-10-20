package com.tieto.food.ui;

import org.apache.wicket.markup.html.basic.Label;

public class PageNotFound extends BasePage {
    private static final long serialVersionUID = 1L;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("404", "Oops, you shouldn't be here :-)"));
    }

}
