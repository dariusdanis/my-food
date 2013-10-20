package com.tieto.food.ui.utils;

import org.apache.wicket.util.string.StringValue;

import com.face4j.facebook.Client;
import com.face4j.facebook.Facebook;
import com.face4j.facebook.OAuthAccessToken;
import com.face4j.facebook.entity.User;
import com.face4j.facebook.enums.Display;
import com.face4j.facebook.enums.Permission;
import com.face4j.facebook.exception.FacebookException;
import com.face4j.facebook.factory.FacebookFactory;

public final class FacebookClient {
    private static final String APP_ID = "619542488079920";
    private static final String APP_SECRET = "bf6477e57188420da4b56df67c1eaf69";
    private static final String CALLBACK_URL = "http://localhost:8080/user/login";
    private static final FacebookFactory FACEBOOK_FACTORY = new FacebookFactory(
            new Client(APP_ID, APP_SECRET));

    private FacebookClient() {
    }

    public static String getRedirectURL() {
        return FACEBOOK_FACTORY.getRedirectURL(CALLBACK_URL, Display.POPUP,
                Permission.EMAIL, Permission.OFFLINE_ACCESS);
    }

    private static OAuthAccessToken getOAuthAccessToken(StringValue c)
            throws FacebookException {
        String code = c.toString();
        return FACEBOOK_FACTORY.getOAuthAccessToken(code, CALLBACK_URL);
    }

    public static User getUser(StringValue code) {
        try {
            Facebook facebook = FACEBOOK_FACTORY
                    .getInstance(getOAuthAccessToken(code));
            return facebook.getCurrentUser();
        } catch (FacebookException e) {
            return null;
        }
    }
}