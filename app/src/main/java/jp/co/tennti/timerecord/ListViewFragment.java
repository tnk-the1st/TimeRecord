package jp.co.tennti.timerecord;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import jp.co.tennti.timerecord.commonUtils.TimeUtils;
import jp.co.tennti.timerecord.daoUtils.MySQLiteOpenHelper;


public class ListViewFragment extends Fragment {
    private Bitmap myImage = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_4444);
    //private AnimationDrawable animation;
    static final String TAG = "ListViewFragment";

    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int GC = Gravity.CENTER;
    private final static int GL = Gravity.LEFT;
    private final static int GE = Gravity.END;         // Gravity.RIGHTでもよい
    private int colorFlg = 1;                   //背景切り替え用フラグ
    TextView dateTextView=null;
    AlertDialog.Builder builder=null;
    View contentView=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /**DB接続**/
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(getActivity().getApplicationContext());
        final SQLiteDatabase db = helper.getWritableDatabase();
        /**DB接続**/

        TimeUtils timeUtil = new TimeUtils();
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        contentView = inflater.inflate(R.layout.date_picker_dia, null);
        Resources resM = getResources();
        if(myImage!=null){
            myImage.recycle();
        }
        myImage = BitmapFactory.decodeResource(resM, R.mipmap.list_disp_all);
        ImageView imgView = (ImageView)view.findViewById(R.id.listImageView);
        imgView.setImageDrawable(null);
        imgView.setImageBitmap(null);
        imgView.setImageBitmap(myImage);
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);

        /**日付データ**/
        /**データピッカー**/
        DatePicker datePicker = (DatePicker)contentView.findViewById(R.id.datePickerDia);
        // APIレベル11以降の「日」のカレンダーを表示しない 初期値true
        datePicker.setCalendarViewShown(false);
        // 初期値を設定する 1999年12月31日
        //datePicker.updateDate(1999, 11, 31);
        //2011/01/01~3000/12/31
        Date minDate = new Date(111, 0, 1);
        Date maxDate = new Date(1100, 12, 31);
        datePicker.setMinDate(minDate.getTime());
        datePicker.setMaxDate(maxDate.getTime());
        int day_id = Resources.getSystem().getIdentifier("day", "id", "android");
        datePicker.findViewById(day_id).setVisibility(View.GONE);
        /**データピッカー**/

        dateTextView = (TextView)view.findViewById(R.id.dateTextView);
        dateTextView.setText(timeUtil.getCurrentYearMonthJaCal());
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( builder == null ) {

                    // 確認ダイアログの生成
                    builder = new AlertDialog.Builder(getContext());
                    builder.setView(contentView);
                    TimeUtils timeUtil = new TimeUtils();
                    builder.setTitle(timeUtil.getCurrentDate());
                    builder.setMessage("メッセージ");
                    builder.setPositiveButton(
                        "設定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // OK ボタンクリック処理
                                DatePicker datePicker = (DatePicker) contentView.findViewById(R.id.datePickerDia);
                                try{
                                    //String JOIN_YEAR_MONTH;
                                    int month = datePicker.getMonth()+1;
                                    TimeUtils timeUtil = new TimeUtils();
                                    StringBuilder builder = new StringBuilder();
                                    String yearStr  = String.valueOf(datePicker.getYear());
                                    String monthStr = String.valueOf(month);
                                    builder.append(yearStr);
                                    builder.append("年");
                                    timeUtil.is10over(month, builder);
                                    builder.append(monthStr);
                                    builder.append("月");
                                    //JOIN_YEAR_MONTH = builder.toString();
                                    dateTextView.setText(builder.toString());
                                } catch (NullPointerException e){
                                    Log.d("NullPointerException",e.getMessage());
                                }

                            }
                        });
                    builder.setNegativeButton(
                        "閉じる",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel ボタンクリック処理
                            }
                        });

                    // 表示
                    builder.create().show();
                } else {

                    final android.view.ViewParent parent = contentView.getParent ();
                    if (parent instanceof android.view.ViewManager)
                    {
                        final android.view.ViewManager viewManager = (android.view.ViewManager) parent;

                        viewManager.removeView (contentView);
                    }

                    //builder = new AlertDialog.Builder(getContext());
                    builder.setView(contentView);
                    builder.show();
                }
            }
        });
        /**日付データ**/
        /**データピッカー**/

        /**データピッカー**/
        /**期間変更ボタン**/
        // ボタンを設定
        ImageButton perCountButton = (ImageButton)view.findViewById(R.id.perSwitchButton);

        perCountButton.setImageBitmap(null);
        perCountButton.setImageDrawable(null);
        //controlButton.setImageBitmap(permitDisallowedImage);
        perCountButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_period_change_switch));
        // ボタンを取得して、ClickListenerをセット
        //timeCountButton.setOnClickListener(this);
        // リスナーをボタンに登録
        perCountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateTextView = (TextView)view.findViewById(R.id.dateTextView);
                    if ( dateTextView.getText() != null && dateTextView.getText() != "" ) {
                        String yearStr  =  dateTextView.getText().toString().substring(0, 4);
                        String monthStr =  dateTextView.getText().toString().substring(5, 7);

                        //String JOIN_YEAR_MONTH;
                        StringBuilder builder = new StringBuilder();
                        builder.append(yearStr);
                        builder.append("-");
                        builder.append(monthStr);
                        //JOIN_YEAR_MONTH = builder.toString();

                        //String TABLE_YEAR_MONTH;
                        StringBuilder builder_t = new StringBuilder();
                        builder_t.append("time_record_");
                        builder_t.append(yearStr);
                        builder_t.append(monthStr);
                        //TABLE_YEAR_MONTH = builder_t.toString();


                        /**一覧レイアウト**/
                        TableLayout mTableLayoutList = (TableLayout) view.findViewById(R.id.tableLayoutList);
                        mTableLayoutList.removeAllViews();

                        TableRow.LayoutParams paramsDate = setParams(0.2f);       // LayoutParamsのカスタマイズ処理
                        TableRow.LayoutParams paramsQuitTime = setParams(0.3f);
                        TableRow.LayoutParams paramsOverTime = setParams(0.2f);
                        TableRow.LayoutParams paramsWeek = setParams(0.1f);
                        TableRow rowHeader = new TableRow(getActivity());    // 行を作成
                        rowHeader.setPadding(2, 2, 2, 2);       // 行のパディングを指定(左, 上, 右, 下)
                        rowHeader.setBackgroundResource(R.drawable.row_head);

                        Typeface meiryoType=Typeface.createFromAsset(getResources().getAssets(), "meiryo.ttc");
                        db.beginTransaction();
                        try {
                            Cursor cursor = db.rawQuery("SELECT basic_date,leaving_date,over_time_date,week FROM " + builder_t.toString() + " WHERE year_month_date=? ORDER BY basic_date;", new String[]{builder.toString()});
                            try {
                                if (cursor.moveToFirst()) {
                                    do {
                                        TableRow row = new TableRow(getActivity());          // 行を作成
                                        row.removeAllViews();
                                        //row.setPadding(1, 1, 1, 1);             // 行のパディングを指定(左, 上, 右, 下)
                                        // 日付
                                        TextView textDate = setTextItem(cursor.getString(cursor.getColumnIndex("basic_date")), GC);     // TextViewのカスタマイズ処理
                                        // 退社時間
                                        TextView textsQuitTime = setTextItem(cursor.getString(cursor.getColumnIndex("leaving_date")), GL);      // TextViewのカスタマイズ処理
                                        // 残業時間
                                        TextView textOverTime = setTextItem(cursor.getString(cursor.getColumnIndex("over_time_date")), GC);      // TextViewのカスタマイズ処理
                                        // 曜日
                                        TextView textWeek = setTextItem(cursor.getString(cursor.getColumnIndex("week")), GC);      // TextViewのカスタマイズ処理
                                        /******************* フォント調整 *******************/
                                        textDate.setTextSize(11);
                                        textsQuitTime.setTextSize(11);
                                        textOverTime.setTextSize(11);
                                        textWeek.setTextSize(11);
                                        textDate.setTypeface(meiryoType);
                                        textsQuitTime.setTypeface(meiryoType);
                                        textOverTime.setTypeface(meiryoType);
                                        textWeek.setTypeface(meiryoType);
                                        /******************* フォント調整 *******************/

                                        row.addView(textDate, paramsDate);      // 日付
                                        row.addView(textsQuitTime, paramsQuitTime);      // 退社時間
                                        row.addView(textOverTime, paramsOverTime);          //残業時間
                                        row.addView(textWeek, paramsWeek);        // 曜日
                                        mTableLayoutList.addView(row);            // TableLayoutにrowHeaderを追加

                                        // 交互に行の背景を変える
                                        if (colorFlg % 2 != 0) {
                                            row.setBackgroundResource(R.drawable.row_color1);
                                        } else {
                                            row.setBackgroundResource(R.drawable.row_color2);
                                        }
                                        colorFlg++;
                                    } while (cursor.moveToNext());

                                } else {
                                    // Toast.makeText(getActivity(), "検索結果 0件", Toast.LENGTH_SHORT).show();
                                    Log.e("SELECT ERROR", "検索結果 0件");
                                }
                            }finally {
                                cursor.close();
                            }
                        } catch (SQLException ex) {
                            Log.e("SELECT ERROR", ex.toString());
                        } finally {
                            db.endTransaction();
                        }
                    }
                }
        });
        /**期間変更ボタン**/


        Typeface meiryoType=Typeface.createFromAsset(getResources().getAssets(), "meiryo.ttc");
        /**ヘッダーレイアウト**/
        TableLayout headerTable = (TableLayout) view.findViewById(R.id.headerTable);
        TableRow rowHeader = new TableRow(getActivity());    // 行を作成
        rowHeader.setPadding(2, 2, 2, 2);       // 行のパディングを指定(左, 上, 右, 下)
        rowHeader.setBackgroundResource(R.drawable.row_head);

        // ヘッダー：日付
        TextView headeDate = setTextItem("日付", GC);            // TextViewのカスタマイズ処理
        headeDate.setTextColor(Color.WHITE);
        headeDate.setTextSize(12);
        headeDate.setTypeface(meiryoType);
        TableRow.LayoutParams paramsDate = setParams(0.2f);       // LayoutParamsのカスタマイズ処理
        // ヘッダー：退社時間
        TextView headerQuitTime = setTextItem("退社時間", GC);
        headerQuitTime.setTextColor(Color.WHITE);
        headerQuitTime.setTextSize(12);
        headerQuitTime.setTypeface(meiryoType);
        TableRow.LayoutParams paramsQuitTime = setParams(0.3f);
        // ヘッダー：残業時間
        TextView headerOverTime = setTextItem("残業時間", GC);
        headerOverTime.setTextColor(Color.WHITE);
        headerOverTime.setTextSize(12);
        headerOverTime.setTypeface(meiryoType);
        TableRow.LayoutParams paramsOverTime = setParams(0.2f);
        // ヘッダー：曜日
        TextView headerWeek = setTextItem("曜日", GC);
        headerWeek.setTextColor(Color.WHITE);
        headerWeek.setTextSize(12);
        headerWeek.setTypeface(meiryoType);
        TableRow.LayoutParams paramsWeek = setParams(0.1f);
        // rowHeaderにヘッダータイトルを追加
        rowHeader.addView(headeDate, paramsDate);                 // ヘッダー：日付
        rowHeader.addView(headerQuitTime, paramsQuitTime);        // ヘッダー：退社時間
        rowHeader.addView(headerOverTime, paramsOverTime);        // ヘッダー：残業時間
        rowHeader.addView(headerWeek, paramsWeek);                // ヘッダー：曜日
        //rowHeader.setBackgroundResource(R.drawable.row_deco1);  // 背景

        // TableLayoutにrowHeaderを追加
        headerTable.addView(rowHeader);
        /**ヘッダーレイアウト**/
        /**一覧レイアウト**/
        TableLayout mTableLayoutList = (TableLayout) view.findViewById(R.id.tableLayoutList);

        /**DB接続 実行処理**/
        db.beginTransaction();
        try {
            colorFlg = 1;
            Cursor cursor = db.rawQuery("SELECT basic_date,leaving_date,over_time_date,week FROM " + timeUtil.createTableName() + " WHERE year_month_date=? ORDER BY basic_date;", new String[]{timeUtil.getCurrentYearMonthHyphen()});
            try {
                if (cursor.moveToFirst()) {
                    do {
                        TableRow row = new TableRow(getActivity());          // 行を作成
                        //row.setPadding(1, 1, 1, 1);             // 行のパディングを指定(左, 上, 右, 下)
                        // 日付
                        TextView textDate = setTextItem(cursor.getString(cursor.getColumnIndex("basic_date")), GC);     // TextViewのカスタマイズ処理
                        // 退社時間
                        TextView textsQuitTime = setTextItem(cursor.getString(cursor.getColumnIndex("leaving_date")), GL);      // TextViewのカスタマイズ処理
                        // 退社時間
                        TextView textOverTime = setTextItem(cursor.getString(cursor.getColumnIndex("over_time_date")), GC);      // TextViewのカスタマイズ処理
                        // 曜日
                        TextView textWeek = setTextItem(cursor.getString(cursor.getColumnIndex("week")), GC);      // TextViewのカスタマイズ処理
                        /******************* フォント調整 *******************/
                        textDate.setTextSize(11);
                        textsQuitTime.setTextSize(11);
                        textOverTime.setTextSize(11);
                        textWeek.setTextSize(11);
                        textDate.setTypeface(meiryoType);
                        textsQuitTime.setTypeface(meiryoType);
                        textOverTime.setTypeface(meiryoType);
                        textWeek.setTypeface(meiryoType);
                        /******************* フォント調整 *******************/

                        row.addView(textDate, paramsDate);      // 日付
                        row.addView(textsQuitTime, paramsQuitTime);      // 退社時間
                        row.addView(textOverTime, paramsOverTime);          //残業時間
                        row.addView(textWeek, paramsWeek);        // 曜日
                        mTableLayoutList.addView(row);            // TableLayoutにrowHeaderを追加

                        // 交互に行の背景を変える
                        if (colorFlg % 2 != 0) {
                            row.setBackgroundResource(R.drawable.row_color1);
                        } else {
                            row.setBackgroundResource(R.drawable.row_color2);
                        }
                        colorFlg++;
                    } while (cursor.moveToNext());

                } else {
                   // Toast.makeText(getActivity(), "検索結果 0件", Toast.LENGTH_SHORT).show();
                    Log.e("SELECT ERROR", "検索結果 0件");
                    Toast.makeText(getActivity(), "検索結果 0件", Toast.LENGTH_SHORT).show();
                }
            }finally {
                cursor.close();
            }
        } catch (SQLException ex) {
            Log.e("SELECT ERROR", ex.toString());
        } finally {
            db.endTransaction();
        }
        return view;//inflater.inflate(R.layout.fragment_list_view, container, false);
    }

   @Override
    public void onStart() {
        super.onStart();

    }
    /***
     * Activityが「onPause」になった場合や、Fragmentが変更更新されて操作を受け付けなくなった場合に呼び出される
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    /***
     * フォアグラウンドでなくなった場合に呼び出される
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    /***
     * Fragmentの内部のViewリソースの整理を行う
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        //myImage.recycle();
        myImage=null;
        ImageView imgView = (ImageView)getActivity().findViewById(R.id.listImageView);
        imgView.setImageBitmap(null);
        imgView.setImageDrawable(null);
        TableLayout headerTable = (TableLayout) getActivity().findViewById(R.id.headerTable);
        headerTable.setOnClickListener(null);
        headerTable.setBackground(null);
        headerTable.removeAllViews();
        /************ 期間変更ボタン ************/
        ImageButton timeSwitchButton = (ImageButton)getActivity().findViewById(R.id.perSwitchButton);
        timeSwitchButton.setImageBitmap(null);
        timeSwitchButton.setImageDrawable(null);
        timeSwitchButton.setOnClickListener(null);
        dateTextView = (TextView)getActivity().findViewById(R.id.dateTextView);
        dateTextView.setOnClickListener(null);
        /************ グローバル変数 ************/
        dateTextView=null;
        builder=null;
        contentView=null;
        System.gc();
    }

    /***
     * Fragmentが破棄される時、最後に呼び出される
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    /***
     * Activityの関連付けから外された時に呼び出される
     */
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
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
        TextView title = new TextView(getActivity());
        title.setTextSize(16.0f);           // テキストサイズ
        title.setTextColor(Color.BLACK);    // テキストカラー
        title.setGravity(gravity);          // テキストのGravity
        title.setText(str);                 // テキストのセット

        return title;
    }/**
     * 行の各項目のLayoutParamsカスタマイズ処理
     * setParams()
     */
    private TableRow.LayoutParams setParams(float f) {
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, WC);
        params.weight = f;      //weight(行内でのテキストごとの比率)
        return params;
    }

}
