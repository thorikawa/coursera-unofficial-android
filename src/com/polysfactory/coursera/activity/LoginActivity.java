
package com.polysfactory.coursera.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.polysfactory.coursera.R;
import com.polysfactory.coursera.auth.AccountConstants;

public class LoginActivity extends AccountAuthenticatorActivity {

    private static final String LOGIN_URL = "https://www.coursera.org/maestro/api/user/login";

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button okButton = (Button) findViewById(R.id.button_ok);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View button) {
                Log.d(TAG, "onClick");
                EditText usernameText = (EditText) findViewById(R.id.txt_username);
                EditText passwordText = (EditText) findViewById(R.id.txt_password);
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                LoginTask loginTask = new LoginTask(username, password);
                loginTask.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    class LoginTask extends AsyncTask<Void, Void, Boolean> {
        private String mUsername;
        private String mPassword;

        public LoginTask(String username, String password) {
            this.mUsername = username;
            this.mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(LOGIN_URL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email_address", mUsername));
            params.add(new BasicNameValuePair("password", mPassword));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
            } catch (UnsupportedEncodingException e) {
                // TODO
                Log.w(TAG, e);
            }
            HttpResponse httpResponse = null;
            try {
                httpResponse = httpClient.execute(httpPost);
            } catch (ClientProtocolException e) {
                Log.w(TAG, e);
            } catch (IOException e) {
                Log.w(TAG, e);
            }
            String token = null;
            if (httpResponse != null) {
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    token = httpResponse.getFirstHeader("Set-Cookie").getValue();
                } else {
                    // TODO
                    Log.i(TAG, "authentication error");
                }
            }
            if (token == null) {
                return false;
            }
            AccountManager am = AccountManager.get(LoginActivity.this);
            Account account = new Account(mUsername, AccountConstants.ACCOUNT_TYPE_COURSERA);
            am.addAccountExplicitly(account, mPassword, null);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result) {
                LoginActivity.this.finish();
            }
        }

    }
}
