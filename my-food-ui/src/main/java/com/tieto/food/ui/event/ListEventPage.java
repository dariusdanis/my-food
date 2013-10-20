package com.tieto.food.ui.event;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.service.EventService;
import com.tieto.food.ui.BasePage;
import com.tieto.food.ui.SearchPanel;

@SuppressWarnings("all") 
public class ListEventPage extends BasePage {
    private static final long serialVersionUID = 680799783656763653L;
    private static final String FILTER_TEXT = "filterTextParam";
    private static final String FILTER_PARTICIPANT = "filterParticipantParam";
    private static final String FILTER_LOCATION = "filterLocationParam";
    private PageParameters pageParameters;

    @SpringBean
    private EventService eventService;

    public static PageParameters parametersWith(String textFilter,
            String participantFilter, String locationFilter) {
        PageParameters pageParameters = new PageParameters();
        if (textFilter != null) {
            pageParameters.add(FILTER_TEXT, textFilter);
        }
        if (participantFilter != null) {
            pageParameters.add(FILTER_PARTICIPANT, participantFilter);
        }
        if (locationFilter != null) {
            pageParameters.add(FILTER_LOCATION, locationFilter);
        }
        return pageParameters;
    }

    public ListEventPage(PageParameters pageParameters) {
        this.pageParameters = pageParameters;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        initializeSearchPanel();
        addInfoMessage();
        add(new EventsListPanel("eventsListPanel", initEventListModel()));
    }

    public void addInfoMessage() {
        Label infoMessage;
        if (!getSession().getFeedbackMessages().isEmpty()) {
            infoMessage = new Label("infoMessage", getSession()
                    .getFeedbackMessages().iterator().next().getMessage()
                    .toString());
            getSession().getFeedbackMessages().clear();
        } else {
            infoMessage = new Label("infoMessage", "");
            infoMessage.setVisible(false);
        }
        add(infoMessage);
    }

    private void initializeSearchPanel() {
        SearchPanel searchPanel = new SearchPanel("searchPanel");
        searchPanel = (SearchPanel) add(searchPanel).get("searchPanel");
        if (pageParameters != null) {
            StringValue text = pageParameters.get(FILTER_TEXT);
            StringValue user = pageParameters.get(FILTER_PARTICIPANT);
            StringValue location = pageParameters.get(FILTER_LOCATION);
            if (text != null) {
                searchPanel.getSearchModel().setFullText(text.toString());
            }
            if (user != null) {
                searchPanel.getSearchModel()
                        .setParticipantText(user.toString());
            }
            if (location != null) {
                searchPanel.getSearchModel().setLocationText(
                        location.toString());
            }
        }
    }

    private IModel<List<Event>> initEventListModel() {
        return new LoadableDetachableModel<List<Event>>() {
            private static final long serialVersionUID = 7973708256192453464L;

            @Override
            protected List<Event> load() {
                if (isFilteringRequired()) {
                    return returnFilteredEvents();
                } else {
                    return eventService.listValidFutureEvents();
                }
            }
        };
    }

    private List<Event> returnFilteredEvents() {
        List<Event> searchResults = eventService.searchEvents(pageParameters
                .get(FILTER_TEXT).toString(),
                pageParameters.get(FILTER_PARTICIPANT).toString(),
                pageParameters.get(FILTER_LOCATION).toString());
        getSession().info("Search found " + searchResults.size() + " results");
        remove("infoMessage");
        addInfoMessage();
        return searchResults;
    }

    private boolean isFilteringRequired() {
        return !(pageParameters.get(FILTER_TEXT).isEmpty()
                && pageParameters.get(FILTER_PARTICIPANT).isEmpty() && pageParameters
                .get(FILTER_LOCATION).isEmpty());
    }
}