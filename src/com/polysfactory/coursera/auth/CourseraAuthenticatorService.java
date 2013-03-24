
package com.polysfactory.coursera.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CourseraAuthenticatorService extends Service {

    AbstractAccountAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new CourseraAuthenticator(this.getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mAuthenticator.getIBinder();
    }

}
