package jp.co.tennti.timerecord;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import jp.co.tennti.timerecord.commonUtils.BitmapUtils;
import jp.co.tennti.timerecord.commonUtils.GeneralUtils;
import jp.co.tennti.timerecord.commonUtils.TimeUtils;
import jp.co.tennti.timerecord.contacts.Constants;
import jp.co.tennti.timerecord.daoUtils.MySQLiteOpenHelper;


public class HolidayFragment extends Fragment {

    private Bitmap mainImage = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_4444);
    private static ImageButton allHolidayRegistrButton = null;
    private static ImageButton amHolidayRegistrButton = null;
    private static ImageButton pmHolidayRegistrButton = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(getActivity().getApplicationContext());
        final SQLiteDatabase db  = helper.getWritableDatabase();
        final TimeUtils timeUtil = new TimeUtils();
        final View view          = inflater.inflate(R.layout.fragment_holiday, container, false);

        final Resources resource = getResources();
        if(mainImage != null){
            mainImage.recycle();
        }
        mainImage = BitmapFactory.decodeResource(resource, R.mipmap.main_disp_kongou);
        final ImageView imgView = (ImageView)view.findViewById(R.id.contentImageView);

        imgView.setImageDrawable(null);
        imgView.setImageBitmap(null);
        BitmapUtils bu = new BitmapUtils();
        DisplayMetrics displayMetrics = bu.getDisplayMetrics(getContext());
        imgView.setImageBitmap(bu.resize(mainImage,displayMetrics.widthPixels,displayMetrics.heightPixels));
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);

        /************ ボタン設定 start ************/
        // ボタンを設定
        allHolidayRegistrButton = (ImageButton)view.findViewById(R.id.allHolidayRegistrButton);
        amHolidayRegistrButton  = (ImageButton)view.findViewById(R.id.amHolidayRegistrButton);
        pmHolidayRegistrButton  = (ImageButton)view.findViewById(R.id.pmHolidayRegistrButton);
        allHolidayRegistrButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_allholiday));
        amHolidayRegistrButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_amhalfholiday));
        pmHolidayRegistrButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_pmhalfholiday));


        /** 初期表示時にボタンを非活性にする判定**/
        if ( helper.isCurrentDate(db,timeUtil.getCurrentTableName(),timeUtil.getCurrentYearMonthDay()) ) {
            allHolidayRegistrButton.setEnabled(false);
            allHolidayRegistrButton.setColorFilter(Color.argb(100, 0, 0, 0));
            amHolidayRegistrButton.setEnabled(false);
            amHolidayRegistrButton.setColorFilter(Color.argb(100, 0, 0, 0));
            pmHolidayRegistrButton.setEnabled(false);
            pmHolidayRegistrButton.setColorFilter(Color.argb(100, 0, 0, 0));
/*            final Cursor cursor = db.rawQuery("SELECT * FROM "+timeUtil.getCurrentTableName()+" WHERE basic_date = ?", new String[]{timeUtil.getCurrentYearMonthDay()});
            try {
                if(cursor != null && cursor.moveToNext()){
                    cursor.moveToFirst();
                    if( cursor.getString(cursor.getColumnIndex("holiday_flag")).equals("1") ){
                        allHolidaySwitch.setChecked(true);
                    }
                    if( cursor.getString(cursor.getColumnIndex("holiday_flag")).equals("2") ){
                        amHalfHolidaySwitch.setChecked(true);
                    }
                    if( cursor.getString(cursor.getColumnIndex("holiday_flag")).equals("3") ){
                        pmHalfHolidaySwitch.setChecked(true);
                    }
                }
            } catch (SQLException e) {
                GeneralUtils.createErrorDialog(getActivity(), "SQL SELECT エラー", "活性判定時のSELECT 処理に失敗しました:" + e.getLocalizedMessage(),"OK");
                Log.e("SQLException SELECT", e.toString());
            } finally {
                cursor.close();
            }*/
        }
        /************ ボタン設定 end ************/

        /************ 全休登録ボタン start ************/
        // リスナーをボタンに登録
        allHolidayRegistrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTimeRecord(db , Constants.ALL_DAYS_HOLIDAY_FLAG);
                /*db.beginTransaction();
                try {
                    final RandGeneratUtils randGenerat = new RandGeneratUtils();
                    final TimeUtils timeUtil = new TimeUtils();
                    String overtime = Constants.TIME_ZERO;//timeUtil.getTimeDiff(timeUtil.conTargetDateFullSlash(timeUtil.getCurrentDate()));
                    String holidayFlag = Constants.ALL_DAYS_HOLIDAY_FLAG;
*//*                    if (holidaySwitch.isChecked()) {
                        holidayFlag = Constants.ALL_DAYS_HOLIDAY_FLAG;
                        overtime    = Constants.TIME_ZERO;
                    }
                    if (amHalfHolidaySwitch.isChecked()) {
                        holidayFlag = Constants.AM_HALF_HOLIDAY_FLAG;
                    }
                    if (pmHalfHolidaySwitch.isChecked()) {
                        holidayFlag = Constants.PM_HALF_HOLIDAY_FLAG;
                        overtime    = Constants.TIME_ZERO;
                    }*//*
                    //アカウント名取得
                    TextView accountName = (TextView) getActivity().findViewById(R.id.accountName);
                    System.out.println(accountName.getText().toString());
                    final SQLiteStatement statement = db.compileStatement("INSERT INTO " + timeUtil.getCurrentTableName() + Constants.INSERT_SQL_VALUES);
                    try {
                        statement.bindString(1, timeUtil.getCurrentYearMonthDay());
                        statement.bindString(2, timeUtil.getCurrentDate());
                        statement.bindString(3, overtime);
                        statement.bindString(4, timeUtil.getCurrentWeekOmit());
                        statement.bindString(5, holidayFlag);
                        statement.bindString(6, accountName.getText().toString());
                        statement.executeInsert();
                        allHolidayRegistrButton.setEnabled(false);
                        allHolidayRegistrButton.setColorFilter(Color.argb(100, 0, 0, 0));
                        // 第3引数は、表示期間（LENGTH_SHORT、または、LENGTH_LONG）
                        Toast.makeText(getActivity(), "現在時刻を登録しました", Toast.LENGTH_SHORT).show();
                    } catch (SQLException ex) {
                        GeneralUtils.createErrorDialog(getActivity(), "SQL INSERT エラー", "insert処理に失敗しました:" + ex.getLocalizedMessage(), "OK");
                        Log.e("SQLException INSERT", ex.toString());
                    } finally {
                        statement.close();
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }*/
            }
        });
        /************ 全休登録ボタン end ************/

        /************ 午前休登録ボタン start ************/
        // リスナーをボタンに登録
        amHolidayRegistrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTimeRecord(db , Constants.AM_HALF_HOLIDAY_FLAG);
            }
        });
        /************ 午前休登録ボタン end ************/
        /************ 午後休登録ボタン start ************/
        // リスナーをボタンに登録
        pmHolidayRegistrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTimeRecord(db , Constants.PM_HALF_HOLIDAY_FLAG);
            }
        });
        /************ 午後休登録ボタン end ************/

        /************ 制御ボタン start ************/
        // ボタンを設定
        final ImageButton controlButton = (ImageButton)view.findViewById(R.id.controlButton);
        controlButton.setImageBitmap(null);
        controlButton.setImageDrawable(null);
        controlButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_permit_switch));

        // リスナーをボタンに登録
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allHolidayRegistrButton.setEnabled(true);
                allHolidayRegistrButton.setColorFilter(null);
                amHolidayRegistrButton.setEnabled(true);
                amHolidayRegistrButton.setColorFilter(null);
                pmHolidayRegistrButton.setEnabled(true);
                pmHolidayRegistrButton.setColorFilter(null);
