package com.tieto.food.ui;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import com.tieto.food.ui.event.ListEventPage;

@SuppressWarnings("all") 
public class SearchPanel extends Panel {
    private static final long serialVersionUID = -2747771170996403057L;
    private SearchModel searchModel;

    public SearchPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        initializeSearchModel();
        add(initSearchForm("searchForm"));
    }

    private void initializeSearchModel() {
        if (searchModel == null) {
            searchModel = new SearchModel();
        }
    }

    private Form<?> initSearchForm(String wicketId) {
        Form<?> searchForm = new Form<Void>(wicketId);
        AjaxFormValidatingBehavior.addToAllFormComponents(searchForm,
                "onkeyup", Duration.ONE_SECOND);
        searchForm.add(initTextField("searchText", "fullText"));
        searchForm.add(initTextField("searchParticipant", "participantText"));
        searchForm.add(initTextField("searchLocation", "locationText"));
        searchForm.add(initSearchButton("searchButton"));
        return searchForm;
    }

    private AjaxButton initSearchButton(String wicketId) {
        return new AjaxButton(wicketId) {
            private static final long serialVersionUID = -999546755353936040L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                performSearch();
            }
        };
    }

    private void performSearch() {
        setResponsePage(ListEventPage.class, ListEventPage.parametersWith(
                searchModel.getFullText(), searchModel.getParticipantText(),
                searchModel.getLocationText()));
    }

    private TextField<String> initTextField(String wicketId,
            String modelProperty) {
        return new TextField<String>(wicketId, new PropertyModel<String>(
                searchModel, modelProperty));

    }

    public SearchModel getSearchModel() {
        return searchModel;
    }

    public class SearchModel {
        private String fullText;
        private String participantText;
        private String locationText;

        SearchModel() {
        }

        public String getFullText() {
            return fullText;
        }

        public void setFullText(String fullText) {
            this.fullText = fullText;
        }

        public String getParticipantText() {
            return participantText;
        }

        public void setParticipantText(String participantText) {
            this.participantText = participantText;
        }

        public String getLocationText() {
            return locationText;
        }

        public void setLocationText(String locationText) {
            this.locationText = locationText;
        }
    }
}