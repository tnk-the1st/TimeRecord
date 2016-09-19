package jp.co.tennti.timerecord.commonUtils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import jp.co.tennti.timerecord.AsyncTaskUtils.GetJsonAsyncTask;
import jp.co.tennti.timerecord.AsyncTaskUtils.HttpRequestAsyncTask;
import jp.co.tennti.timerecord.AsyncTaskUtils.SetImageAsyncTask;
import jp.co.tennti.timerecord.AsyncTaskUtils.SetNavInfoAsyncTask;

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
    protected static final String KEY_AUTH_ERROR          = "\"code\": 401";

    public GoogleOauth2Utils(Activity activity, AccountManager accountManager) {
        this.activity = activity;
        this.accountManager = accountManager;
    }

    /**
     * メイン処理
     * */
    public void startRequest(String authTokenType) {
        Log.d("startRequest", "リクエスト開始 - リクエスト先:" + authTokenType);
        this.authTokenType = authTokenType;
        if (accountName == null) {
            Log.d("startRequest", "アカウントが選択されていない");
            if (!GeneralUtils.isGoogleInfoFile()) {
                chooseAccount();
                return;
            }
            if (TimeUtils.isBeginningMonth()) {
                continueAccount();
                return;
            }
            getJsonAccount();
            /*if (GeneralUtils.isGoogleInfoFile()) {
                if (TimeUtils.isBeginningMonth()) {
                    continueAccount();
                    return;
                }
                getJsonAccount();
            } else {
                chooseAccount();
            }*/
        } else {
            getAuthToken();
        }
    }
    /**
     * アカウントマネージャーでアカウントを選択する
     * */
    protected void chooseAccount() {
        Log.d("chooseAccount", "AuthToken取得開始（アカウント選択）");
        accountManager.getAuthTokenByFeatures(ACCOUNT_TYPE, authTokenType, null, activity, null, null,
                new AccountManagerCallback<Bundle>() {
                    public void run(AccountManagerFuture<Bundle> future) {
                        onGetAuthToken(future);
                    }
                },
                null);
    }

    /**
     * JSONからGoogle取得情報を取得する
     * */
    protected void getJsonAccount() {
        Log.d("chooseAccount", "JSON取得開始（アカウント）");
        JSONObject jsonGoogleOauth = GeneralUtils.getJsonAuthToken();
        JSONObject jsonGoogleInfo  = GeneralUtils.getJsonGoogleInfo();
        Bitmap bitmap = GeneralUtils.getBitmapFromURL(this.activity);
        String accountNameMail     = "";
        String accountFullName     = "";
        try {
            accountNameMail = jsonGoogleOauth.getString("account_name");
            accountFullName = jsonGoogleInfo.getString("name");
            SetNavInfoAsyncTask sniat = new SetNavInfoAsyncTask(this.activity, accountNameMail,accountFullName, bitmap);
            sniat.execute();
        } catch (JSONException e) {
            Log.e("JSONException", e.toString());
        }
    }

    protected void continueAccount() {
        Log.d("chooseAccount", "AuthToken取得開始（アカウント続行）");
        JSONObject jsonGoogleOauth = GeneralUtils.getJsonAuthToken();
        String accountNameMail = "";
        try {
            accountNameMail = jsonGoogleOauth.getString("account_name");
        } catch (JSONException e) {
            Log.e("JSONException", e.toString());
        }
        accountManager.getAuthToken(new Account(accountNameMail, "com.google"), authTokenType, null,
                activity, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bundle = future.getResult();
                            accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
                            authToken   = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                            if (authTokenType.equals(AUTH_TOKEN_TYPE_PROFILE)) {
                                getUserInfo(); //ユーザー情報取得開始
                            }
                        } catch (OperationCanceledException e) {
                            Log.e("OperationCanceledExc", e.toString());
                        } catch (IOException e) {
                            Log.e("IOException", e.toString());
                        } catch (AuthenticatorException e) {
                            Log.e("AuthenticatorException", e.toString());
                        }
                    }
                }, null);
    }

    /**
     * authTokenを取得する部分
     * */
    protected void onGetAuthToken(AccountManagerFuture<Bundle> future) {
        try {
            Bundle bundle = future.getResult();
            accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
            authToken   = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            if (authToken == null) {
                throw new Exception("authTokenがNULL accountName=" + accountName);
            }
            //GeneralUtils.createJsonAuthTokenSD(accountName,authToken);
            //GeneralUtils.createAuthTokenSD(accountName, activity);
            Log.d("onGetAuthToken", "AuthToken取得完了 accountName=" + accountName + " authToken=" + authToken + " authTokenType=" + authTokenType);
            if (authTokenType.equals(AUTH_TOKEN_TYPE_PROFILE)) {
                getUserInfo(); //ユーザー情報取得開始
            }
        } catch (OperationCanceledException e) {
            Log.e("onGetAuthTokenException", "AuthToken取得キャンセル",e);
        } catch (Exception e) {
            Log.e("onGetAuthToken", "AuthToken取得失敗", e);
        }
    }

    /**
     * googleからユーザー情報を取得する部分
     * */
    public void getUserInfo() {
        String authTokenStr = authToken;

        Log.d("getUserInfo", "ユーザー情報取得開始");

        String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + authToken + "&key=" + API_KEY;
        GetJsonAsyncTask task = new GetJsonAsyncTask();
        task.setListener(new GetJsonAsyncTask.OnResultEventListener() {
            @Override
            public void onResult(JSONObject json) {
                String msg = "";
                String pictureUrl = "";
                String fullName   = "";
                try {
                    if (json == null) {
                        msg = "ユーザー情報取得失敗";
                        Log.d("getUserInfo", msg);
                    } else if (json.toString().contains(KEY_AUTH_ERROR)) {
                        msg = "ユーザー情報取得失敗（認証エラー）";
                        accountManager.invalidateAuthToken(ACCOUNT_TYPE, authToken);
                        Log.d("getUserInfo", msg + " AuthTokenを破棄して再取得");
                        startRequest(AUTH_TOKEN_TYPE_PROFILE);
                    } else {
                        msg = "ユーザー情報取得成功\njson=" + json.toString();
                        Log.d("getUserInfo", msg);
                        pictureUrl = json.getString("picture");
                        fullName   = json.getString("name");
                    }

                    //取得データをjsonとして保存する
                    GeneralUtils.createJsonAuthTokenSD(accountName, authToken);
                    GeneralUtils.createJsonGoogleOauthInfoSD(json);
                    SetImageAsyncTask siat = new SetImageAsyncTask(json.getString("picture"));
                    siat.execute();
                    HttpRequestAsyncTask hrat = new HttpRequestAsyncTask(activity , pictureUrl , fullName , accountName);
                    hrat.execute();
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
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
            Log.d("getAuthToken", "アカウントが削除されている");
            chooseAccount();
            return;
        }
        Log.d("getAuthToken", "AuthToken取得開始");
    }
}
