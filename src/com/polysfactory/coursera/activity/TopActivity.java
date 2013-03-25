
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
import android.util.Log;
import android.widget.ListView;

import com.polysfactory.coursera.R;
import com.polysfactory.coursera.adapter.MyCourseListAdapter;
import com.polysfactory.coursera.api.LoadCourseListTask;
import com.polysfactory.coursera.api.LoadCourseListTask.CallbackListner;
import com.polysfactory.coursera.auth.AccountConstants;
import com.polysfactory.coursera.model.AuthToken;
import com.polysfactory.coursera.model.Course;

public class TopActivity extends Activity implements AccountManagerCallback<Bundle> {

    String mToken;

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_course_list);

        mListView = (ListView) findViewById(R.id.my_course_list);

        // login check
        final AccountManager accountManager = AccountManager.get(this.getApplicationContext());
        Account[] accounts = accountManager
                .getAccountsByType(AccountConstants.ACCOUNT_TYPE_COURSERA);
        int numAccounts = accounts.length;
        if (numAccounts == 0) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        Bundle result = accountManager.addAccount(
                                AccountConstants.ACCOUNT_TYPE_COURSERA, null, null, null,
                                TopActivity.this, TopActivity.this, null).getResult();
                        if (result.containsKey(AccountManager.KEY_INTENT)) {
                            Log.v("TopActivity", "start activity");
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
        } else if (numAccounts > 0) {
            Account account = accounts[0];
            accountManager.getAuthToken(account, AccountConstants.ACCOUNT_TYPE_COURSERA, null,
                    false, this, null);
        }
    }

    @Override
    public void run(AccountManagerFuture<Bundle> future) {
        try {
            Bundle result = future.getResult();
            mToken = result.getString(AccountManager.KEY_AUTHTOKEN);
            AuthToken authToken = new AuthToken(mToken);
            Log.v("TopActivity", "Token: " + mToken);
            LoadCourseListTask loadCourseListTask = new LoadCourseListTask(authToken,
                    new CallbackListner() {
                        @Override
                        public void onFinish(Course[] courses) {
                            mListView.setAdapter(new MyCourseListAdapter(TopActivity.this
                                    .getApplicationContext(), courses));
                        }
                    });
            loadCourseListTask.execute();
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
}
