
package com.polysfactory.coursera;

import android.app.Application;

import com.polysfactory.coursera.model.AuthToken;

public class CourseraApplication extends Application {

    private AuthToken mAuthToken;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    public AuthToken getAuthToken() {
        return mAuthToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.mAuthToken = authToken;
    }

}
