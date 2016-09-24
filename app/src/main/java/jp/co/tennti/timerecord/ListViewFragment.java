package jp.co.tennti.timerecord;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import jp.co.tennti.timerecord.commonUtils.FontUtils;
import jp.co.tennti.timerecord.commonUtils.GeneralUtils;
import jp.co.tennti.timerecord.commonUtils.ListViewUtils;
import jp.co.tennti.timerecord.AsyncTaskUtils.TargetListAsyncTask;
import jp.co.tennti.timerecord.commonUtils.TimeUtils;
import jp.co.tennti.timerecord.contacts.Constants;
import jp.co.tennti.timerecord.daoUtils.MySQLiteOpenHelper;


public class ListViewFragment extends Fragment {
    private Bitmap mainImage = Bitmap.createBitmap(64, 64, Bitmap.Config.RGB_565);
    TextView dateTextView = null;
    AlertDialog.Builder alertDialogBuilder = null;
    View contentView = null;
    TextView totalText = null;
    /**
     * ヘッダーサイズ
     */
    TableRow.LayoutParams paramsDate     = setParams(0.2f);
    TableRow.LayoutParams paramsQuitTime = setParams(0.3f);
    TableRow.LayoutParams paramsOverTime = setParams(0.2f);
    TableRow.LayoutParams paramsWeek     = setParams(0.1f);
    TableRow.LayoutParams paramsHoliday  = setParams(0.1f);

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
        //final Typeface meiryoType  = FontUtils.getTypefaceFromAssetsZip(getContext(),"font/meiryo_first_level.zip");
        final Typeface meiryobType = FontUtils.getTypefaceFromAssetsZip(getContext(),"font/meiryob_first_level.zip");

