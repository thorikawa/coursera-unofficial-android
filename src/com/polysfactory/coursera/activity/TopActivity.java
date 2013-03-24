
package com.polysfactory.coursera.activity;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.polysfactory.coursera.auth.AccountConstants;

public class TopActivity extends Activity implements AccountManagerCallback<Bundle> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // login check
        final AccountManager accountManager = AccountManager.get(this.getApplicationContext());
        Account[] accounts = accountManager
                .getAccountsByType(AccountConstants.ACCOUNT_TYPE_COURSERA);
        if (accounts.length == 0) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        Bundle result = accountManager.addAccount(
                                AccountConstants.ACCOUNT_TYPE_COURSERA,
                                null,
                                null, null,
                                TopActivity.this, TopActivity.this, null).getResult();
                        if (result.containsKey(AccountManager.KEY_INTENT)) {
                            TopActivity.this.startActivity((Intent) result
                                    .get(AccountManager.KEY_INTENT));
                        }
                    } catch (OperationCanceledException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (AuthenticatorException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        }
    }

    @Override
    public void run(AccountManagerFuture<Bundle> arg0) {
        // TODO Auto-generated method stub

    }
}
