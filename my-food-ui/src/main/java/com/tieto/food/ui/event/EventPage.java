package com.tieto.food.ui.event;

import java.util.Date;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import com.tieto.food.domain.entity.Comment;
import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.service.CommentService;
import com.tieto.food.domain.service.EventService;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.BasePage;
import com.tieto.food.ui.utils.MyFoodSession;

@SuppressWarnings("all") 
public class EventPage extends BasePage {
    private static final long serialVersionUID = 1L;
    public static final String EVENT_ID = "eventId";

    @SpringBean
    private EventService eventService;

    @SpringBean
    private UserService userService;

    @SpringBean
    private CommentService commentService;

    private Event event;
    private List<Comment> list;
    private TextArea<String> textArea;

    @SuppressWarnings("unused")
    // do not delete this!!!!!
    private String emptyText = "";

    public static PageParameters parametersWith(Long eventId) {
        return new PageParameters().add(EVENT_ID, eventId);
    }

    public EventPage(PageParameters params) {
        try {
            event = eventService.loadById(params.get(EVENT_ID).toLong());
            list = commentService.listByEvent(event);
            if (event.getEventDate().before(new Date())) {
                event = null;
            }
        } catch (Exception e) {
            event = null;
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (event == null) {
            getSession().info("Event doesn't exist!");
            setResponsePage(ListEventPage.class);
            return;
        }
        add(new EventsListItemPanel("eventItem", event));
        Form<?> form = new Form<Void>("commentForm");
        MarkupContainer commentDiv = new WebMarkupContainer("commentDiv");
        commentDiv.setOutputMarkupId(true);
        form.add(commentDiv);
        ListView<Comment> listView = initCommentListView("comments", list);
        commentDiv.add(listView);
        form.add(initSubbmitLink("addNewComment", listView, commentDiv));
        textArea = initCommentTextArea("commentArea");
        form.add(textArea);
        add(form);
    }

    private TextArea<String> initCommentTextArea(String wicketId) {
        TextArea<String> commentTextArea = new TextArea<String>(wicketId,
                new PropertyModel<String>(this, "emptyText"));
        commentTextArea.add(new StringValidator(0, 255));
        commentTextArea.setOutputMarkupId(true);
        if (!MyFoodSession.get().isAuthenticated()) {
            commentTextArea.setEnabled(false);
        }
        return commentTextArea;
    }

    private AjaxButton initSubbmitLink(String wicketId,
            final ListView<Comment> listView, final MarkupContainer commentDiv) {
        AjaxButton commentLink = new AjaxButton(wicketId) {
            private static final long serialVersionUID = 1L;
            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit();
                String commentText = getRequest().getRequestParameters()
                        .getParameterValue("commentArea").toString();
                if (commentText != null && commentText.trim().length() != 0
                        && target != null) {
                    Comment comment = new Comment(commentText,
                            userService.loadById(MyFoodSession.get().getUser()
                                    .getId()), event, new Date());
                    listView.getModelObject()
                            .add(commentService.merge(comment));
                    target.add(commentDiv);
                    textArea.setModel(new PropertyModel<String>(EventPage.this,
                            "emptyText"));
                    target.add(textArea);
                }
            }
        };
        commentLink.setDefaultFormProcessing(false);
        commentLink.setEnabled(MyFoodSession.get().isAuthenticated());
        return commentLink;

    }

    private ListView<Comment> initCommentListView(String wicketId,
            List<Comment> list) {
        ListView<Comment> listView = new ListView<Comment>(wicketId, list) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Comment> item) {
                item.add(new CommentPanel("commentItem", item.getModelObject(), event));
            }
        };
        return listView;
    }

}