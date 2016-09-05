package jp.co.tennti.timerecord.commonUtils;

import android.content.Context;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.tennti.timerecord.contacts.Constants;

/**
 * Created by TENNTI on 2016/04/20.
 */
public class GeneralUtils {

    /** 認証トークンのフルパス */
    private static final String AUTH_TOKEN_FILE = Constants.AUTH_TOKEN_DIRECTORY + "auth_token.txt";
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
        File file = new File(AUTH_TOKEN_FILE);
        return file.exists();
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
     * SDCard にauthTokenを保存する(Android 用)
     * @param  String  authToken 認証トークン
     * @param  Context  context コンテキスト情報
     */
    public final void createAuthTokenSD(String authToken,Context context) {
        try {
            OutputStream out   = new ObjectOutputStream(context.openFileOutput(AUTH_TOKEN_FILE, Context.MODE_PRIVATE));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
            writer.append(authToken);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * SDCard に保存されているauthTokenを取得する(Android 用)
     * @param  Context  context コンテキスト情報
     * @return String  authTokenSD 保存されている認証トークン
     */
    public final String getAuthTokenSD(Context context) {
        String authTokenSD ="";
        try {
            ObjectInputStream in  = new ObjectInputStream(context.openFileInput(AUTH_TOKEN_FILE));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            authTokenSD = reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return authTokenSD;
    }
}
