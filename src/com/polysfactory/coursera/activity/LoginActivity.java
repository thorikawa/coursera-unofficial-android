
package com.polysfactory.coursera.activity;

import java.lang.ref.SoftReference;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.polysfactory.coursera.R;
import com.polysfactory.coursera.auth.AccountConstants;

public class LoginActivity extends AccountAuthenticatorActivity {

    private static final int MSG_CODE_MONITOR_WEWBVIEW = 0;

    private static final String SIGNIN_URL = "https://www.coursera.org/account/signin";

    private static final String HOME_URL = "https://www.coursera.org/";

    private static final String TAG = "LoginActivity";

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mWebView = (WebView) findViewById(R.id.login_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(SIGNIN_URL);
        mWebView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d(TAG, cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });

        Button okButton = (Button) findViewById(R.id.button_ok);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View button) {
                Log.d(TAG, "onClick");
                EditText usernameText = (EditText) findViewById(R.id.txt_username);
                EditText passwordText = (EditText) findViewById(R.id.txt_password);
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                LoginTask loginTask = new LoginTask(LoginActivity.this.getApplicationContext(),
                        username, password, mWebView, mLoginTaskCallbackListener);
                loginTask.login();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    LoginTaskCallbackListener mLoginTaskCallbackListener = new LoginTaskCallbackListener() {
        @Override
        public void onFinished(Bundle result) {
            if (result != null) {
                if (result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT)) {
                    // authentication succeeded
                    LoginActivity.this.setAccountAuthenticatorResult(result);
                    LoginActivity.this.finish();
                } else {
                    // authentication error
                    TextView errorText = (TextView) findViewById(R.id.error_message);
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText(result.getString(AccountManager.KEY_AUTH_FAILED_MESSAGE));
                }
            }
        }
    };

    static interface LoginTaskCallbackListener {
        void onFinished(Bundle result);
    }

    static class LoginTask extends Handler {
        private Context mContext;
        private String mUsername;
        private String mPassword;
        private SoftReference<WebView> mWebViewRef;
        private SoftReference<LoginTaskCallbackListener> mLoginTaskCallbackListenerRef;

        public LoginTask(Context context, String username, String password, WebView webView,
                LoginTaskCallbackListener loginTaskCallbackListener) {
            mContext = context;
            mUsername = username;
            mPassword = password;
            mWebViewRef = new SoftReference<WebView>(webView);
            mLoginTaskCallbackListenerRef = new SoftReference<LoginActivity.LoginTaskCallbackListener>(
                    loginTaskCallbackListener);
        }

        public void login() {
            WebView webView = mWebViewRef.get();
            if (webView != null) {
                webView.loadUrl("javascript:document.getElementById('signin-email').value = '"
                        + mUsername + "'");
                webView.loadUrl("javascript:document.getElementById('signin-password').value = '"
                        + mPassword + "'");
                webView.loadUrl("javascript:document.querySelector('.coursera-signin-button').click()");
                this.sendEmptyMessage(MSG_CODE_MONITOR_WEWBVIEW);
            }
        }

        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_CODE_MONITOR_WEWBVIEW) {
                WebView webView = mWebViewRef.get();
                if (webView != null) {
                    final String url = webView.getUrl();
                    Log.d(TAG, "url: " + url);
                    if (url.equals(HOME_URL)) {
                        // logined
                        String cookie = CookieManager.getInstance().getCookie(url);
                        onPostExecute(cookie);
                    } else {
                        this.sendEmptyMessageDelayed(MSG_CODE_MONITOR_WEWBVIEW, 500);
                    }
                }
            }
        }

        private void onPostExecute(String token) {
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, mUsername);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE,
                    AccountConstants.ACCOUNT_TYPE_COURSERA);
            result.putString(AccountManager.KEY_AUTHTOKEN, token);
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);

            AccountManager am = AccountManager.get(mContext);
            Account account = new Account(mUsername,
                    AccountConstants.ACCOUNT_TYPE_COURSERA);
            am.addAccountExplicitly(account, mPassword, result);

            LoginTaskCallbackListener loginTaskCallbackListener = mLoginTaskCallbackListenerRef
                    .get();
            if (loginTaskCallbackListener != null) {
                loginTaskCallbackListener.onFinished(result);
            }
        }
    }
}
