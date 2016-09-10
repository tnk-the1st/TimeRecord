package jp.co.tennti.timerecord.commonUtils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import jp.co.tennti.timerecord.R;

/**
 * Created by TENNTI on 2016/06/07.
 */
public class ListAdapter extends BaseAdapter {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int GC = Gravity.CENTER;
    private final static int GL = Gravity.LEFT;
    private final static int GE = Gravity.END;
    private int colorFlg = 1;

    TableRow.LayoutParams paramsDate = setParams(0.2f);
    TableRow.LayoutParams paramsQuitTime = setParams(0.3f);
    TableRow.LayoutParams paramsOverTime = setParams(0.2f);
    TableRow.LayoutParams paramsWeek = setParams(0.1f);

    private  Context contextMenba;
    private LayoutInflater inflater;
    private List<HashMap<String, String>> listMap;

    public ListAdapter(Context context,  List<HashMap<String, String>> colList) {
        contextMenba = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listMap = colList;
    }

    @Override
    public int getCount() {
        return listMap.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final TimeUtils timeUtil = new TimeUtils();
            convertView = inflater.inflate(R.layout.fragment_list_view, null,false);
            TableLayout mTableLayoutList = (TableLayout) convertView.findViewById(R.id.tableLayoutList);
            for (HashMap<String, String> onloadMap : listMap) {
                TableRow row = new TableRow(contextMenba);
                // 日付
                final TextView textDate = setTextItem(onloadMap.get("basic_date"), GC);
                // 退社時間
                final TextView textsQuitTime = setTextItem(onloadMap.get("leaving_date"), GL);
                // 退社時間
                final TextView textOverTime = setTextItem(onloadMap.get("overtime"), GC);
                // 曜日
                final TextView textWeek = setTextItem(onloadMap.get("week"), GC);
                /******************* フォント調整 *******************/
                textDate.setTextSize(11);
                textsQuitTime.setTextSize(11);
                textOverTime.setTextSize(11);
                textWeek.setTextSize(11);
                /******************* フォント調整 *******************/
                row.addView(textDate, paramsDate);
                row.addView(textsQuitTime, paramsQuitTime);
                row.addView(textOverTime, paramsOverTime);
                row.addView(textWeek, paramsWeek);

                // 交互に行の背景を変える
                if (colorFlg % 2 != 0) {
                    row.setBackgroundResource(R.drawable.row_color1);
                } else {
                    row.setBackgroundResource(R.drawable.row_color2);
                }
                /******************* 曜日背景色ドリブン *******************/
                textWeek.setBackgroundResource(timeUtil.setBackgroundWeek(onloadMap.get("week")));
                /******************* 曜日背景色ドリブン *******************/
                mTableLayoutList.addView(row);            // TableLayoutにrowHeaderを追加
                colorFlg++;
            }
        }
        return convertView;
    }

    /**
     * 行の各項目のTextViewカスタマイズ処理
     * setTextItem()
     *
     * @param str     String
     * @param gravity int
     * @return title TextView タイトル
     */
    private TextView setTextItem(String str, int gravity) {
        TextView title = new TextView(contextMenba);
        title.setTextSize(16.0f);           // テキストサイズ
        title.setTextColor(Color.BLACK);    // テキストカラー
        title.setGravity(gravity);          // テキストのGravity
        title.setText(str);                 // テキストのセット

        return title;
    }
    /**
     * 行の各項目のLayoutParamsカスタマイズ処理
     * setParams()
     */
    private TableRow.LayoutParams setParams(float f) {
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, WC);
        params.weight = f;      //weight(行内でのテキストごとの比率)
        //params.setMargins(12,12,12,12);
        return params;
    }
}