        final TimeUtils timeUtil = new TimeUtils();
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        contentView = inflater.inflate(R.layout.date_picker_dia, null);
        Resources resM = getResources();
        if (mainImage != null) {
            mainImage.recycle();
        }
        mainImage = BitmapFactory.decodeResource(resM, R.mipmap.list_disp_all);
        ImageView imgView = (ImageView) view.findViewById(R.id.listImageView);
        imgView.setImageDrawable(null);
        imgView.setImageBitmap(null);
        imgView.setImageBitmap(mainImage);
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);


        /**日付データ**/
        /**データピッカー**/
        DatePicker datePicker = (DatePicker) contentView.findViewById(R.id.datePickerDia);
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

        dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        dateTextView.setText(timeUtil.getCurrentYearMonthJaCal());
        dateTextView.setTextColor(Color.BLACK);
        dateTextView.setTypeface(meiryobType);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialogBuilder == null) {

                    // 確認ダイアログの生成
                    alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setView(contentView);
                    final TimeUtils timeUtil = new TimeUtils();
                    alertDialogBuilder.setTitle(timeUtil.getCurrentDate());
                    alertDialogBuilder.setMessage("メッセージ");
                    alertDialogBuilder.setPositiveButton(
                            "設定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // OK ボタンクリック処理
                                    DatePicker datePicker = (DatePicker) contentView.findViewById(R.id.datePickerDia);
                                    try {
                                        final int month = datePicker.getMonth() + 1;
                                        final TimeUtils timeUtil = new TimeUtils();
                                        dateTextView.setText(timeUtil.joinTarYYYYMMJaCal(String.valueOf(datePicker.getYear()), month));
                                    } catch (NullPointerException e) {
                                        Log.e("NullPointerException", e.getMessage());
                                    }

                                }
                            });
                    alertDialogBuilder.setNegativeButton(
                            "閉じる",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Cancel ボタンクリック処理
                                }
                            });
                    alertDialogBuilder.create().show();
                } else {

                    final android.view.ViewParent parent = contentView.getParent();
                    if (parent instanceof android.view.ViewManager) {
                        final android.view.ViewManager viewManager = (android.view.ViewManager) parent;
                        viewManager.removeView(contentView);
                    }
                    alertDialogBuilder.setView(contentView);
                    alertDialogBuilder.show();
                }
            }
        });
        /**日付データ**/
        /**データピッカー**/

        /**データピッカー**/
        /**期間変更ボタン**/
        // ボタンを設定
        final ImageButton perCountButton = (ImageButton) view.findViewById(R.id.perSwitchButton);
        perCountButton.setImageBitmap(null);
        perCountButton.setImageDrawable(null);
        perCountButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_period_change_switch));
        // リスナーをボタンに登録
        perCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTextView = (TextView) view.findViewById(R.id.dateTextView);
                String baseTime ="00:00:00";
                if (dateTextView.getText() != null && dateTextView.getText() != "") {
                    final String yearStr  = dateTextView.getText().toString().substring(0, 4);
                    final String monthStr = dateTextView.getText().toString().substring(5, 7);

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

                    // 行を作成
                    TableRow rowHeader = new TableRow(getActivity());
                    // 行のパディングを指定(左, 上, 右, 下)
                    //rowHeader.setPadding(2, 2, 2, 2);
                    rowHeader.setBackgroundResource(R.drawable.row_head);

                    List<HashMap<String, String>> editResultList = new ArrayList<HashMap<String, String>>();
                    TargetListAsyncTask task = new TargetListAsyncTask(getActivity(), db);
                    try {
                        final MySQLiteOpenHelper helper = new MySQLiteOpenHelper(getActivity());
                        if (helper.isTarMonthTable(db, buffer_t.toString())) {
                            editResultList = task.execute(buffer_t.toString()).get();
                            if ( helper.countTargetMonthData(db,buffer_t.toString()) == 0 ) {
                                editResultList = GeneralUtils.createblankTable(buffer.toString());
                            }
                        } else {
                            editResultList = GeneralUtils.createblankTable(buffer.toString());
                        }

                        ListViewUtils lv = new ListViewUtils(getContext());
                        totalText.setText(Constants.TOTAL_OVERTIME_LABEL + lv.createTableRow(editResultList,mTableLayoutList));
                    } catch (InterruptedException e) {
                        Log.e("InterruptedException", e.toString());
                    } catch (ExecutionException e) {
                        Log.e("ExecutionException", e.toString());
                    }
                }
            }
        });
        /**期間変更ボタン**/

        /**合計期間テキスト**/
        totalText = (TextView) view.findViewById(R.id.totalText);
        totalText.setTextColor(Color.WHITE);
        totalText.setTextSize(12);
        totalText.setBackgroundResource(R.drawable.row_footer);
        /**合計期間テキスト**/

        /**ヘッダーレイアウト**/
        TableLayout headerTable = (TableLayout) view.findViewById(R.id.headerTable);
        // 行を作成
        TableRow rowHeader = new TableRow(getActivity());
        // 行のパディングを指定(左, 上, 右, 下)
        //rowHeader.setPadding(-2,-2, -2, -2);
        //rowHeader.setBackgroundResource(R.drawable.row_head);

        // ：日付
        final TextView headeDate =setTextItem("日付", Constants.GRAVITY_CENTER);
        headeDate.setTextColor(Color.WHITE);
        headeDate.setTextSize(12);
        headeDate.setTypeface(meiryobType);
        headeDate.setBackgroundResource(R.drawable.row_head);

        // ：退社時間
        final TextView headerQuitTime = setTextItem("退社時間", Constants.GRAVITY_CENTER);
        headerQuitTime.setTextColor(Color.WHITE);
        headerQuitTime.setTextSize(12);
        headerQuitTime.setTypeface(meiryobType);
        headerQuitTime.setBackgroundResource(R.drawable.row_head);
        // ：残業時間
        final TextView headerOverTime = setTextItem("残業時間", Constants.GRAVITY_CENTER);
        headerOverTime.setTextColor(Color.WHITE);
        headerOverTime.setTextSize(12);
        headerOverTime.setTypeface(meiryobType);
        headerOverTime.setBackgroundResource(R.drawable.row_head);
        // ：曜日
        final TextView headerWeek = setTextItem("曜日", Constants.GRAVITY_CENTER);
        headerWeek.setTextColor(Color.WHITE);
        headerWeek.setTextSize(12);
        headerWeek.setTypeface(meiryobType);
        headerWeek.setBackgroundResource(R.drawable.row_head);
        // ：休暇
        final TextView headerhHoliday = setTextItem("休暇", Constants.GRAVITY_CENTER);
        headerhHoliday.setTextColor(Color.WHITE);
        headerhHoliday.setTextSize(12);
        headerhHoliday.setTypeface(meiryobType);
        headerhHoliday.setBackgroundResource(R.drawable.row_head);
        // rowHeaderにヘッダータイトルを追加
        rowHeader.addView(headeDate, paramsDate);                 // ヘッダー：日付
        rowHeader.addView(headerQuitTime, paramsQuitTime);        // ヘッダー：退社時間
        rowHeader.addView(headerOverTime, paramsOverTime);        // ヘッダー：残業時間
        rowHeader.addView(headerWeek, paramsWeek);                // ヘッダー：曜日
        rowHeader.addView(headerhHoliday, paramsHoliday);         // ヘッダー：休暇

        //rowHeader.setBackgroundResource(R.drawable.row_deco1);  // 背景

        // TableLayoutにrowHeaderを追加
        headerTable.addView(rowHeader);
        List<HashMap<String, String>> onloadResultList = new ArrayList<>();
        /**ヘッダーレイアウト**/
        /**一覧レイアウト**/
        final TableLayout mTableLayoutList = (TableLayout) view.findViewById(R.id.tableLayoutList);

        if(!helper.isTarMonthTable(db,timeUtil.getCurrentTableName().toString())){
            //perCountButton.setEnabled(false);
            //perCountButton.setColorFilter(Color.argb(100, 0, 0, 0));
            onloadResultList = GeneralUtils.createblankTable(timeUtil.getCurrentYearMonthHyphen());
            ListViewUtils lv = new ListViewUtils(getContext());
            totalText.setText(Constants.TOTAL_OVERTIME_LABEL + lv.createTableRow(onloadResultList,mTableLayoutList));
            Toast.makeText(getActivity(), "テーブルが存在しません。", Toast.LENGTH_LONG).show();
            return view;
        }

        //OnloadListAsyncTask task = new OnloadListAsyncTask(getActivity(), db);
        TargetListAsyncTask task = new TargetListAsyncTask(getActivity(), db);
        try {
            onloadResultList = task.execute(timeUtil.getCurrentTableName().toString()).get();
            if(onloadResultList.size() == 0){
                onloadResultList = GeneralUtils.createblankTable(timeUtil.getCurrentYearMonthHyphen());
            }
            ListViewUtils lv = new ListViewUtils(getContext());
            totalText.setText(Constants.TOTAL_OVERTIME_LABEL + lv.createTableRow(onloadResultList,mTableLayoutList));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("InterruptedException", e.toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e("ExecutionException", e.toString());
        }
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
        if(mainImage != null){
            mainImage.recycle();
            mainImage = null;
        }
        ImageView imgView = (ImageView) getActivity().findViewById(R.id.listImageView);
        imgView.setImageBitmap(null);
        imgView.setImageDrawable(null);
        TableLayout headerTable = (TableLayout) getActivity().findViewById(R.id.headerTable);
        headerTable.setOnClickListener(null);
        headerTable.setBackground(null);
        headerTable.removeAllViews();
        /************ 期間変更ボタン ************/
        ImageButton timeSwitchButton = (ImageButton) getActivity().findViewById(R.id.perSwitchButton);
        timeSwitchButton.setImageBitmap(null);
        timeSwitchButton.setImageDrawable(null);
        timeSwitchButton.setOnClickListener(null);
        dateTextView = (TextView) getActivity().findViewById(R.id.dateTextView);
        dateTextView.setOnClickListener(null);
        /************ グローバル変数 ************/
        dateTextView = null;
        alertDialogBuilder = null;
        contentView = null;
        totalText = null;
        TableLayout mTableLayoutList = (TableLayout) getActivity().findViewById(R.id.tableLayoutList);
        mTableLayoutList.removeAllViews();mTableLayoutList.setBackground(null);
        mTableLayoutList = null;
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
    }

    /**
     * 行の各項目のLayoutParamsカスタマイズ処理
     * setParams()
     */
    private TableRow.LayoutParams setParams(float f) {
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, Constants.WRAP_CONTENT);
        params.weight = f;      //weight(行内でのテキストごとの比率)
        //params.setMargins(12,12,12,12);
        return params;
    }

}
