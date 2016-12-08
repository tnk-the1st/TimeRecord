package jp.co.tennti.timerecord.AsyncTaskUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.co.tennti.timerecord.R;
import jp.co.tennti.timerecord.commonUtils.BitmapUtils;

/**
 *  URL to Bitmap用の非同期処理
 *  非同期である必要があるためAsyncTaskを使用している
 * Created by TENNTI on 2016/08/14.
 */
public class HttpRequestAsyncTask extends AsyncTask<Uri.Builder, Void, String> {

    private Activity mainActivity;
    String pictureUrl;
    Bitmap bitmap;
    String userName;
    String accountName;
    /**
     * @param activity
     * @param pictureUrl  認証したアカウントの画像
     * @param userName    認証したアカウントの名前
     * @param accountName 認証したアカウントID->メールアドレス
     * */
    public HttpRequestAsyncTask(Activity activity, String pictureUrl, String userName, String accountName) {
        this.mainActivity = activity;
        this.pictureUrl   = pictureUrl;
        this.userName     = userName;
        this.accountName  = accountName;
    }

    /**
     * このメソッドは必ずオーバーライドする必要がある
     * 非同期で処理される部分
     * */
    @Override
    protected String doInBackground(Uri.Builder... builder) {
        HttpURLConnection connection = null;
        InputStream inputStream      = null;
        try {
            URL url = new URL(pictureUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            Log.e("IOException : " , "URL to Bitmap処理の変換に失敗", e);
        } finally {
            if (connection != null){
                connection.disconnect();
            }
            try {
                if (inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e){
                Log.e("IOException", e.toString());
            }
        }
        return userName;
    }


    /**
     *このメソッドは非同期処理の終わった後に呼び出されます
     *取得した結果をイメージビューに入れる
     * */
    @Override
    protected void onPostExecute(String result) {
        ImageView accountIconView = (ImageView)mainActivity.findViewById(R.id.accountIconView);
        TextView accountName      = (TextView)mainActivity.findViewById(R.id.accountName);
        TextView accountMail      = (TextView)mainActivity.findViewById(R.id.accountMail);
        if(accountIconView != null){
            accountIconView.setScaleType(ImageView.ScaleType.FIT_START);
            accountIconView.setImageBitmap(BitmapUtils.toRoundBitmap(this.bitmap));
        }
        accountName.setText(result);
        accountMail.setText(this.accountName);
    }
}