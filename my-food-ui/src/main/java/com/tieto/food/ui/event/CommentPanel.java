package com.tieto.food.ui.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import com.tieto.food.domain.entity.Comment;
import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.service.CommentService;
import com.tieto.food.ui.user.UserProfilePage;
import com.tieto.food.ui.utils.MyFoodSession;

@SuppressWarnings("all") 
public class CommentPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private final static String DO_NOT_DISPLAY_STYLE = 
            "visibility:hidden;display:none;";
    private final static String FORMAT_ATTRIBUTE =  "%02d";
    private Comment comment;

    @SpringBean
    private CommentService commentService;
    
    private Event event;
    private boolean edit;

    public CommentPanel(String id, Comment comment, Event event) {
        super(id);
        this.event = event;
        this.comment = comment;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<?> form = new Form<Void>("form");
        Label label = initComment("message", comment);
        add(label);
        TextArea<String> textAria = initEditTextArea("editTextAre");
        form.add(textAria);
        add(initDate("date", comment));
        add(initAuthor("author", comment));
        add(initTime("time", comment));
        add(initDeleteLink("deleteComment", comment));
        AjaxSubmitLink ajaxSubmitLink = initSubmitLink("subbmit", label,
                textAria);
        add(initEditLink("editComment", label, textAria, ajaxSubmitLink));
        form.add(ajaxSubmitLink);
        add(form);
    }

    @SuppressWarnings("all") 
    private AjaxSubmitLink initSubmitLink(String wicketId, final Label label,
            final TextArea<String> textAria) {
        final AjaxSubmitLink ajaxSubmitLink = new AjaxSubmitLink(wicketId) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (edit) {
                    this.add(new AttributeModifier("style",
                            DO_NOT_DISPLAY_STYLE));
                    commentService.merge(comment);
                    label.add(new AttributeModifier("style", ""));
                    textAria.add(new AttributeModifier("style",
                            DO_NOT_DISPLAY_STYLE));
                    label.setDefaultModel(new PropertyModel<String>(
                            CommentPanel.this, "comment.comment"));
                    target.add(textAria);
                    target.add(label);
                    target.add(this);
                    edit = false;
                }
            }
        };
        ajaxSubmitLink.setVisible(MyFoodSession.get().isAuthenticated()
                && MyFoodSession.get().getUser().getUserId() == comment
                        .getUser().getId());
        ajaxSubmitLink.add(new AttributeModifier("style", DO_NOT_DISPLAY_STYLE));
        return ajaxSubmitLink;
    }

    private TextArea<String> initEditTextArea(String wicketId) {
        TextArea<String> textAria = new TextArea<String>(
                wicketId,
                new PropertyModel<String>(CommentPanel.this, 
                        "comment.comment"));
        textAria.add(new StringValidator(0, 255));
        textAria.add(new AttributeModifier("style",
                DO_NOT_DISPLAY_STYLE));
        textAria.setOutputMarkupId(true);
        return textAria;
    }

    private AjaxFallbackLink<Object> initEditLink(String wicketId,
            final Label label, final TextArea<String> textAria,
            final AjaxSubmitLink ajaxSubmitLink) {
        AjaxFallbackLink<Object> link = new AjaxFallbackLink<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                label.add(new AttributeModifier("style",
                        DO_NOT_DISPLAY_STYLE));
                textAria.add(new AttributeModifier("style", ""));
                textAria.setModel(new PropertyModel<String>(CommentPanel.this,
                        "comment.comment"));
                ajaxSubmitLink.add(new AttributeModifier("style", ""));
                target.add(ajaxSubmitLink);
                target.add(textAria);
                target.add(label);
                edit = true;
            }
        };

        link.setVisible(MyFoodSession.get().isAuthenticated()
                && MyFoodSession.get().getUser().getUserId() == comment
                        .getUser().getId());
        return link;
    }

    private Link<Object> initDeleteLink(String wicketId, 
            final Comment comment) {
        Link<Object> link = new Link<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                commentService.remove(comment);
                setResponsePage(EventPage.class, 
                        EventPage.parametersWith(event.getEventId()));
            }
        };

        link.setVisible(MyFoodSession.get().isAuthenticated()
                && MyFoodSession.get().getUser().getUserId() == comment
                        .getUser().getId());
        return link;
    }

    private Label initTime(String wicketId, Comment comment) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(comment.getCommentDate());
        return new Label(wicketId, String.format(FORMAT_ATTRIBUTE,
                calendar.get(Calendar.HOUR_OF_DAY))
                + ":" + String.format(FORMAT_ATTRIBUTE,
                        calendar.get(Calendar.MINUTE)));
    }

    private Link<Object> initAuthor(String wicketId, Comment comment) {
        Link<Object> linkToProfile = initLinkToProfile("linkToProfile", comment
                .getUser().getId());
        linkToProfile.add(new Label(wicketId, comment.getUser().getName()));
        return linkToProfile;
    }

    private Link<Object> initLinkToProfile(String wicketId, final Long userId) {
        return new Link<Object>(wicketId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(UserProfilePage.class,
                        UserProfilePage.parametersWith(userId));
            }

        };
    }

    private Label initDate(String wicketId, Comment comment) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(comment.getCommentDate());

        return new Label(wicketId, calendar.get(Calendar.YEAR) + "-"
                + (String.format(FORMAT_ATTRIBUTE, 
                        calendar.get(Calendar.MONTH) + 1))
                + "-" + String.format(FORMAT_ATTRIBUTE, 
                        calendar.get(Calendar.DATE)));
    }

    private Label initComment(String wicketId, Comment comment) {
        Label label = new Label(wicketId, comment.getComment());
        label.setOutputMarkupId(true);
        return label;
    }
}