package com.tieto.food.ui.utils;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.BasePage;

public final class EmailClient {
    private static final EmailClient EMAIL_CLIENT = new EmailClient();
    private static final String FROM = "myfoodred2@gmail.com";
    private static final String HOST = "smtp.gmail.com";
    private static final String PASSWORD = "tietomyfood";
    private static final String PORT = "587";
    private static final String JOINED = "joined";
    private static final String LEFT = "left";
    private static final String TRUE = "true";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private Session session;
    private Transport transport;
    private Pattern pattern;

    @SpringBean
    private UserService userService;

    @SuppressWarnings("all") 
    private EmailClient() {
        Injector.get().inject(this);
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", HOST);
        properties.setProperty("mail.smtp.port", PORT);
        properties.setProperty("mail.smtp.auth", TRUE);
        properties.setProperty("mail.smtp.ssl.enable", TRUE);
        properties.setProperty("mail.smtp.tls.enable", TRUE);
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.user", FROM);
        properties.setProperty("mail.smtp.password", PASSWORD);
        properties.put("mail.smtp.starttls.enable", TRUE);
        session = Session.getDefaultInstance(properties);
        try {
            transport = session.getTransport("smtp");
            transport.connect(HOST, FROM, PASSWORD);
        } catch (MessagingException e) {
            // Do nothing
        }
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    @SuppressWarnings("all") 
    public synchronized Thread sendNewEventNotificationEmail(
            final List<User> recipients, final Event event) {
        Thread thread =  new Thread() {
            @Override
            public void run() {
                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(FROM));
                    message.setSubject("New MyFood event (" + (new Date())
                            + ")");
                    InternetAddress[] address = new InternetAddress[1];
                    User eventAuthor = userService.loadById(event
                            .getCreatedBy());
                    String description;
                    if (event.getDescription() != null) {
                        description = event.getDescription();
                    } else {
                        description = "(no description)";
                    }
                    for (User recipient : recipients) {
                        message.setRecipient(Message.RecipientType.TO,
                                new InternetAddress(recipient.getEmail()));
                        message.setContent(
                                "<html><font size=\"3\"><body><p><strong>"
                                        + "Hello, "
                                        + recipient.getName()
                                        + " !</strong></p><p>"
                                        + eventAuthor.getName()
                                        + " "
                                        + eventAuthor.getSurname()
                                        + " has created a new event that "
                                        + "might "
                                        + "interest you !<br><br>"
                                        + event.getTitle()
                                        + " @ "
                                        + event.getEventPlace().getPlace()
                                        + " ("
                                        + event.getEventType().getType()
                                        + ")<br>"
                                        + "Description: "
                                        + description
                                        + "<br>"
                                        + "It starts at: "
                                        + event.getEventDate().toString()
                                                .substring(0, 16)
                                        + "<br><br><a href=\""
                                        + BasePage.getAbsolutePath()
                                        + "event/"
                                        + event.getEventId()
                                        + "\">"
                                        + "Check it out!</a></p>"
                                        + "</body></font></html>", "text/html");
                        address[0] = new InternetAddress(recipient.getEmail());
                        if (!transport.isConnected()) {
                            transport.connect(HOST, FROM, PASSWORD);
                        }
                        transport.sendMessage(message, address);
                    }
                } catch (MessagingException mex) {
                    if (!transport.isConnected()) {
                        try {
                            transport.connect(HOST, FROM, PASSWORD);
                        } catch (MessagingException e) {
                            // Do nothing
                        }
                    }
                }
            }
        };
        thread.start();
        return thread;
    }

    /**
     * 
     * @param event
     * @param participant
     *            User who joined the event
     * @param notificationType
     *            0 - if someone left, any number if someone joined
     */
    @SuppressWarnings("all") 
    public synchronized Thread sendNotificationAboutUserEventEmail(
            final Event event, final User participant,
            final int notificationType) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    String notificationTypeStr;
                    if (notificationType == 0) {
                        notificationTypeStr = LEFT;
                    } else {
                        notificationTypeStr = JOINED;
                    }
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(FROM));
                    message.setSubject("Your MyFood event notification ("
                            + (new Date()) + ")");
                    InternetAddress[] address = new InternetAddress[1];
                    User recipient = userService.loadById(event.getCreatedBy());
                    message.setRecipient(Message.RecipientType.TO,
                            new InternetAddress(recipient.getEmail()));
                    String description;
                    if (event.getDescription() != null) {
                        description = event.getDescription();
                    } else {
                        description = "(no description)";
                    }
                    message.setContent(
                            "<html><font size=\"3\"><body><p><strong>Hello, "
                                    + recipient.getName()
                                    + " !</strong></p><p>"
                                    + participant.getName()
                                    + " "
                                    + participant.getSurname()
                                    + " has "
                                    + notificationTypeStr
                                    + " your event !<br><br>"
                                    + event.getTitle()
                                    + " @ "
                                    + event.getEventPlace().getPlace()
                                    + " ("
                                    + event.getEventType().getType()
                                    + ")<br>"
                                    + "Description: "
                                    + description
                                    + "<br>"
                                    + "It starts at: "
                                    + event.getEventDate().toString()
                                            .substring(0, 16)
                                    + "<br><br><a href=\""
                                    + BasePage.getAbsolutePath() + "event/"
                                    + event.getEventId() + "\">"
                                    + "Check your event !</a></p>"
                                    + "</body></font></html>", "text/html");
                    address[0] = new InternetAddress(recipient.getEmail());
                    if (!transport.isConnected()) {
                        transport.connect(HOST, FROM, PASSWORD);
                    }
                    transport.sendMessage(message, address);
                } catch (MessagingException mex) {
                    // Do nothing
                }
            }
        };
        thread.start();
        return thread;
    }

    @SuppressWarnings("all") 
    public synchronized Thread sendUpcomingEventNotificationEmail(
            final List<User> recipients, final Event event) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(FROM));
                    message.setSubject("MyFood reminder");
                    InternetAddress[] address = new InternetAddress[1];
                    for (User recipient : recipients) {
                        message.setRecipient(Message.RecipientType.TO,
                                new InternetAddress(recipient.getEmail()));
                        message.setContent(
                                "<html><font size=\"3\"><body>"
                                        + "<p><strong>Hello, "
                                        + recipient.getName()
                                        + " !</strong></p><p>"
                                        + " The event - "
                                        + event.getTitle()
                                        + " , that you are participating "
                                        + "in is starting in a few hours"
                                        + " @ "
                                        + event.getEventPlace().getPlace()
                                        + " ("
                                        + event.getEventType().getType()
                                        + ") <br/>Event starts at: "
                                        + event.getEventDate().toString()
                                                .substring(0, 16)
                                        + " try not to be late and have "
                                        + "fun! <br/>"
                                        + "Best wishes! <br/> -- <br/> "
                                        + "MyFood team."
                                        + "<br><br><a href=\""
                                        + BasePage.getAbsolutePath()
                                        + "event/"
                                        + event.getEventId()
                                        + "\">"
                                        + "Go to event page.</a></p>"
                                        + "</body></font></html>", "text/html");
                        address[0] = new InternetAddress(recipient.getEmail());
                        if (!transport.isConnected()) {
                            transport.connect(HOST, FROM, PASSWORD);
                        }
                        transport.sendMessage(message, address);
                    }
                } catch (MessagingException mex) {
                    if (!transport.isConnected()) {
                        try {
                            transport.connect(HOST, FROM, PASSWORD);
                        } catch (MessagingException e) {
                            // Do nothing
                        }
                    }
                }
            }
        };
        thread.start();
        return thread;
    }

    @SuppressWarnings("all") 
    public synchronized Thread sendRemoveParticipantEmail(final User recipient,
            final Event event) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(FROM));
                    message.setSubject("MyFood event notification ("
                            + (new Date()) + ")");
                    InternetAddress[] address = new InternetAddress[1];
                    String description;
                    if (event.getDescription() != null) {
                        description = event.getDescription();
                    } else {
                        description = "(no description)";
                    }
                    message.setRecipient(Message.RecipientType.TO,
                            new InternetAddress(recipient.getEmail()));
                    message.setContent(
                            "<html><font size=\"3\"><body><p><strong>Hello, "
                                    + recipient.getName()
                                    + " !</strong></p><p>"
                                    + " You have been removed from event: <br>"
                                    + event.getTitle()
                                    + " @ "
                                    + event.getEventPlace().getPlace()
                                    + " ("
                                    + event.getEventType().getType()
                                    + ")<br>"
                                    + "Description: "
                                    + description
                                    + "<br>"
                                    + "It starts at: "
                                    + event.getEventDate().toString()
                                            .substring(0, 16), "text/html");
                    address[0] = new InternetAddress(recipient.getEmail());
                    if (!transport.isConnected()) {
                        transport.connect(HOST, FROM, PASSWORD);
                    }
                    transport.sendMessage(message, address);
                } catch (MessagingException mex) {
                    if (!transport.isConnected()) {
                        try {
                            transport.connect(HOST, FROM, PASSWORD);
                        } catch (MessagingException e) {
                            // Do nothing
                        }
                    }
                }
            }
        };
        thread.start();
        return thread;
    }

    @SuppressWarnings("all") 
    public synchronized Thread sendEventBlockedEmail(final User recipient,
            final Event event) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(FROM));
                    message.setSubject("MyFood event notification ("
                            + (new Date()) + ")");
                    InternetAddress[] address = new InternetAddress[1];
                    String description;
                    if (event.getDescription() != null) {
                        description = event.getDescription();
                    } else {
                        description = "(no description)";
                    }
                    message.setRecipient(Message.RecipientType.TO,
                            new InternetAddress(recipient.getEmail()));
                    message.setContent(
                            "<html><font size=\"3\"><body><p><strong>Hello, "
                                    + recipient.getName()
                                    + " !</strong></p><p>"
                                    + " We are very sorry but the following"
                                    + " event: <br>" + event.getTitle() + " @ "
                                    + event.getEventPlace().getPlace() + " ("
                                    + event.getEventType().getType() + ")<br>"
                                    + "Description: " + description
                                    + "<br><br>"
                                    + "has been blocked due to one of the "
                                    + "following reasons: " + "<ul>"
                                    + "<li>It may have inappropriate content"
                                    + "</li>"
                                    + "<li>It might be an advertisement</li>"
                                    + "<li>It might seem to be insulting</li>"
                                    + "</ul><br />"
                                    + "Excuse us for any inconvenience caused.",
                            "text/html");
                    address[0] = new InternetAddress(recipient.getEmail());
                    if (!transport.isConnected()) {
                        transport.connect(HOST, FROM, PASSWORD);
                    }
                    transport.sendMessage(message, address);
                } catch (MessagingException mex) {
                    if (!transport.isConnected()) {
                        try {
                            transport.connect(HOST, FROM, PASSWORD);
                        } catch (MessagingException e) {
                            // Do nothing
                        }
                    }
                }
            }
        };
        thread.start();
        return thread;
    }

    @SuppressWarnings("all") 
    public synchronized Thread sendEventAllowedEmail(final User recipient,
            final Event event) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(FROM));
                    message.setSubject("MyFood event notification ("
                            + (new Date()) + ")");
                    InternetAddress[] address = new InternetAddress[1];
                    String description;
                    if (event.getDescription() != null) {
                        description = event.getDescription();
                    } else {
                        description = "(no description)";
                    }
                    message.setRecipient(Message.RecipientType.TO,
                            new InternetAddress(recipient.getEmail()));
                    message.setContent(
                            "<html><font size=\"3\"><body><p><strong>Hello, "
                                    + recipient.getName()
                                    + " !</strong></p><p>"
                                    + " We would like to inform you that the "
                                    + "following event: <br>"
                                    + event.getTitle() + " @ "
                                    + event.getEventPlace().getPlace() + " ("
                                    + event.getEventType().getType() + ")<br>"
                                    + "Description: " + description
                                    + "<br><br>"
                                    + "has been allowed again!<br /><br />"
                                    + "Have a nice day.", "text/html");
                    address[0] = new InternetAddress(recipient.getEmail());
                    if (!transport.isConnected()) {
                        transport.connect(HOST, FROM, PASSWORD);
                    }
                    transport.sendMessage(message, address);
                } catch (MessagingException mex) {
                    if (!transport.isConnected()) {
                        try {
                            transport.connect(HOST, FROM, PASSWORD);
                        } catch (MessagingException e) {
                            // Do nothing
                        }
                    }
                }
            }
        };
        thread.start();
        return thread;
    }

    @SuppressWarnings("all") 
    public synchronized Thread sendInvitationEmail(final User recipient,
            final Event event) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(FROM));
                    message.setSubject("MyFood event invitation ("
                            + (new Date()) + ")");
                    InternetAddress[] address = new InternetAddress[1];
                    String description;
                    if (event.getDescription() != null) {
                        description = event.getDescription();
                    } else {
                        description = "(no description)";
                    }
                    User eventAuthor = userService.loadById(event
                            .getCreatedBy());
                    message.setRecipient(Message.RecipientType.TO,
                            new InternetAddress(recipient.getEmail()));
                    message.setContent(
                            "<html><font size=\"3\"><body><p><strong>Hello, "
                                    + recipient.getName()
                                    + " !</strong></p><p>"
                                    + eventAuthor.getName()
                                    + " "
                                    + eventAuthor.getSurname()
                                    + " has invited you to his "
                                    + "MyFood event !<br><br>"
                                    + event.getTitle()
                                    + " @ "
                                    + event.getEventPlace().getPlace()
                                    + " ("
                                    + event.getEventType().getType()
                                    + ")<br>"
                                    + "Description: "
                                    + description
                                    + "<br>"
                                    + "It starts at: "
                                    + event.getEventDate().toString()
                                            .substring(0, 16)
                                    + "<br><br><a href=\""
                                    + BasePage.getAbsolutePath() + "event/"
                                    + event.getEventId() + "\">"
                                    + "Join now!</a></p></body></font></html>",
                            "text/html");
                    address[0] = new InternetAddress(recipient.getEmail());
                    if (!transport.isConnected()) {
                        transport.connect(HOST, FROM, PASSWORD);
                    }
                    transport.sendMessage(message, address);
                } catch (MessagingException mex) {
                    if (!transport.isConnected()) {
                        try {
                            transport.connect(HOST, FROM, PASSWORD);
                        } catch (MessagingException e) {
                            // Do nothing
                        }
                    }
                }
            }
        };
        thread.start();
        return thread;
    }

    @SuppressWarnings("all") 
    public synchronized Thread sendInvitationEmail(final String email,
            final Event event) {
        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(FROM));
                    message.setSubject("MyFood event invitation ("
                            + (new Date()) + ")");
                    InternetAddress[] address = new InternetAddress[1];
                    String description;
                    if (event.getDescription() != null) {
                        description = event.getDescription();
                    } else {
                        description = "(no description)";
                    }
                    User eventAuthor = userService.loadById(event
                            .getCreatedBy());
                    message.setRecipient(Message.RecipientType.TO,
                            new InternetAddress(email));
                    message.setContent(
                            "<html><font size=\"3\"><body><p><strong>Hello"
                                    + " !</strong></p><p>"
                                    + eventAuthor.getName()
                                    + " "
                                    + eventAuthor.getSurname()
                                    + " has invited you to his MyFood "
                                    + "event !<br><br>"
                                    + event.getTitle()
                                    + " @ "
                                    + event.getEventPlace().getPlace()
                                    + " ("
                                    + event.getEventType().getType()
                                    + ")<br>"
                                    + "Description: "
                                    + description
                                    + "<br>"
                                    + "It starts at: "
                                    + event.getEventDate().toString()
                                            .substring(0, 16)
                                    + "<br><br><a href=\""
                                    + BasePage.getAbsolutePath()
                                    + "event/"
                                    + event.getEventId()
                                    + "\">"
                                    + "Join now!</a></p></body></font>"
                                    + "</html>", "text/html");
                    address[0] = new InternetAddress(email);
                    if (!transport.isConnected()) {
                        transport.connect(HOST, FROM, PASSWORD);
                    }
                    transport.sendMessage(message, address);
                } catch (MessagingException mex) {
                    if (!transport.isConnected()) {
                        try {
                            transport.connect(HOST, FROM, PASSWORD);
                        } catch (MessagingException e) {
                            // Do nothing
                        }
                    }
                }
            }
        };
        thread.start();
        return thread;
    }

    @SuppressWarnings("all") 
    public boolean validateAndSendInvitations(String users, Event event) {
        if (users != null) {
            users = users.replace("[", "");
            users = users.replace("]", "");
            users = users.replaceAll("\"", "");
            if (users.trim().length() != 0) {
                String[] stringIds = users.split(",");
                int[] ids = new int[stringIds.length];
                String[] emails = new String[stringIds.length];
                int j = 0, k = 0;
                for (int i = 0; i < stringIds.length; i++) {
                    if (stringIds[i].length() != 0) {
                        try {
                            ids[j] = Integer.valueOf(stringIds[i]);
                            userService.listOnlyExisting().get(ids[j]);
                            j++;
                        } catch (IndexOutOfBoundsException ex1) {
                            return false;
                        } catch (NumberFormatException ex2) {
                            if (validateEmail(stringIds[i])) {
                                emails[k] = stringIds[i];
                                k++;
                            } else {
                                return false;
                            }
                        }
                    }
                }
                for (int i = 0; i < j; i++) {
                    sendInvitationEmail(
                            userService.listOnlyExisting().get(ids[i]), event);
                }
                for (int i = 0; i < k; i++) {
                    sendInvitationEmail(emails[i], event);
                }
                return true;
            }
        }
        return false;
    }

    public boolean validateEmail(final String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void finalize() throws Throwable {
        if (transport.isConnected()) {
            try {
                transport.close();
            } catch (MessagingException e) {
                // Do nothing
            }
        }
        super.finalize();
    }

    public static EmailClient getInstanceOfEmailClient() {
        return EMAIL_CLIENT;
    }
}