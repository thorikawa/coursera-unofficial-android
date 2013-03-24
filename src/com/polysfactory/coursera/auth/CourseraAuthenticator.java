
package com.polysfactory.coursera.auth;

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

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class CourseraAuthenticator extends AbstractAccountAuthenticator {

    Context mContext;

    private static final String LOGIN_URL = "https://www.coursera.org/maestro/api/user/login";

    private static final String TAG = "CourseraAuthenticator";

    private static final String ACCOUNT_TYPE_COURSERA = "com.polysfactory.coursera";

    public CourseraAuthenticator(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse arg0, String arg1, String arg2,
            String[] arg3, Bundle arg4) throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse arg0, Account arg1, Bundle arg2)
            throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse arg0, String arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle options) throws NetworkErrorException {

        Bundle result = null;

        AccountManager accountManager = AccountManager.get(mContext);
        String username = account.name;
        String password = accountManager.getPassword(account);
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(LOGIN_URL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email_address", username));
        params.add(new BasicNameValuePair("password", password));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            // TODO
            Log.w(TAG, e);
        }
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            Log.w(TAG, e);
            throw new NetworkErrorException(e.getMessage());
        } catch (IOException e) {
            Log.w(TAG, e);
            throw new NetworkErrorException(e.getMessage());
        }
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            String token = httpResponse.getFirstHeader("Set-Cookie").getValue();
            result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, username);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE_COURSERA);
            result.putString(AccountManager.KEY_AUTHTOKEN, token);
        } else {
            // TODO
            Log.i(TAG, "authentication error");
        }
        return result;
    }

    @Override
    public String getAuthTokenLabel(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse arg0, Account arg1, String[] arg2)
            throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse arg0, Account arg1, String arg2,
            Bundle arg3) throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }
}
