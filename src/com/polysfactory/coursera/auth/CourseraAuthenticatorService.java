
package com.polysfactory.coursera.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CourseraAuthenticatorService extends Service {

    AbstractAccountAuthenticator mAuthenticator;

    private static final String TAG = "CourseraAuthenticatorService";

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new CourseraAuthenticator(this.getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "onBind");
        return mAuthenticator.getIBinder();
    }

}
