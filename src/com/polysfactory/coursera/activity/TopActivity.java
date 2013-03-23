
package com.polysfactory.coursera.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.polysfactory.coursera.model.User;

public class TopActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // login check
        User user = new User();
        if (!user.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