/*                allHolidaySwitch.setChecked(false);
                amHalfHolidaySwitch.setChecked(false);
                pmHalfHolidaySwitch.setChecked(false);*/
            }
        });
        /************ 制御ボタン end ************/
        /************ 削除ボタン start ************/
        // ボタンを設定
        final ImageButton deleteButton = (ImageButton)view.findViewById(R.id.deleteButtonMain);
        deleteButton.setImageBitmap(null);
        deleteButton.setImageDrawable(null);
        deleteButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_delete_switch));

        // リスナーをボタンに登録
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ダイアログの生成
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                // アラートダイアログのタイトルを設定します
                alertDialogBuilder.setTitle("指定日削除ダイアログ");
                // アラートダイアログのメッセージを設定します
                final TimeUtils timeUtil = new TimeUtils();
                alertDialogBuilder.setMessage(timeUtil.getCurrentYearMonthDay()+"のデータ削除を行いますがよろしいですか。");
                // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                alertDialogBuilder.setNeutralButton("実行",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.beginTransaction();
                                try {
                                    final TimeUtils timeUtil = new TimeUtils();
                                    final SQLiteStatement statement = db.compileStatement("DELETE FROM "+timeUtil.getCurrentTableName()+" WHERE basic_date=?");
                                    try {
                                        /**年月の判定 start**/
                                        /**年月の判定 end**/
                                        statement.bindString(1, timeUtil.getCurrentYearMonthDay());
                                        statement.executeUpdateDelete();
                                        Toast.makeText(getActivity(), "対象日付のデータを削除しました", Toast.LENGTH_SHORT).show();
                                    }  catch (SQLException ex) {
                                        GeneralUtils.createErrorDialog(getActivity(),"SQL DELETE エラー","delete処理に失敗しました:" + ex.getLocalizedMessage(),"OK");
                                        Log.e("SQLException DELETE", ex.toString());
                                    } finally {
                                        statement.close();
                                    }
                                    db.setTransactionSuccessful();
                                } finally {
                                    db.endTransaction();
                                }
                            }
                        });
                alertDialogBuilder.setNegativeButton(
                        "cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                // アラートダイアログのキャンセルが可能かどうかを設定します
                alertDialogBuilder.setCancelable(true);
                AlertDialog alertDialog = alertDialogBuilder.create();
                // アラートダイアログを表示します
                alertDialog.show();
            }
        });
        /************ 削除ボタン end ************/
        return view;
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
        ImageView imgView = (ImageView)getActivity().findViewById(R.id.contentImageView);
        imgView.setImageBitmap(null);
        imgView.setImageDrawable(null);
        /************ 登録ボタン ************/
        allHolidayRegistrButton = (ImageButton)getActivity().findViewById(R.id.allHolidayRegistrButton);
        allHolidayRegistrButton.setImageDrawable(null);
        allHolidayRegistrButton.setImageBitmap(null);
        allHolidayRegistrButton.setOnClickListener(null);
        allHolidayRegistrButton = null;
        amHolidayRegistrButton = (ImageButton)getActivity().findViewById(R.id.amHolidayRegistrButton);
        amHolidayRegistrButton.setImageDrawable(null);
        amHolidayRegistrButton.setImageBitmap(null);
        amHolidayRegistrButton.setOnClickListener(null);
        amHolidayRegistrButton = null;
        pmHolidayRegistrButton = (ImageButton)getActivity().findViewById(R.id.pmHolidayRegistrButton);
        pmHolidayRegistrButton.setImageDrawable(null);
        pmHolidayRegistrButton.setImageBitmap(null);
        pmHolidayRegistrButton.setOnClickListener(null);
        pmHolidayRegistrButton = null;
        /************ 制御ボタン ************/
        ImageButton controlButton = (ImageButton)getActivity().findViewById(R.id.controlButton);
        controlButton.setImageBitmap(null);
        controlButton.setImageDrawable(null);
        controlButton.setOnClickListener(null);
        /************ 削除ボタン ************/
        ImageButton deleteButton = (ImageButton) getActivity().findViewById(R.id.deleteButtonMain);
        deleteButton.setImageBitmap(null);
        deleteButton.setImageDrawable(null);
        /************ 休暇関連スイッチ ************/
        //allHolidaySwitch = (android.support.v7.widget.SwitchCompat)getActivity().findViewById(R.id.allHolidaySwitch);
        /*allHolidaySwitch.setOnClickListener(null);
        allHolidaySwitch.setTypeface(null);
        allHolidaySwitch = null;*/
        //amHalfHolidaySwitch = (android.support.v7.widget.SwitchCompat)getActivity().findViewById(R.id.amHalfHolidaySwitch);
        /*amHalfHolidaySwitch.setOnClickListener(null);
        amHalfHolidaySwitch.setTypeface(null);
        amHalfHolidaySwitch = null;*/
        //pmHalfHolidaySwitch = (android.support.v7.widget.SwitchCompat)getActivity().findViewById(R.id.pmHalfHolidaySwitch);
        /*pmHalfHolidaySwitch.setOnClickListener(null);
        pmHalfHolidaySwitch.setTypeface(null);
        pmHalfHolidaySwitch = null;*/

        BitmapUtils.cleanupView(getView());
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

    /***
     * 3種類のボタンごとのデータ登録
     */
    public void insertTimeRecord(SQLiteDatabase db ,String holidayFlag) {
        db.beginTransaction();
        try {
            final TimeUtils timeUtil = new TimeUtils();
            String overtime = timeUtil.getTimeDiff(timeUtil.conTargetDateFullSlash(timeUtil.getCurrentDate()));
            if (holidayFlag.equals(Constants.ALL_DAYS_HOLIDAY_FLAG)) {
                overtime = Constants.TIME_ZERO;
            }
            if (holidayFlag.equals(Constants.AM_HALF_HOLIDAY_FLAG)) {
                //holidayFlag = Constants.AM_HALF_HOLIDAY_FLAG;
            }
            if (holidayFlag.equals(Constants.PM_HALF_HOLIDAY_FLAG)) {
                overtime = Constants.TIME_ZERO;
            }
            //アカウント名取得
            TextView accountName = (TextView)getActivity().findViewById(R.id.accountName);
            final SQLiteStatement statement = db.compileStatement("INSERT INTO " + timeUtil.getCurrentTableName() + Constants.INSERT_SQL_VALUES);
            try {
                statement.bindString(1, timeUtil.getCurrentYearMonthDay());
                statement.bindString(2, timeUtil.getCurrentDate());
                statement.bindString(3, overtime);
                statement.bindString(4, timeUtil.getCurrentWeekOmit());
                statement.bindString(5, holidayFlag);
                statement.bindString(6, accountName.getText().toString());
                statement.executeInsert();
                allHolidayRegistrButton.setEnabled(false);
                allHolidayRegistrButton.setColorFilter(Color.argb(100, 0, 0, 0));
                amHolidayRegistrButton.setEnabled(false);
                amHolidayRegistrButton.setColorFilter(Color.argb(100, 0, 0, 0));
                pmHolidayRegistrButton.setEnabled(false);
                pmHolidayRegistrButton.setColorFilter(Color.argb(100, 0, 0, 0));
                /*if (holidayFlag.equals(Constants.ALL_DAYS_HOLIDAY_FLAG)) {
                    allHolidaySwitch.setChecked(true);
                }
                if (holidayFlag.equals(Constants.AM_HALF_HOLIDAY_FLAG)) {
                    amHalfHolidaySwitch.setChecked(true);
                }
                if (holidayFlag.equals(Constants.PM_HALF_HOLIDAY_FLAG)) {
                    pmHalfHolidaySwitch.setChecked(true);
                }*/
                // 第3引数は、表示期間（LENGTH_SHORT、または、LENGTH_LONG）
                Toast.makeText(getActivity(), "現在時刻を登録しました", Toast.LENGTH_SHORT).show();
            }  catch (SQLException ex) {
                GeneralUtils.createErrorDialog(getActivity(),"SQL INSERT エラー","insert処理に失敗しました:" + ex.getLocalizedMessage(),"OK");
                Log.e("SQLException INSERT", ex.toString());
            } finally {
                statement.close();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return;
    }
}
