package jp.co.tennti.timerecord.commonUtils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jp.co.tennti.timerecord.apache.commons.IOUtils;
import jp.co.tennti.timerecord.contacts.Constants;
import jp.co.tennti.timerecord.daoUtils.MySQLiteOpenHelper;

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
     * @param  fileName ファイル名 string
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
     * @param  fileName ファイル名 string
     * @return String ファイルまでの絶対パス
     */
    public static final String toSDCardAbsolutePath(String fileName) {
        return getSDCardDir().getAbsolutePath() + File.separator + fileName;
    }

    /**
     * 月分の空List作成
     * @param  tarDate yyyy-MM String
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
     * @param  accountName 選択ID(アドレス) string
     * @param  context コンテキスト情報 Context
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
     * @param  accountName 選択ID(アドレス) String
     * @param  authToken 認証トークン String
     */
    public static final void createJsonAuthTokenSD(String accountName,String authToken) {
        PrintWriter writer = null;
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
            writer = new PrintWriter(new OutputStreamWriter(outStream,"UTF-8"));

            writer.print(jsonObject);
            writer.close();
        } catch (JSONException e) {
            Log.e("JSONException",e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    /**
     * SDCard にGoogle Oauthで取得した情報をJSONファイルに保存する
     * @param json 取得情報 JSONObject
     */
    public static final void createJsonGoogleOauthInfoSD(JSONObject json) {
        PrintWriter writer = null;
        try {
            if (!new File(Constants.GOOGLE_INFO_JSON_DIR).exists()) {
                new File(Constants.GOOGLE_INFO_JSON_DIR).mkdirs();
            }
            File tokenFile = new File( GOOGLE_USER_INFO_FILE );
            OutputStream outStream = new FileOutputStream(tokenFile);
            writer = new PrintWriter(new OutputStreamWriter(outStream,"UTF-8"));
            json.accumulate("create_date", TimeUtils.getCurrentYearMonthDay());
            writer.print(json);
            writer.close();
        } catch (JSONException e) {
            Log.e("JSONException",e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    /**
     * 指定したパスとファイル名のJSONファイルの情報を取得する
     * @param  FilePath 取得JSONファイルのパスとファイル名（絶対パス）string
     * @return JSONObject  json 取得情報
     */
    public static final JSONObject getJsonTargetFile(String FilePath) {
        JSONObject jsonObject = null;
        InputStream inputStream = null;
        try {
            File tokenFile = new File( FilePath );
            inputStream = new FileInputStream(tokenFile);
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
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return jsonObject;
    }
    /**
     * SDCard にGoogle Oauthで取得した情報をJSONから取得する
     * @return JSONObject  json 取得情報
     */
    public static final JSONObject getJsonAuthToken() {
        JSONObject jsonObject = null;
        InputStream inputStream = null;
        try {
            File tokenFile = new File( AUTH_TOKEN_JSON_FILE );
            inputStream = new FileInputStream(tokenFile);
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
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return jsonObject;
    }

    /**
     * SDCard にGoogle Oauthで取得した情報をJSONから取得する
     * @return JSONObject  json 取得情報
     */
    public static final JSONObject getJsonGoogleInfo() {
        JSONObject jsonObject = null;
        InputStream inputStream = null;
        try {
            File tokenFile = new File( GOOGLE_USER_INFO_FILE );
            inputStream = new FileInputStream(tokenFile);
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
        } finally {
            IOUtils.closeQuietly(inputStream);
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

        } catch (JSONException e) {
            Log.e("JSONException",e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
        return jsonObject;
    }

    /**
     * SDCard に保存されているauthTokenを取得する
     * @param  context コンテキスト情報 Context
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
       * @param fileUrl 取得画像のURLパス String
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
    public static final Bitmap getBitmapFromURL(Context context) {
        Bitmap mBitmap = null;
        FileInputStream file = null;
        BufferedInputStream buf = null;
        try {
            file = new FileInputStream(GOOGLE_USER_ICON_FILE);
            buf = new BufferedInputStream(file);
            mBitmap = BitmapFactory.decodeStream(buf);
            file.close();
            buf.close();
        } catch (FileNotFoundException e){
            Log.e("FileNotFoundException", e.toString());
        } catch (IOException e){
            Log.e("IOException", e.toString());
        } finally {
            IOUtils.closeQuietly(file);
            IOUtils.closeQuietly(buf);
        }
        return mBitmap;
       /* Bitmap bitmap = null;

        try {
            //画像をファイルとして取り出す
            //bmに対する処理を書く
            File saveFile                  = new File(GOOGLE_USER_ICON_FILE);
            FileInputStream fis = new FileInputStream(saveFile);
            //FileOutputStream fos = new FileOutputStream(saveFile);
            bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
        return bitmap;
        */

    }

    /**
     * CSVファイルをSDCard 内に出力(Android 用)
     * @param  tableName 対象テーブル名 String
     * @return String ファイルまでの絶対パス
     */
    public static final void exportCSV(Activity activity , String tableName) {
        final MySQLiteOpenHelper helper = new MySQLiteOpenHelper(activity.getApplicationContext());
        final SQLiteDatabase db         = helper.getWritableDatabase();
        PrintWriter pw = null;
        try {
            final TimeUtils timeUtil = new TimeUtils();
            if (tableName.isEmpty()) {
                tableName = timeUtil.getCurrentTableName().toString();
            }
            if (!new File(Constants.CSV_DIRECTORY).exists()) {
                new File(Constants.CSV_DIRECTORY).mkdirs();
            }
            //出力先を作成する
            FileWriter  fw  = new FileWriter( Constants.CSV_DIRECTORY + tableName +".csv" , false);
            pw = new PrintWriter(new BufferedWriter(fw));

            Cursor cursor                   = MySQLiteOpenHelper.getCurrentList(db, tableName);
            setCsvDbList(cursor , pw);
            //終了メッセージを画面に出力する
            //System.out.println("出力が完了しました。");
        } catch (IOException e) {
            //例外時処理
            Log.e("IOException", e.toString());
        } finally {
            db.close();
            pw.close();
        }
    }
    /**
     * 取得DBデータを整理する
     * @param  cursor  取得データ群 Cursor
     * @param  pw 出力用ファイル PrintWriter
     */
    public static void setCsvDbList( Cursor cursor , PrintWriter pw) {
        if(cursor == null){
            pw.print("basic_date,leaving_date,overtime,week,holiday_flag,user_cd");
            pw.close();
        }
        if (cursor != null) {
            int startPosition = cursor.getPosition(); // Cursorをいじる前に現在のPositionを一旦変数に保持
            if (cursor.moveToFirst()) {
                String[] columnNames = cursor.getColumnNames();
                int length = columnNames.length;
                for (int i = 0; i < length; i++) {
                    pw.print(columnNames[i]);
                    if( i != length-1 ){
                        pw.print(",");
                    }
                }
                pw.println();
                do {
                    for (int i = 0; i < length; i++) {
                        String columnValue;
                        try {
                            columnValue = cursor.getString(i);
                            pw.print(columnValue);
                            if( i != length-1 ){
                                pw.print(",");
                            }
                        } catch (SQLiteException e) {
                            Log.e("SQLiteException", e.toString());
                        }
                    }
                    pw.println();
                } while (cursor.moveToNext());
            }
            cursor.moveToPosition(startPosition); // Cursorをいじり終わったら元のPositionに戻してあげる
        }
    }


    private static void parallelCsvDbList( Cursor cursor , PrintWriter pw , int length) {
        int threadNumber = 4;
        // 4スレッド用意
        ExecutorService executor = Executors.newFixedThreadPool(threadNumber);

        // 結果を入れる配列
        //String[] results = new String[threadNumber];

        // タスクのリストを作る
        List<Callable<String>> tasks = new ArrayList<Callable<String>>();
        //for(int i = 1; i <= threadNumber; i++){
            tasks.add(new ParallelTasks(cursor , pw ,length));

        //}

        try{
            // 並列実行
            List<Future<String>> futures;
            try{
                futures = executor.invokeAll(tasks);
            } catch(InterruptedException e){
                Log.e("InterruptedException",e.toString());
                //System.out.println(e);
                return ;
            }
            System.out.println("-----------");

            // 結果をresultsに入れる
            /*for(int i = 0; i< threadNumber; i++){
                try{
                    results[i] = (futures.get(i)).get();
                }catch(Exception e){
                    System.out.println(e);
                }
            }*/
        } finally{
            // 終了
            if(executor != null) executor.shutdown();
        }
    }

    /**
     * SDCard 内のCSVファイルを削除(Android 用)
     * @param  tableName 対象テーブル名 String
     * @return String ファイルまでの絶対パス
     */
    public static final void deleteCSV(String tableName) {
            final TimeUtils timeUtil = new TimeUtils();
            if (tableName.isEmpty()) {
                tableName = timeUtil.getCurrentTableName().toString();
            }
            //出力先を作成する
            File file  = new File( Constants.CSV_DIRECTORY + tableName +".csv");
            if (file.exists()) {
                file.delete();
            }
        return;
    }

    /**
     * SDCard 内のCSVフォルダを削除(Android 用)
     * @return String ファイルまでの絶対パス
     */
    public static final void deleteDirCSV() {
        if (!new File(Constants.CSV_DIRECTORY).exists()) {
            new File(Constants.CSV_DIRECTORY).delete();
        }
        return;
    }

    /**
     * 全CSVファイル名を取得
     * @return List<String> ファイル名リスト
     */
    public static final List<String> getDirCSVNameList() {
        File dir = new File(Constants.CSV_DIRECTORY);
        List<String> listValues = new ArrayList<String>();

        if (!dir.isDirectory()) {
            return listValues;
        }
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            listValues.add(file.getName());
        }
        return listValues;
    }

    /**
     * zip圧縮する
     * @return String ファイルまでの絶対パス
     */
    public static final void createDBZipFile() {
        File dir             = new File(Constants.DB_FULL_NAME);
        File[] files         = {dir};
        ZipOutputStream zos  = null;
        String DB_ZIP_DIR    = Constants.DB_DIRECTORY+"zip/";
        String FILL_SUB_NAME = "";
        try {
            if (!new File(DB_ZIP_DIR).exists()) {
                new File(DB_ZIP_DIR).mkdirs();
                final String command = "chmod 777 " + DB_ZIP_DIR;
                Runtime.getRuntime().exec(command);
            }

            int i = 1;
            while (new File(DB_ZIP_DIR+"time_record_db"+FILL_SUB_NAME+".zip").exists()){
                FILL_SUB_NAME = "_"+String.valueOf(i);
                i++;
            }

            zos = new ZipOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(
                                    new File(DB_ZIP_DIR+"time_record_db"+FILL_SUB_NAME+".zip"))));
            byte[] buf = new byte[1024];
            InputStream is = null;
            for (File file : files) {
                ZipEntry entry = new ZipEntry(file.getName());
                zos.putNextEntry(entry);
                is = new BufferedInputStream(new FileInputStream(file));
                int len = 0;
                while ((len = is.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(zos);
        }
    }
}
