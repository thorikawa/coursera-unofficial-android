
package com.polysfactory.coursera.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AuthToken {
    private static final String TAG = "AuthToken";

    private static final String MAESTRO_USER = "maestro_user";

    private static final Pattern COOKIE_SEPARATOR = Pattern.compile(";");

    private static final Pattern COOKIE_KEY_VALUE_SEPARATOR = Pattern.compile("=");

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    private String mCookie;

    private User mUser;

    public AuthToken(String cookie) {
        mCookie = cookie;
        String[] cookieItems = COOKIE_SEPARATOR.split(cookie);
        for (String item : cookieItems) {
            String[] pair = COOKIE_KEY_VALUE_SEPARATOR.split(item, 2);
            if (pair.length < 2) {
                continue;
            }
            String key = pair[0].trim();
            if (key.equals(MAESTRO_USER)) {
                try {
                    String value = URLDecoder.decode(pair[1], "UTF-8");
                    mUser = gson.fromJson(value, User.class);
                    Log.v(TAG, "user parsed");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public User getUser() {
        return mUser;
    }

    public String getCookie() {
        return mCookie;
    }
}
