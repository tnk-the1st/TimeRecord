package jp.co.tennti.timerecord.commonUtils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import jp.co.tennti.timerecord.AsyncTaskUtils.AsyncHttpRequest;

/**
 * Created by TENNTI on 2016/08/14.
 */
public class GoogleOauth2Utils {

    public Activity activity;
    protected AccountManager accountManager;
    protected String accountName;
    protected String authToken;
    protected String authTokenType;
    protected static final String AUTH_TOKEN_TYPE_PROFILE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    protected static final String ACCOUNT_TYPE            = "com.google";
    protected static final String API_KEY                 = "USE_YOUR_API_KEY";
    protected static final String KEY_AUTH_ERROR          = "\"code\":401";

    public GoogleOauth2Utils(Activity activity, AccountManager accountManager) {
        this.activity = activity;
        this.accountManager = accountManager;
    }

    /**
     * メイン処理
     * */
    public void startRequest(String authTokenType) {
        Log.v("startRequest", "リクエスト開始 - リクエスト先:" + authTokenType);
        this.authTokenType = authTokenType;
        if (accountName == null) {
            Log.v("startRequest", "アカウントが選択されていない");
            chooseAccount();
        } else {
            getAuthToken();
        }
    }
    /**
     * アカウントマネージャーでアカウントを選択する
     * */
    protected void chooseAccount() {
        Log.v("chooseAccount", "AuthToken取得開始（アカウント選択）");
        accountManager.getAuthTokenByFeatures(ACCOUNT_TYPE, authTokenType, null, activity, null, null,
                new AccountManagerCallback<Bundle>() {
                    public void run(AccountManagerFuture<Bundle> future) {
                        onGetAuthToken(future);
                    }
                },
                null);
    }
    /**
     * authTokenを取得する部分
     * */
    protected void onGetAuthToken(AccountManagerFuture<Bundle> future) {
        try {
            Bundle bundle = future.getResult();
            accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
            authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            if (authToken == null) {
                throw new Exception("authTokenがNULL accountName=" + accountName);
            }
            Log.v("onGetAuthToken", "AuthToken取得完了 accountName=" + accountName + " authToken=" + authToken + " authTokenType=" + authTokenType);
            if (authTokenType.equals(AUTH_TOKEN_TYPE_PROFILE)) {
                getUserInfo(); //ユーザー情報取得開始
            }
        } catch (OperationCanceledException e) {
            Log.v("onGetAuthToken", "AuthToken取得キャンセル");
        } catch (Exception e) {
            Log.v("onGetAuthToken", "AuthToken取得失敗", e);
        }
    }

    /**
     * googleからユーザー情報を取得する部分
     * */
    public void getUserInfo() {
        String s = authToken;

        Log.v("getUserInfo", "ユーザー情報取得開始");
        String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + authToken + "&key=" + API_KEY;

        AsyncTaskGetJson task = new AsyncTaskGetJson();
        task.setListener(new OnResultEventListener() {
            @Override
            public void onResult(JSONObject json) {
                String msg = "";
                if (json == null) {
                    msg = "ユーザー情報取得失敗";
                } else if (json.toString().contains(KEY_AUTH_ERROR)) {
                    msg = "ユーザー情報取得失敗（認証エラー）";
                    accountManager.invalidateAuthToken(ACCOUNT_TYPE, authToken);
                    Log.v("getUserInfo", msg + " AuthTokenを破棄して再取得");
                    startRequest(AUTH_TOKEN_TYPE_PROFILE);
               } else {
                    msg = "ユーザー情報取得成功\njson=" + json.toString();
                    Log.v("getUserInfo", msg);
                }
                try {
                    Log.v("link", json.getString("picture"));
                    AsyncHttpRequest ahr = new AsyncHttpRequest(activity,json.getString("picture"),json.getString("name"),accountName);
                    ahr.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        task.execute(url);
    }


    /**
     * アカウント情報をAuthTokenから判定する
     * */
    protected void getAuthToken() {
        Account account = null;
        Account[] accounts = accountManager.getAccounts();
        for (int i = 0; i < accounts.length; i++) {
            account = accounts[i];
            if (account.name.equals(accountName)) {
                break;
            }
        }
        if (account == null) {
            Log.v("getAuthToken", "アカウントが削除されている");
            chooseAccount();
            return;
        }
        Log.v("getAuthToken", "AuthToken取得開始");
    }

    /**
     * JSONの変換処理AsyncTask
     * */
    public class AsyncTaskGetJson extends AsyncTask<String, Void, JSONObject> {

        protected DefaultHttpClient client;
        protected OnResultEventListener listener;


        @Override
        protected void onPreExecute() {
            //Log.v("onPreExecute", "JSON取得開始");
        }


        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONObject json = null;
            String url = urls[0];
            client = new DefaultHttpClient();
            try {
                HttpGet httpGet = new HttpGet(url);
                HttpResponse res = client.execute(httpGet);
                HttpEntity entity = res.getEntity();
                String result = EntityUtils.toString(entity);
                //Log.v("doInBackground", "result=" + result);
                json = new JSONObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //Log.v("doInBackground", "リクエスト切断");
                client.getConnectionManager().shutdown();
            }
            return json;
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            if (json == null) {
                //Log.v("onPostExecute", "JSON取得失敗 JSONがNULL");
            } else {
                //Log.v("onPostExecute", "JSON取得成功");
            }
            if (listener != null) {
                listener.onResult(json);
            }
        }


        @Override
        protected void onCancelled() {
            //Log.v("onCancelled", "JSON取得キャンセル");
            if (client != null) {
                client.getConnectionManager().shutdown();
            }
            super.onCancelled();
        }


        public void setListener(OnResultEventListener listener) {
            this.listener = listener;
        }



    } // END class TaskGetJson
    public interface OnResultEventListener {
        public void onResult(JSONObject json);
    } // END interface EventListener
}
