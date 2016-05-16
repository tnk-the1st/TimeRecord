package jp.co.tennti.timerecord.commonUtils;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

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
}
