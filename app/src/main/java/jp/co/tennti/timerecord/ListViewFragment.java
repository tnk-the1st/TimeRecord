package jp.co.tennti.timerecord;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int GC = Gravity.CENTER;
    private final static int GL = Gravity.LEFT;
    private final static int GE = Gravity.END;         // Gravity.RIGHTでもよい
    private int colorFlg        = 1;                   //背景切り替え用フラグ
    TextView dateTextView       = null;
    AlertDialog.Builder builder = null;
    View contentView            = null;
    /** ヘッダーサイズ */
    TableRow.LayoutParams paramsDate     = setParams(0.2f);       // LayoutParamsのカスタマイズ処理
    TableRow.LayoutParams paramsQuitTime = setParams(0.3f);
    TableRow.LayoutParams paramsOverTime = setParams(0.2f);
    TableRow.LayoutParams paramsWeek     = setParams(0.1f);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /**DB接続**/
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(getActivity().getApplicationContext());
        final SQLiteDatabase db   = helper.getWritableDatabase();
        /**DB接続**/
        final Typeface meiryobType=Typeface.createFromAsset(getResources().getAssets(), "meiryob.ttc");
        final Typeface meiryoType=Typeface.createFromAsset(getResources().getAssets(), "meiryo.ttc");

        final TimeUtils timeUtil = new TimeUtils();
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
        final Date minDate = new Date(111, 0, 1);
        final Date maxDate = new Date(1100, 12, 31);
        datePicker.setMinDate(minDate.getTime());
        datePicker.setMaxDate(maxDate.getTime());
        final int day_id = Resources.getSystem().getIdentifier("day", "id", "android");
        datePicker.findViewById(day_id).setVisibility(View.GONE);
        /**データピッカー**/

        dateTextView = (TextView)view.findViewById(R.id.dateTextView);
        dateTextView.setText(timeUtil.getCurrentYearMonthJaCal());
        dateTextView.setTextColor(Color.BLACK);
        dateTextView.setTypeface(meiryobType);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( builder == null ) {

                    // 確認ダイアログの生成
                    builder = new AlertDialog.Builder(getContext());
                    builder.setView(contentView);
                    final TimeUtils timeUtil = new TimeUtils();
                    builder.setTitle(timeUtil.getCurrentDate());
                    builder.setMessage("メッセージ");
                    builder.setPositiveButton(
                        "設定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // OK ボタンクリック処理
                                DatePicker datePicker = (DatePicker) contentView.findViewById(R.id.datePickerDia);
                                try{
                                    final int month = datePicker.getMonth()+1;
                                    final TimeUtils timeUtil = new TimeUtils();
                                    dateTextView.setText(timeUtil.joinTarYYYYMMJaCal(String.valueOf(datePicker.getYear()), month));
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
                    if (parent instanceof android.view.ViewManager) {
                        final android.view.ViewManager viewManager = (android.view.ViewManager) parent;
                        viewManager.removeView (contentView);
                    }

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
        final ImageButton perCountButton = (ImageButton)view.findViewById(R.id.perSwitchButton);

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
                        final String yearStr  =  dateTextView.getText().toString().substring(0, 4);
                        final String monthStr =  dateTextView.getText().toString().substring(5, 7);

                        StringBuffer buffer = new StringBuffer();
                        buffer.append(yearStr);
                        buffer.append("-");
                        buffer.append(monthStr);

                        StringBuffer buffer_t = new StringBuffer();
                        buffer_t.append("time_record_");
                        buffer_t.append(yearStr);
                        buffer_t.append(monthStr);

                        /**一覧レイアウト**/
                        final TableLayout mTableLayoutList = (TableLayout) view.findViewById(R.id.tableLayoutList);
                        mTableLayoutList.removeAllViews();

                        TableRow.LayoutParams paramsDate = setParams(0.2f);       // LayoutParamsのカスタマイズ処理
                        TableRow.LayoutParams paramsQuitTime = setParams(0.3f);
                        TableRow.LayoutParams paramsOverTime = setParams(0.2f);
                        TableRow.LayoutParams paramsWeek = setParams(0.1f);
                        TableRow rowHeader = new TableRow(getActivity());    // 行を作成
                        rowHeader.setPadding(2, 2, 2, 2);       // 行のパディングを指定(左, 上, 右, 下)
                        rowHeader.setBackgroundResource(R.drawable.row_head);

                        final Typeface meiryoType=Typeface.createFromAsset(getResources().getAssets(), "meiryo.ttc");
                        db.beginTransaction();
                        try {
                            Cursor cursor = db.rawQuery("SELECT basic_date,leaving_date,over_time_date,week FROM " + buffer_t.toString() + " WHERE year_month_date=? ORDER BY basic_date LIMIT 31;", new String[]{buffer.toString()});
                            try {
                                if (cursor.moveToFirst()) {
                                    do {
                                        TableRow row = new TableRow(getActivity());          // 行を作成
                                        row.removeAllViews();
                                        //row.setPadding(1, 1, 1, 1);             // 行のパディングを指定(左, 上, 右, 下)
                                        // 日付
                                        final TextView textDate = setTextItem(cursor.getString(cursor.getColumnIndex("basic_date")), GC);     // TextViewのカスタマイズ処理
                                        // 退社時間
                                        final TextView textsQuitTime = setTextItem(cursor.getString(cursor.getColumnIndex("leaving_date")), GL);      // TextViewのカスタマイズ処理
                                        // 残業時間
                                        final TextView textOverTime = setTextItem(cursor.getString(cursor.getColumnIndex("over_time_date")), GC);      // TextViewのカスタマイズ処理
                                        // 曜日
                                        final TextView textWeek = setTextItem(cursor.getString(cursor.getColumnIndex("week")), GC);      // TextViewのカスタマイズ処理
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



        /**ヘッダーレイアウト**/
        TableLayout headerTable = (TableLayout) view.findViewById(R.id.headerTable);
        TableRow rowHeader      = new TableRow(getActivity());    // 行を作成
        rowHeader.setPadding(2, 2, 2, 2);       // 行のパディングを指定(左, 上, 右, 下)
        rowHeader.setBackgroundResource(R.drawable.row_head);

        // ヘッダー：日付
        final TextView headeDate = setTextItem("日付", GC);            // TextViewのカスタマイズ処理
        headeDate.setTextColor(Color.WHITE);
        headeDate.setTextSize(12);
        headeDate.setTypeface(meiryobType);
        //TableRow.LayoutParams paramsDate = setParams(0.2f);       // LayoutParamsのカスタマイズ処理
        // ヘッダー：退社時間
        final TextView headerQuitTime = setTextItem("退社時間", GC);
        headerQuitTime.setTextColor(Color.WHITE);
        headerQuitTime.setTextSize(12);
        headerQuitTime.setTypeface(meiryobType);
        //TableRow.LayoutParams paramsQuitTime = setParams(0.3f);
        // ヘッダー：残業時間
        final TextView headerOverTime = setTextItem("残業時間", GC);
        headerOverTime.setTextColor(Color.WHITE);
        headerOverTime.setTextSize(12);
        headerOverTime.setTypeface(meiryobType);
        //TableRow.LayoutParams paramsOverTime = setParams(0.2f);
        // ヘッダー：曜日
        final TextView headerWeek = setTextItem("曜日", GC);
        headerWeek.setTextColor(Color.WHITE);
        headerWeek.setTextSize(12);
        headerWeek.setTypeface(meiryobType);
        //TableRow.LayoutParams paramsWeek = setParams(0.1f);
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
        final TableLayout mTableLayoutList = (TableLayout) view.findViewById(R.id.tableLayoutList);

        /** 非同期処理*/
        AsyncTask<Void, Runnable, Void> task = new AsyncTask<Void, Runnable, Void>() {
            Cursor result =null; // 処理その1の結果
            ProgressDialog dialog;
            @Override
            protected Void doInBackground(Void... params) {
                // 処理その1
                final int result1 = 0; // 処理その1の結果

                /**DB接続 実行処理**/
                db.beginTransaction();
                try {
                    final Cursor cursor = db.rawQuery("SELECT basic_date,leaving_date,over_time_date,week FROM " + timeUtil.createTableName() + " WHERE year_month_date=? ORDER BY basic_date LIMIT 31;", new String[]{timeUtil.getCurrentYearMonthHyphen()});
                    result = cursor;
                } catch (SQLException ex) {
                    Log.e("SELECT ERROR", ex.toString());
                } finally {
                    db.endTransaction();
                }
                publishProgress(new Runnable() {
                    @Override
                    public void run() {
                        // UIスレッドでの処理その1
                        // ...
                        // ここでは先の処理その1の結果をfinal変数を用いて受けとって利用できる
                        if (result.moveToFirst()) {
                            do {
                                TableRow row = new TableRow(getActivity());          // 行を作成
                                //row.setPadding(1, 1, 1, 1);             // 行のパディングを指定(左, 上, 右, 下)
                                // 日付
                                final TextView textDate = setTextItem(result.getString(result.getColumnIndex("basic_date")), GC);     // TextViewのカスタマイズ処理
                                // 退社時間
                                final TextView textsQuitTime = setTextItem(result.getString(result.getColumnIndex("leaving_date")), GL);      // TextViewのカスタマイズ処理
                                // 退社時間
                                final TextView textOverTime = setTextItem(result.getString(result.getColumnIndex("over_time_date")), GC);      // TextViewのカスタマイズ処理
                                // 曜日
                                final TextView textWeek = setTextItem(result.getString(result.getColumnIndex("week")), GC);      // TextViewのカスタマイズ処理
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
                            } while (result.moveToNext());

                        } else {
                            Log.e("SELECT ERROR", "検索結果 0件");
                            Toast.makeText(getActivity(), "検索結果 0件", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return null;
            }

            @Override
            protected void onPreExecute() {
                //タスク開始
                // 進捗ダイアログ表示
                /*dialog = new ProgressDialog(getContext());
                dialog.setMessage("now progressing...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(false);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setProgress(0);
                dialog.setMax(100);
                dialog.show();*/
            }

            @Override
            protected void onProgressUpdate(Runnable... values) {
                for (Runnable runnable : values) {
                    runnable.run();
                }
            }
        };
        task.execute();


        /**DB接続 実行処理**/
        /*db.beginTransaction();
        try {
            colorFlg = 1;
            final Cursor cursor = db.rawQuery("SELECT basic_date,leaving_date,over_time_date,week FROM " + timeUtil.createTableName() + " WHERE year_month_date=? ORDER BY basic_date;", new String[]{timeUtil.getCurrentYearMonthHyphen()});
            try {
                if (cursor.moveToFirst()) {
                    do {
                        TableRow row = new TableRow(getActivity());          // 行を作成
                        //row.setPadding(1, 1, 1, 1);             // 行のパディングを指定(左, 上, 右, 下)
                        // 日付
                        final TextView textDate = setTextItem(cursor.getString(cursor.getColumnIndex("basic_date")), GC);     // TextViewのカスタマイズ処理
                        // 退社時間
                        final TextView textsQuitTime = setTextItem(cursor.getString(cursor.getColumnIndex("leaving_date")), GL);      // TextViewのカスタマイズ処理
                        // 退社時間
                        final TextView textOverTime = setTextItem(cursor.getString(cursor.getColumnIndex("over_time_date")), GC);      // TextViewのカスタマイズ処理
                        // 曜日
                        final TextView textWeek = setTextItem(cursor.getString(cursor.getColumnIndex("week")), GC);      // TextViewのカスタマイズ処理
                        *//******************* フォント調整 *******************//*
                        textDate.setTextSize(11);
                        textsQuitTime.setTextSize(11);
                        textOverTime.setTextSize(11);
                        textWeek.setTextSize(11);
                        textDate.setTypeface(meiryoType);
                        textsQuitTime.setTypeface(meiryoType);
                        textOverTime.setTypeface(meiryoType);
                        textWeek.setTypeface(meiryoType);
                        *//******************* フォント調整 *******************//*

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
        }*/
        return view;
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
    }

    /***
     * フォアグラウンドでなくなった場合に呼び出される
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /***
     * Fragmentの内部のViewリソースの整理を行う
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
    }

    /***
     * Fragmentが破棄される時、最後に呼び出される
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /***
     * Activityの関連付けから外された時に呼び出される
     */
    @Override
    public void onDetach() {
        super.onDetach();
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
