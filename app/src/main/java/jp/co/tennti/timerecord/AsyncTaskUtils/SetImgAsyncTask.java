package jp.co.tennti.timerecord.AsyncTaskUtils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import jp.co.tennti.timerecord.contacts.Constants;

/**
 * Created by TENNTI on 2016/09/16.
 */
public class SetImgAsyncTask extends AsyncTask<Uri.Builder, Void, Bitmap> {
    private String fileUrl;

    public SetImgAsyncTask(String fileUrl){
        this.fileUrl = fileUrl;
    }

    @Override
    protected Bitmap doInBackground(Uri.Builder... builder){
        // 受け取ったbuilderでインターネット通信する
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        Bitmap bitmap = null;

        try{
            URL url = new URL(this.fileUrl);//builder[0].toString());
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            inputStream = connection.getInputStream();
            //Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
            File saveFile                  = new File( Constants.GOOGLE_INFO_DIR + Constants.GOOGLE_USER_ICON_IMG);
            FileOutputStream fileOutStream = new FileOutputStream(saveFile);
            //myBitmap.compress(Bitmap.CompressFormat.PNG, 50, fileOutStream);
            int c;
            while((c =inputStream.read()) != -1) fileOutStream.write((byte) c);
            fileOutStream.flush();
            fileOutStream.close();
            inputStream.close();
        } catch (MalformedURLException e){
            Log.e("MalformedURLException", e.toString());
        } catch (IOException e){
            Log.e("IOException", e.toString());
        } finally {
            if (connection != null){
                connection.disconnect();
            }
            try {
                if (inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e){
                Log.e("IOException_f", e.toString());
            }
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result){
        // インターネット通信して取得した画像をImageViewにセットする
        //this.imageView.setImageBitmap(result);
    }
}
