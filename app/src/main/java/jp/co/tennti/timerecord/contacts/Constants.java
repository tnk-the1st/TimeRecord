package jp.co.tennti.timerecord.contacts;

import android.view.Gravity;
import android.view.ViewGroup;

/**
 * Created by TENNTI on 2016/05/17.
 */
public class Constants {

    public static final int FONT_SIZE_11 = 11;
    /**
     * 時間が無いとき用の値
     * */
    public static final String NO_TIME = "--:--:--";
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

}
