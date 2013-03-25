
package com.polysfactory.coursera.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.polysfactory.coursera.activity.LoginActivity;

public class CourseraAuthenticator extends AbstractAccountAuthenticator {

    Context mContext;

    private static final String TAG = "CourseraAuthenticator";

    private AccountManager mAccountManager;

    public CourseraAuthenticator(Context context) {
        super(context);
        this.mContext = context;
        this.mAccountManager = AccountManager.get(mContext);
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
            String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        // TODO Auto-generated method stub
        Log.d(TAG, "addAccount");
        final Bundle result;
        final Intent intent;

        intent = new Intent(this.mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        result = new Bundle();
        result.putParcelable(AccountManager.KEY_INTENT, intent);
        return result;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse arg0, Account arg1, Bundle arg2)
            throws NetworkErrorException {
        // TODO Auto-generated method stub
        Log.d(TAG, "confirmCredentials");
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
        String token = mAccountManager.getUserData(account, AccountManager.KEY_AUTHTOKEN);
        Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE,
                AccountConstants.ACCOUNT_TYPE_COURSERA);
        result.putString(AccountManager.KEY_AUTHTOKEN, token);
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
