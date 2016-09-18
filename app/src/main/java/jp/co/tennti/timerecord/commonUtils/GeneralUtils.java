package jp.co.tennti.timerecord.commonUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.tennti.timerecord.contacts.Constants;

/**
 * Created by TENNTI on 2016/04/20.
 */
public class GeneralUtils {

    /** 認証トークンのフルパス */
    private static final String AUTH_TOKEN_FILE       = Constants.AUTH_TOKEN_DIRECTORY + Constants.OAUTH_TOKEN_FILE_NAME;

    /** 認証トークンJSONのフルパス */
    private static final String AUTH_TOKEN_JSON_FILE  = Constants.GOOGLE_INFO_JSON_DIR + Constants.OAUTH_TOKEN_FILE_JSON;

    /** Google Oauth取得情報JSONのフルパス */
    private static final String GOOGLE_USER_INFO_FILE = Constants.GOOGLE_INFO_JSON_DIR + Constants.GOOGLE_USER_INFO_JSON;
    
    /** Google Oauth取得情報画像のフルパス */
    private static final String GOOGLE_USER_ICON_FILE = Constants.GOOGLE_INFO_DIR + Constants.GOOGLE_USER_ICON_IMG;

    /**
     * SQLエラー時のエラーダイアログ生成メソッド
     * @param fragActivity  フラグメントのアクティビティ
     * @param titleName    ダイアログタイトル
     * @param messageName  表示メッセージ
     * @param buttonName   ボタン名
     */
    public static void createErrorDialog(FragmentActivity fragActivity,String titleName, String messageName, String buttonName) {
        new AlertDialog.Builder(fragActivity)
                .setTitle(titleName)
                .setMessage(messageName)
                .setPositiveButton(buttonName, null)
                .show();
    }

    /**
     * SDCard のファイルを削除する(Android 用)
     * @param  String  fileName ファイル名
     * @return boolean true : ファイル削除成功
     *                 false : SDカードがマウントされていない
     */
    public static final boolean deleteSDCardFile(String fileName) {
        if (!isSDCardMount()) {
            return false;
        }
        File file = new File(toSDCardAbsolutePath(fileName));
        return file.delete();
    }

