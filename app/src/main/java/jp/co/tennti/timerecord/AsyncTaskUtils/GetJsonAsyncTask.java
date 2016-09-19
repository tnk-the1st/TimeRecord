package jp.co.tennti.timerecord.AsyncTaskUtils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * JSONの変換処理AsyncTask
 * */
public class GetJsonAsyncTask extends AsyncTask<String, Void, JSONObject> {

    protected HttpURLConnection connection;
    protected OnResultEventListener listener;
    int status = 0;

    @Override
    protected void onPreExecute() {
        //Log.v("onPreExecute", "JSON取得開始");
    }


    @Override
    protected JSONObject doInBackground(String... urls) {
        String urlOrg = urls[0];
        JSONObject json = null;
        try {
            URL url = new URL(urlOrg);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            status = connection.getResponseCode();

            BufferedInputStream inputStream    = new BufferedInputStream(connection.getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer                      = new byte[256];
            int length;
            while ((length = inputStream.read(buffer)) != -1){
                if (length > 0){
                    outputStream.write(buffer, 0, length);
                }
            }
            json = new JSONObject(new String(outputStream.toByteArray()));
        } catch (MalformedURLException e) {
            Log.e("MalformedURLException", e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        } catch (JSONException e) {
            Log.e("JSONException", e.toString());
        } finally {
            if(connection != null){
                connection.disconnect();
            }
            if (status == HttpURLConnection.HTTP_OK) {
                return json;
            } else {
                Log.e("HttpURLConnection", String.valueOf(status));
            }
        }
        return json;
    }


    @Override
    protected void onPostExecute(JSONObject json) {
        if (json == null) {
            Log.d("onPostExecute", "JSON取得失敗 JSONがNULL");
        }/* else {
            Log.v("onPostExecute", "JSON取得成功");
        }*/
        if (listener != null) {
            listener.onResult(json);
        }
    }


    @Override
    protected void onCancelled() {
        //Log.v("onCancelled", "JSON取得キャンセル");
        if (connection != null) {
            connection.disconnect();
            //client.getConnectionManager().shutdown();
        }
        super.onCancelled();
    }


    public void setListener(OnResultEventListener listener) {
        this.listener = listener;
    }

    public interface OnResultEventListener {
        public void onResult(JSONObject json);
    } // END interface EventListener
} // END class TaskGetJson