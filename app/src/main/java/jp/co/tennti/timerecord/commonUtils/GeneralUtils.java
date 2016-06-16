package jp.co.tennti.timerecord.commonUtils;

import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.tennti.timerecord.contacts.Constants;

/**
 * Created by TENNTI on 2016/04/20.
 */
public class GeneralUtils {

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

    public static final  List<HashMap<String, String>> createblankTable(StringBuffer buffer ,TimeUtils timeUtil){
        List<HashMap<String, String>> blankResultList = new ArrayList<HashMap<String, String>>();
        final int MAX_LENGTH_I = 31;
        for (int i = 1; i <= MAX_LENGTH_I; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            StringBuffer buffer_day = new StringBuffer();
            if (i < 10) {
                buffer_day.append("0");
            }
            map.put("basic_date", buffer.toString() + "-" + buffer_day.append(i).toString());
            map.put("leaving_date", "");
            map.put("overtime", Constants.NO_TIME);
            map.put("week", timeUtil.getTargWeekOmit(buffer.toString() + "-" + buffer_day.append(i).toString()));
            blankResultList.add(map);
        }
        return blankResultList;
    }

}
