package jp.co.tennti.timerecord.commonUtils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.tennti.timerecord.R;
import jp.co.tennti.timerecord.contacts.Constants;


public class ListViewUtils {

    Context context;

    public ListViewUtils(Context context) {
        this.context = context;
    }
    /**
     * ヘッダーサイズ
     */
    TableRow.LayoutParams paramsDate     = setParams(0.2f);
    TableRow.LayoutParams paramsQuitTime = setParams(0.3f);
    TableRow.LayoutParams paramsOverTime = setParams(0.2f);
    TableRow.LayoutParams paramsWeek     = setParams(0.1f);

     public String createTableRow( List<HashMap<String, String>> tableDataList , TableLayout mTableLayoutList) {
        final Typeface meiryobType = FontUtils.getTypefaceFromAssetsZip(context,"font/meiryob_first_level.zip");
        int colorFlg = 1;
        String baseTime ="00:00:00";
        final TimeUtils timeUtil = new TimeUtils();
        for (HashMap<String, String> tableDataMap : tableDataList) {
            TableRow row = new TableRow(context);

            // 日付
            final TextView textDate     = setTextItem(tableDataMap.get("basic_date"), Constants.GRAVITY_CENTER);
            // 退社時間
            final TextView textQuitTime = setTextItem(tableDataMap.get("leaving_date"), Constants.GRAVITY_CENTER);
            // 退社時間
            final TextView textOverTime = setTextItem(tableDataMap.get("overtime"), Constants.GRAVITY_CENTER);
            // 曜日
            final TextView textWeek     = setTextItem(tableDataMap.get("week"), Constants.GRAVITY_CENTER);
            /******************* フォント調整 *******************/
            setTextViewFontInfo(textDate , meiryobType);
            setTextViewFontInfo(textQuitTime , meiryobType);
            setTextViewFontInfo(textOverTime , meiryobType);
            setTextViewFontInfo(textWeek , meiryobType);
            /******************* フォント調整 *******************/
            // 交互に行の背景を変える
            if (colorFlg % 2 != 0) {
                textDate.setBackgroundResource(R.drawable.row_color1);
                textQuitTime.setBackgroundResource(R.drawable.row_color1);
                textOverTime.setBackgroundResource(R.drawable.row_color1);
                textWeek.setBackgroundResource(R.drawable.row_color1);
            } else {
                textDate.setBackgroundResource(R.drawable.row_color2);
                textQuitTime.setBackgroundResource(R.drawable.row_color2);
                textOverTime.setBackgroundResource(R.drawable.row_color2);
                textWeek.setBackgroundResource(R.drawable.row_color2);
            }
            /******************* 曜日背景色ドリブン *******************/
            textWeek.setBackgroundResource(timeUtil.setBackgroundWeek(tableDataMap.get("week")));
            /******************* 曜日背景色ドリブン *******************/
            if(!tableDataMap.get("overtime").toString().equals(Constants.NO_TIME)){
                baseTime = timeUtil.addTimeCalculation( baseTime , tableDataMap.get("overtime").toString() );
            }
            row.addView(textDate, paramsDate);
            row.addView(textQuitTime, paramsQuitTime);
            row.addView(textOverTime, paramsOverTime);
            row.addView(textWeek, paramsWeek);
            mTableLayoutList.addView(row);
            colorFlg++;
        }
        //colorFlg = 1;
        return baseTime;
    }
    /**
     * セルごとの情報にフォントの情報を挿入する
     * @param targetText  対象のセルテキストビュー
     * @param meiryobType 文字のフォント
     */
    private void setTextViewFontInfo( TextView targetText ,Typeface meiryobType) {
        targetText.setTextSize(Constants.FONT_SIZE_11);
        targetText.setTypeface(meiryobType);
    }
    /**
     * 行の各項目のLayoutParamsカスタマイズ処理
     * weight(行内でのテキストごとの比率)
     * setParams()
     */
    private TableRow.LayoutParams setParams(float f) {
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, 0);
        params.weight = f;
        params.height = Constants.ROW_HIGHT_SIZE;
        return params;
    }
    /**
     * 行の各項目のTextViewカスタマイズ処理
     * setTextItem()
     *テキストサイズ
     * テキストカラー
     * テキストのGravity
     * テキストのセット
     * @param str     String
     * @param gravity int
     * @return title TextView タイトル
     */
    private TextView setTextItem(String str, int gravity) {
        TextView title = new TextView(context);
        title.setTextSize(16.0f);
        title.setTextColor(Color.BLACK);
        title.setGravity(gravity);
        title.setText(str);
        return title;
    }

}