    /**
     * SDCard のマウント状態をチェックする(Android 用)
     * @return boolean true : マウントされている
     *                 false: マウントされていない
     */
    public static final boolean isSDCardMount() {
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ファイルが存在するか判定する
     * String filepath = this.getFilesDir().getAbsolutePath() + "/" +  "test.txt";
     * @return boolean true isExists(file.exists());
     */
    public static final boolean isFileExist ( String filepath ) {
        File file = new File(filepath);
        //File file = this.getFileStreamPath(filepath);
        return file.exists();
    }

    /**
     * 認証ファイルが存在するか判定する
     * String filepath = this.getFilesDir().getAbsolutePath() + "/" +  "test.txt";
     * @return boolean true isExists(file.exists());
     */
    public static final boolean isAuthFile () {
        File authTokenFile  = new File(AUTH_TOKEN_JSON_FILE);
        File googleUserFile = new File(GOOGLE_USER_INFO_FILE);

        return authTokenFile.exists() && googleUserFile.exists();
    }

    /**
     * Google取得情報ファイルが存在するか判定する
     * String filepath = this.getFilesDir().getAbsolutePath() + "/" +  "test.txt";
     * @return boolean true isExists(file.exists());
     */
    public static final boolean isGoogleInfoFile () {
        File authTokenFile  = new File(AUTH_TOKEN_JSON_FILE);
        File googleUserFile = new File(GOOGLE_USER_INFO_FILE);

        return authTokenFile.exists() && googleUserFile.exists();
    }


    /**
     * SDCard のルートディレクトリを取得(Android 用)
     * @return String ルートディレクトリパス
     */
    public static final File getSDCardDir() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * ファイル名からSDCard 内の絶対パスに変換(Android 用)
     * @param  String fileName ファイル名
     * @return String ファイルまでの絶対パス
     */
    public static final String toSDCardAbsolutePath(String fileName) {
        System.out.println(getSDCardDir().getAbsolutePath());
        return getSDCardDir().getAbsolutePath() + File.separator + fileName;
    }

    /**
     * 月分の空List作成
     * @param  String tarDate yyyy-MM
     * @return List<HashMap<String, String>> blankResultList 空行リスト
     */
    public static final  List<HashMap<String, String>> createblankTable(String tarDate){
        TimeUtils timeUtil = new TimeUtils();
        List<HashMap<String, String>> blankResultList = new ArrayList<HashMap<String, String>>();
        final int MAX_LENGTH_I = 31;
        for (int i = 1; i <= MAX_LENGTH_I; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            StringBuffer buffer_day = new StringBuffer();
            if (i < 10) {
                buffer_day.append("0");
            }
            map.put("basic_date", tarDate + "-" + buffer_day.append(i).toString());
            map.put("leaving_date", "");
            map.put("overtime", Constants.NO_TIME);
            map.put("week", timeUtil.getTargWeekOmit(tarDate + "-" + buffer_day.append(i).toString()));
            map.put("holiday_flag", "");
            blankResultList.add(map);
        }
        return blankResultList;
    }
    /**
     * SDCard にauthTokenを保存する
     * @param  String  accountName 選択ID(アドレス)
     * @param  Context  context コンテキスト情報
     */
    public static final void createAuthTokenSD(String accountName,Context context) {
        try {
/*            if (!new File(Constants.AUTH_TOKEN_DIRECTORY).exists()) {
                new File(Constants.AUTH_TOKEN_DIRECTORY).mkdirs();
            }*/
            File tokenFile = new File( AUTH_TOKEN_FILE );
            OutputStream outStream = new FileOutputStream(tokenFile);
           //FileOutputStream out   = context.openFileOutput(AUTH_TOKEN_FILE, Context.MODE_PRIVATE);//new ObjectOutputStream(context.openFileOutput(AUTH_TOKEN_FILE, Context.MODE_PRIVATE));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outStream,"UTF-8"));
            writer.append(accountName);
            writer.close();
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
    }
    /**
     * SDCard にJSONファイルのauthToken情報を保存する
     * @param  String  accountName 選択ID(アドレス)
     * @param  String  authToken 認証トークン
     */
    public static final void createJsonAuthTokenSD(String accountName,String authToken) {

        try {
            if (!new File(Constants.GOOGLE_INFO_JSON_DIR).exists()) {
                new File(Constants.GOOGLE_INFO_JSON_DIR).mkdirs();
            }
            JSONObject jsonObject = new JSONObject();
            // JSONデータの作成
            jsonObject.accumulate("account_name", accountName);
            jsonObject.accumulate("auth_token", authToken);
            jsonObject.accumulate("create_date", TimeUtils.getCurrentYearMonthDay());
            File tokenFile = new File( AUTH_TOKEN_JSON_FILE );
            OutputStream outStream = new FileOutputStream(tokenFile);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outStream,"UTF-8"));

            writer.print(jsonObject);
            writer.close();
        } catch (JSONException e) {
            Log.e("JSONException",e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
    }

    /**
     * SDCard にGoogle Oauthで取得した情報をJSONファイルに保存する
     * @param  JSONObject  json 取得情報
     */
    public static final void createJsonGoogleOauthInfoSD(JSONObject json) {

        try {
            if (!new File(Constants.GOOGLE_INFO_JSON_DIR).exists()) {
                new File(Constants.GOOGLE_INFO_JSON_DIR).mkdirs();
            }
            File tokenFile = new File( GOOGLE_USER_INFO_FILE );
            OutputStream outStream = new FileOutputStream(tokenFile);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outStream,"UTF-8"));
            json.accumulate("create_date", TimeUtils.getCurrentYearMonthDay());
            writer.print(json);
            writer.close();
        } catch (JSONException e) {
            Log.e("JSONException",e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
    }

    /**
     * 指定したパスとファイル名のJSONファイルの情報を取得する
     * @param  String FilePath 取得JSONファイルのパスとファイル名（絶対パス）
     * @return JSONObject  json 取得情報
     */
    public static final JSONObject getJsonTargetFile(String FilePath) {
        JSONObject jsonObject= null;
        try {
            File tokenFile = new File( FilePath );
            InputStream inputStream = new FileInputStream(tokenFile);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // Json読み込み
            String jsonString = new String(buffer);
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            Log.e("JSONException",e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
        return jsonObject;
    }
    /**
     * SDCard にGoogle Oauthで取得した情報をJSONから取得する
     * @return JSONObject  json 取得情報
     */
    public static final JSONObject getJsonAuthToken() {
        JSONObject jsonObject= null;
        try {
            File tokenFile = new File( AUTH_TOKEN_JSON_FILE );
            InputStream inputStream = new FileInputStream(tokenFile);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // Json読み込み
            String jsonString = new String(buffer);
            jsonObject = new JSONObject(jsonString);

        } catch (JSONException e) {
            Log.e("JSONException",e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
        return jsonObject;
    }

    /**
     * SDCard にGoogle Oauthで取得した情報をJSONから取得する
     * @return JSONObject  json 取得情報
     */
    public static final JSONObject getJsonGoogleInfo() {
        JSONObject jsonObject= null;
        try {
            File tokenFile = new File( GOOGLE_USER_INFO_FILE );
            InputStream inputStream = new FileInputStream(tokenFile);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // Json読み込み
            String jsonString = new String(buffer);
            jsonObject = new JSONObject(jsonString);

        } catch (JSONException e) {
            Log.e("JSONException",e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
        return jsonObject;
    }

    /**
     * SDCard にGoogle Oauthで取得した情報をJSONから取得する
     * @return JSONObject  json 取得情報
     */
    public static final JSONObject getJsonGoogleOauthInfo() {
        JSONObject jsonObject= null;
        try {
            File tokenFile = new File( GOOGLE_USER_INFO_FILE );
            InputStream inputStream = new FileInputStream(tokenFile);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // Json読み込み
            String jsonString = new String(buffer);
            jsonObject = new JSONObject(jsonString);

            System.out.println(jsonObject.getString("name"));
            System.out.println(jsonObject.getString("picture"));
        } catch (JSONException e) {
            Log.e("JSONException",e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
        return jsonObject;
    }

    /**
     * SDCard に保存されているauthTokenを取得する
     * @param  Context  context コンテキスト情報
     * @return String  authTokenSD 保存されている認証トークン
     */
    public static final String getAuthTokenSD(Context context) {
        String authTokenSD ="";
        try {
            File tokenFile = new File( AUTH_TOKEN_FILE );
            FileReader filereader = new FileReader(tokenFile);
            //OutputStream outStream = new FileOutputStream(tokenFile);
            //ObjectInputStream in  = new ObjectInputStream(context.openFileInput(AUTH_TOKEN_FILE));
            BufferedReader reader = new BufferedReader(filereader);
            //BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            authTokenSD = reader.readLine();
            reader.close();
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
        return authTokenSD;
    }
    
    /**
       * SDCard にGoogle認証で取得したアカウント画像を配置する
       * @param  String fileUrl 取得画像のURLパス
       */
    public static final void setImageFileSD(String fileUrl){
        try {
            URI uri = new URI(fileUrl);
            URL url = uri.toURL();
            HttpURLConnection urlCon       = (HttpURLConnection)url.openConnection();
            InputStream inputStream        = urlCon.getInputStream();

            File saveFile                  = new File(GOOGLE_USER_ICON_FILE);
            FileOutputStream fileOutStream = new FileOutputStream(saveFile);
            int c;
            while((c =inputStream.read()) != -1) fileOutStream.write((byte) c);
            fileOutStream.flush();
            fileOutStream.close();
            inputStream.close();
        } catch (URISyntaxException e) {
           Log.e("URISyntaxException", e.toString());
        } catch (MalformedURLException e) {
           Log.e("MalformedURLExc", e.toString());
        } catch (IOException e) {
           Log.e("IOException", e.toString());
        }
    }
    // URLからBitmapへの変換
    public static void getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            File saveFile                  = new File(GOOGLE_USER_ICON_FILE);
            FileOutputStream fileOutStream = new FileOutputStream(saveFile);
            myBitmap.compress(Bitmap.CompressFormat.PNG, 50, fileOutStream);
            int c;
            while((c =input.read()) != -1) fileOutStream.write((byte) c);
            fileOutStream.flush();
            fileOutStream.close();
            input.close();
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
    }
}
