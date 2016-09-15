package jp.co.tennti.timerecord.contacts;

import android.os.Environment;
import android.view.Gravity;
import android.view.ViewGroup;

/**
 * Created by TENNTI on 2016/05/17.
 */
public class Constants {

    /**
     * SQLのinsertの値の数を指定する
     * */
    public static final String INSERT_SQL_VALUES =" VALUES (?,?,?,?,?,?)";
    /**
     * 文字サイズ11
     * */
    public static final int FONT_SIZE_11 = 11;
    /**
     * 行高さサイズ55
     * */
    public static final int ROW_HIGHT_SIZE = 55;
    /**
     * 全休
     * */
    public static final String ALL_DAYS_HOLIDAY_FLAG = "1";
    /**
     * 午前半休
     * */
    public static final String AM_HALF_HOLIDAY_FLAG = "2";
    /**
     * 午後半休
     * */
    public static final String PM_HALF_HOLIDAY_FLAG = "3";
    /**
     * 全休
     * */
    public static final String ALL_DAYS_HOLIDAY_DISP = "全";
    /**
     * 午前半休
     * */
    public static final String AM_HALF_HOLIDAY_DISP = "前";
    /**
     * 午後半休
     * */
    public static final String PM_HALF_HOLIDAY_DISP = "後";
    /**
     * 時間が無いとき用の値
     * */
    public static final String NO_TIME = "--:--:--";
    /**
     * 時間が0の時用の値
     * */
    public static final String TIME_ZERO = "00:00:00";
    /**
     * DBファイル名
     * */
    public static final String DB_FILE_NAME = "time_record_db.db";
    /**
     * アプリケーションフォルダ
     * */
    public static final String APP_FOLDER_DIR = "/time_record/db/";

    /**
     * 「wrap_content」は幅を自動調整してくれます
     * */
    public final static int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    /**
     * 「MATCH_PARENT」は画面いっぱいに表示
     * */
    public final static int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    /**
     * 上下左右中央に配置し、サイズ変更は行いません
     * */
    public final static int GRAVITY_CENTER = Gravity.CENTER;
    /**
     * コンテナの左側に配置し、サイズ変更は行いません
     * */
    public final static int GRAVITY_LEFT = Gravity.LEFT;
    /**
     * そのサイズを変更していない、そのコンテナの末尾にx軸の位置にオブジェクトをプッシュします
     * */
    public final static int GRAVITY_END = Gravity.END;         // Gravity.RIGHTでもよい
    /**
     * ラベル
     * */
    public final static String TOTAL_OVERTIME_LABEL = "合計残業時間 ";

    /** データベースを置くSD上のフォルダのフルパス */
    public static final String DB_DIRECTORY          = Environment.getExternalStorageDirectory() + "/time_record/db/";
    /** アプリケーションのファイルを置くSD上のフォルダのフルパス */
    public static final String APP_DIRECTORY         = Environment.getExternalStorageDirectory() + "/time_record/";
    /** 認証トークンのファイルを置くSD上のフォルダのフルパス */
    public static final String AUTH_TOKEN_DIRECTORY  = Environment.getExternalStorageDirectory() + "/time_record/auth_token/";
    /** 認証ファイルをjsonにし配置するSD上のフォルダのフルパス */
    public static final String GOOGLE_INFO_DIR       = Environment.getExternalStorageDirectory() + "/time_record/google_info/";
    /** 認証ファイルをjsonにし配置するSD上のフォルダのフルパス */
    public static final String GOOGLE_INFO_JSON_DIR  = Environment.getExternalStorageDirectory() + "/time_record/google_info/json/";

    public static final String AUTH_TOKEN_DIR_SHORT  = "/time_record/auth_token/";
    /**
     * Oauth2認証ファイル名
     * */
    public static final String OAUTH_TOKEN_FILE_NAME  = "auth_token.txt";
    /**
     * Oauth2認証JSONファイル名
     * */
    public static final String OAUTH_TOKEN_FILE_JSON  = "auth_token.json";
    /**
     * Oauth2認証JSONファイル名
     * */
    public static final String GOOGLE_USER_INFO_JSON  = "google_user_info.json";
    /**
     * Oauth2認証アイコンイメージファイル名
     * */
    public static final String GOOGLE_USER_ICON_IMG   = "icon.jpg";
}
