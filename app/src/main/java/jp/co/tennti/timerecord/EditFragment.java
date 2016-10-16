package jp.co.tennti.timerecord;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;

import jp.co.tennti.timerecord.commonUtils.BitmapUtils;
import jp.co.tennti.timerecord.commonUtils.FontUtils;
import jp.co.tennti.timerecord.commonUtils.GeneralUtils;
import jp.co.tennti.timerecord.commonUtils.RandGeneratUtils;
import jp.co.tennti.timerecord.commonUtils.TimeUtils;
import jp.co.tennti.timerecord.contacts.Constants;
import jp.co.tennti.timerecord.daoUtils.MySQLiteOpenHelper;


public class EditFragment extends Fragment {
    private Bitmap mainImage = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_4444);

    TextView dateTextView         = null;
    TextView timeTextView         = null;
    AlertDialog.Builder builder_d = null;
    AlertDialog.Builder builder_t = null;
    View datePickerView           = null;
    View timePickerView           = null;
    android.support.v7.widget.SwitchCompat allHolidaySwitch       = null;
    android.support.v7.widget.SwitchCompat amHalfHolidaySwitch    = null;
    android.support.v7.widget.SwitchCompat pmHalfHolidaySwitch    = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** font 指定 */
        final Typeface meiryobType = FontUtils.getTypefaceFromAssetsZip(getContext(),"font/meiryob_first_level.zip");
        final Typeface meiryoType  = FontUtils.getTypefaceFromAssetsZip(getContext(),"font/meiryo_first_level.zip");

        final MySQLiteOpenHelper helper = new MySQLiteOpenHelper(getActivity().getApplicationContext());
        final SQLiteDatabase db = helper.getWritableDatabase();

        final View view = inflater.inflate(R.layout.fragment_edit, container, false);

        final Resources resource = getResources();
        if(mainImage!=null){
            mainImage.recycle();
        }
        mainImage = BitmapFactory.decodeResource(resource, R.mipmap.edit_disp_hiei);
        final ImageView imgView = (ImageView)view.findViewById(R.id.contentImageView);

        imgView.setImageDrawable(null);
        imgView.setImageBitmap(null);
        BitmapUtils bu = new BitmapUtils();
        DisplayMetrics displayMetrics = bu.getDisplayMetrics(getContext());
        imgView.setImageBitmap(bu.resize(mainImage,displayMetrics.widthPixels,displayMetrics.heightPixels));
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        
        final TimeUtils timeUtil = new TimeUtils();



        /**日付データ**/
        datePickerView = inflater.inflate(R.layout.date_picker_dia, null);
        /**データピッカー**/
        final DatePicker datePicker = (DatePicker)datePickerView.findViewById(R.id.datePickerDia);
        // 初期値を設定する 1999年12月31日
        //datePicker.updateDate(1999, 11, 31);
        //2011/01/01~3000/12/31
        final Date minDate = new Date(111, 0, 1);
        final Date maxDate = new Date(1100, 12, 31);
        datePicker.setMinDate(minDate.getTime());
        datePicker.setMaxDate(maxDate.getTime());

        /**データピッカー**/
        dateTextView = (TextView)view.findViewById(R.id.editDateTextView);
        dateTextView.setText(timeUtil.getCurrentYearMonthDayJaCal());
        dateTextView.setTypeface(meiryobType);
        dateTextView.setTextColor(Color.BLACK);
        dateTextView.setTextSize(18);

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.view.ViewParent parent = datePickerView.getParent ();
                if (parent instanceof android.view.ViewManager) {
                    final android.view.ViewManager viewManager = (android.view.ViewManager) parent;
                    viewManager.removeView (datePickerView);
                }

                // 確認ダイアログの生成
                builder_d = new AlertDialog.Builder(getContext());

                builder_d.setView(datePickerView);
                builder_d.setTitle("登録日時の選択(年月日)");
                //builder.setMessage("メッセージ");
                builder_d.setPositiveButton(
                        "設定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // OK ボタンクリック処理
                                final DatePicker datePicker = (DatePicker) datePickerView.findViewById(R.id.datePickerDia);
                                try{
                                    TimeUtils timeUtil = new TimeUtils();
                                    /**年月の判定 start**/
                                    String yearMonthDays ="1999年01月01日";
                                    /**年月日**/
                                    yearMonthDays = timeUtil.getJoinYYYYMMDDJaCal(datePicker.getYear(),datePicker.getMonth()+1,datePicker.getDayOfMonth());
                                    /**年月の判定 end**/
                                    dateTextView.setText(yearMonthDays);
                                    setHolidayFlag(db, yearMonthDays);
                                } catch (NullPointerException e){
                                    Log.d("NullPointerException",e.getMessage());
                                }
                            }
                        });
                builder_d.setNegativeButton(
                        "閉じる",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel ボタンクリック処理
                            }
                        });
                // 表示
                builder_d.create().show();
            }
        });
        /**日付データ**/
        /**データピッカー**/

        /**時間データ**/
        timePickerView = inflater.inflate(R.layout.time_picker_dia, null);

        timeTextView = (TextView)view.findViewById(R.id.editTimeTextView);
        timeTextView.setText(timeUtil.getCurrentHourMinuteJaCal());
        timeTextView.setTypeface(meiryobType);
        timeTextView.setTextColor(Color.BLACK);
        timeTextView.setTextSize(18);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.view.ViewParent parent = timePickerView.getParent();
                if (parent instanceof android.view.ViewManager) {
                    final android.view.ViewManager viewManager = (android.view.ViewManager) parent;
                    viewManager.removeView(timePickerView);
                }

                // 確認ダイアログの生成
                builder_t = new AlertDialog.Builder(getContext());

                builder_t.setView(timePickerView);
                builder_t.setTitle("登録時間の選択(時分)");
                //builder.setMessage("メッセージ");
                builder_t.setPositiveButton(
                        "設定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // OK ボタンクリック処理
                                try {
                                    final TimePicker timePicker = (TimePicker) timePickerView.findViewById(R.id.timePickerDia);
                                    int hour;
                                    int minute;
                                    int currentApiVersion = Build.VERSION.SDK_INT;
                                    if (currentApiVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                                        hour = timePicker.getHour();
                                        minute = timePicker.getMinute();
                                    } else {
                                        hour = timePicker.getCurrentHour();
                                        minute = timePicker.getCurrentMinute();
                                    }
                                    final TimeUtils timeUtil = new TimeUtils();
                                    /**時分の判定 start**/
                                    String hourMinute = "00時00分";
                                    hourMinute = timeUtil.getTargetHourMinuteJaCal(hour, minute);
                                    /**時分の判定 end**/
                                    timeTextView.setText(hourMinute);
                                } catch (NullPointerException e) {
                                    Log.d("NullPointerException", e.getMessage());
                                }
                            }
                        });
                builder_t.setNegativeButton(
                        "閉じる",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel ボタンクリック処理
                            }
                        });
                // 表示
                builder_t.create().show();
            }
        });


        /************ 有給関連スイッチ start ************/
        allHolidaySwitch    = (android.support.v7.widget.SwitchCompat)view.findViewById(R.id.allHolidaySwitchEdit);
        amHalfHolidaySwitch = (android.support.v7.widget.SwitchCompat)view.findViewById(R.id.amHalfHolidaySwitchEdit);
        pmHalfHolidaySwitch = (android.support.v7.widget.SwitchCompat)view.findViewById(R.id.pmHalfHolidaySwitchEdit);
        allHolidaySwitch.setTypeface(meiryoType);
        amHalfHolidaySwitch.setTypeface(meiryoType);
        pmHalfHolidaySwitch.setTypeface(meiryoType);
        final TextView dateTextViewTemp = (TextView)view.findViewById(R.id.editDateTextView);
        setHolidayFlag(db, dateTextViewTemp.getText().toString());
        // リスナーをボタンに登録
        allHolidaySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amHalfHolidaySwitch.setChecked(false);
                pmHalfHolidaySwitch.setChecked(false);
            }
        });
        amHalfHolidaySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allHolidaySwitch.setChecked(false);
                pmHalfHolidaySwitch.setChecked(false);
            }
        });
        pmHalfHolidaySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allHolidaySwitch.setChecked(false);
                amHalfHolidaySwitch.setChecked(false);
            }
        });
        /************ 有給関連スイッチ end ************/

        /************ 新規登録ボタン start ************/
        // ボタンを設定
        final ImageButton timeCountButton = (ImageButton)view.findViewById(R.id.timeCountButton);
        timeCountButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_edit_times_day_switch));
        // リスナーをボタンに登録
        timeCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.beginTransaction();
                try {
                    final RandGeneratUtils randGenerat = new RandGeneratUtils();
                    final TimeUtils timeUtil = new TimeUtils();

                    /**年月日**/
                    final TextView dateTextViewTemp = (TextView)view.findViewById(R.id.editDateTextView);
                    final StringBuilder builder = new StringBuilder();
                    builder.append("time_record_");
                    builder.append(timeUtil.getTargetYYYY(dateTextViewTemp.getText().toString()));
                    /**DB存在判定**/
                    if (!helper.isTargetTable(db, builder.toString())) {
                        helper.createMonthTable(db , builder.toString());
                    }

                    final SQLiteStatement statement = db.compileStatement("INSERT INTO "+builder.toString() + Constants.INSERT_SQL_VALUES);
                    try {
                        /**年月の判定 start**/
                        String yearMonth    ="1999-01";
                        String yearMonthDay ="1999-01-01";
                        String allDate      ="1999-01-01 00:00:00";

                        /**時分**/
                        final TextView timeTextViewTemp = (TextView)view.findViewById(R.id.editTimeTextView);
                        if ( dateTextViewTemp.getText() != null && dateTextViewTemp.getText() != "" && timeTextViewTemp.getText() != null && timeTextViewTemp.getText() != "") {
                            yearMonth    = timeUtil.getTargetYYYYMMHyphen(dateTextViewTemp.getText().toString());
                            yearMonthDay = timeUtil.conTargetYYYYMMDDHyphen(dateTextViewTemp.getText().toString());
                            allDate      = timeUtil.conTargetDateFullHyphen(dateTextViewTemp.getText().toString(),timeTextViewTemp.getText().toString());
                        }
                        /**年月の判定 end**/
                        /**休日**/
                        String overtime    = timeUtil.getTimeDiff(timeUtil.conTargetDateFullSlash(allDate));
                        String holidayFlag = "0";
                        if (allHolidaySwitch.isChecked()) {
                            holidayFlag = Constants.ALL_DAYS_HOLIDAY_FLAG;
                            overtime    = Constants.TIME_ZERO;
                        }
                        if (amHalfHolidaySwitch.isChecked()) {
                            holidayFlag = Constants.AM_HALF_HOLIDAY_FLAG;
                        }
                        if (pmHalfHolidaySwitch.isChecked()) {
                            holidayFlag = Constants.PM_HALF_HOLIDAY_FLAG;
                            overtime    = Constants.TIME_ZERO;
                        }
                        //アカウント名取得
                        TextView accountCd = (TextView)getActivity().findViewById(R.id.accountMail);
                        //statement.bindString(1, randGenerat.get());
                        statement.bindString(1, yearMonthDay);
                        statement.bindString(2, allDate);
                        statement.bindString(3, overtime);
                        statement.bindString(4, timeUtil.getTargetWeekOmit(yearMonthDay));
                        statement.bindString(5, holidayFlag);
                        statement.bindString(6, accountCd.getText().toString());
                        statement.executeInsert();
                        /*timeCountButton.setEnabled(false);
                        timeCountButton.setColorFilter(Color.argb(100, 0, 0, 0));*/
                        // 第3引数は、表示期間（LENGTH_SHORT、または、LENGTH_LONG）
                        Toast.makeText(getActivity(), "指定時刻を登録しました", Toast.LENGTH_SHORT).show();
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
            }
        });
        /************ 新規登録ボタン end ************/

        /************ 許可ボタン start ************/
        // ボタンを設定
        final ImageButton controlButton = (ImageButton)view.findViewById(R.id.controlButton);


        controlButton.setImageBitmap(null);
        controlButton.setImageDrawable(null);
        controlButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_permit_switch));

        // リスナーをボタンに登録
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeCountButton.setEnabled(true);
                timeCountButton.setColorFilter(null);
                allHolidaySwitch.setChecked(false);
                amHalfHolidaySwitch.setChecked(false);
                pmHalfHolidaySwitch.setChecked(false);
            }
        });

        /************ 許可ボタン end ************/
        /************ 削除ボタン start ************/
        // ボタンを設定
        final ImageButton deleteButton = (ImageButton)view.findViewById(R.id.deleteButtonEdit);
        deleteButton.setImageBitmap(null);
        deleteButton.setImageDrawable(null);
        deleteButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_delete_switch));

        // リスナーをボタンに登録
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ダイアログの生成
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                // アラートダイアログのタイトルを設定します
                alertDialogBuilder.setTitle("指定日削除ダイアログ");
                // アラートダイアログのメッセージを設定します
                final TextView dateTextViewTemp = (TextView)view.findViewById(R.id.editDateTextView);
                final TimeUtils timeUtil = new TimeUtils();

                alertDialogBuilder.setMessage(timeUtil.conTargetYYYYMMDDHyphen(dateTextViewTemp.getText().toString())+"のデータ削除を行いますがよろしいですか。");
                // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                alertDialogBuilder.setNeutralButton("実行",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.beginTransaction();
                                        try {
                                            TimeUtils timeUtil = new TimeUtils();
                                            final TextView dateTextViewTemp = (TextView)view.findViewById(R.id.editDateTextView);

                                            StringBuilder builder = new StringBuilder();
                                            builder.append("time_record_");
                                            builder.append(timeUtil.getTargetYYYY(dateTextViewTemp.getText().toString()));

                                            final SQLiteStatement statement = db.compileStatement("DELETE FROM "+builder.toString()+" WHERE basic_date=?");

                                            try {
                                                /**年月の判定 start**/
                                                String yearMonthDay="1999-01-01";
                                                /**年月日**/

                                                if ( dateTextViewTemp.getText() != null && dateTextViewTemp.getText() != "" ) {
                                                    yearMonthDay = timeUtil.conTargetYYYYMMDDHyphen(dateTextViewTemp.getText().toString());
                                                }
                                                /**年月の判定 end**/
                                                statement.bindString(1, yearMonthDay);
                                                statement.executeUpdateDelete();
                                                // 第3引数は、表示期間（LENGTH_SHORT、または、LENGTH_LONG）
                                                Toast.makeText(getActivity(), "指定日付のデータを登録しました", Toast.LENGTH_SHORT).show();
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
                                 "CANCEL", new DialogInterface.OnClickListener() {
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
        /************ 更新ボタン start ************/
        // ボタンを設定
        final ImageButton upadateButton = (ImageButton)view.findViewById(R.id.updateButtonEdit);
        upadateButton.setImageBitmap(null);
        upadateButton.setImageDrawable(null);
        upadateButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_update_switch));

        // リスナーをボタンに登録
        upadateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.beginTransaction();
                try {
                    final TimeUtils timeUtil = new TimeUtils();
                    final SQLiteStatement statement = db.compileStatement("UPDATE "+timeUtil.getCurrentTableName()+" SET "+
                            " leaving_date=?,overtime=?,week=?,holiday_flag=?,user_cd=? WHERE basic_date = ?");
                    try {
                        /**年月の判定 start**/
                        String yearMonth    ="1999-01";
                        String yearMonthDay ="1999-01-01";
                        String allDate      ="1999-01-01 00:00:00";
                        /**年月日**/
                        final TextView dateTextViewTemp = (TextView)view.findViewById(R.id.editDateTextView);
                        /**時分**/
                        final TextView timeTextViewTemp = (TextView)view.findViewById(R.id.editTimeTextView);
                        if ( dateTextViewTemp.getText() != null && dateTextViewTemp.getText() != "" && timeTextViewTemp.getText() != null && timeTextViewTemp.getText() != "") {
                            yearMonth    = timeUtil.getTargetYYYYMMHyphen(dateTextViewTemp.getText().toString());
                            yearMonthDay = timeUtil.conTargetYYYYMMDDHyphen(dateTextViewTemp.getText().toString());
                            allDate      = timeUtil.conTargetDateFullHyphen(dateTextViewTemp.getText().toString(),timeTextViewTemp.getText().toString());
                        }
                        /**年月の判定 end**/

                        String overtime    = timeUtil.getTimeDiff(timeUtil.conTargetDateFullSlash(allDate));
                        String holidayFlag = "0";
                        if (allHolidaySwitch.isChecked()) {
                            holidayFlag = Constants.ALL_DAYS_HOLIDAY_FLAG;
                            overtime    = Constants.TIME_ZERO;
                        }
                        if (amHalfHolidaySwitch.isChecked()) {
                            holidayFlag = Constants.AM_HALF_HOLIDAY_FLAG;
                        }
                        if (pmHalfHolidaySwitch.isChecked()) {
                            holidayFlag = Constants.PM_HALF_HOLIDAY_FLAG;
                            overtime    = Constants.TIME_ZERO;
                        }
                        //アカウント名取得
                        TextView accountCd = (TextView)getActivity().findViewById(R.id.accountMail);
                        statement.bindString(1, allDate);
                        statement.bindString(2, overtime);
                        statement.bindString(3, timeUtil.getTargetWeekOmit(yearMonthDay));
                        statement.bindString(4, holidayFlag);
                        statement.bindString(5, accountCd.getText().toString());
                        statement.bindString(6, yearMonthDay);
                        statement.executeInsert();
                        // 第3引数は、表示期間（LENGTH_SHORT、または、LENGTH_LONG）
                        Toast.makeText(getActivity(), "指定日付のデータを更新しました", Toast.LENGTH_SHORT).show();
                    }  catch (SQLException e) {
                        GeneralUtils.createErrorDialog(getActivity(),"SQL UPDATE エラー","update処理に失敗しました:" + e.getLocalizedMessage(),"OK");
                        Log.e("SQLException UPDATE", e.toString());
                    } finally {
                        statement.close();
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        });
        /************ 更新ボタン end ************/
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
        ImageButton timeCountButton = (ImageButton)getActivity().findViewById(R.id.timeCountButton);
        timeCountButton.setImageDrawable(null);
        timeCountButton.setImageBitmap(null);
        timeCountButton.setOnClickListener(null);
        /************ 制御ボタン ************/
        ImageButton controlButton = (ImageButton)getActivity().findViewById(R.id.controlButton);
        controlButton.setImageBitmap(null);
        controlButton.setImageDrawable(null);
        controlButton.setOnClickListener(null);
        /************ 削除ボタン ************/
        ImageButton deleteButton = (ImageButton)getActivity().findViewById(R.id.deleteButtonEdit);
        deleteButton.setImageBitmap(null);
        deleteButton.setImageDrawable(null);
        deleteButton.setOnClickListener(null);
        /************ 更新ボタン ************/
        ImageButton updateButton = (ImageButton)getActivity().findViewById(R.id.updateButtonEdit);
        updateButton.setImageBitmap(null);
        updateButton.setImageDrawable(null);
        updateButton.setOnClickListener(null);
        datePickerView.setOnClickListener(null);
        datePickerView = null;
        timePickerView.setOnClickListener(null);
        timePickerView = null;
        dateTextView = null;
        timeTextView = null;
        builder_d = null;
        builder_t = null;
        allHolidaySwitch.setOnClickListener(null);
        allHolidaySwitch = null;
        amHalfHolidaySwitch.setOnClickListener(null);
        amHalfHolidaySwitch = null;
        pmHalfHolidaySwitch.setOnClickListener(null);
        pmHalfHolidaySwitch = null;
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

    public void setHolidayFlag(SQLiteDatabase db ,String targetDate){
        TimeUtils timeUtil = new TimeUtils();
        String targetDateHyphen = timeUtil.conTargetYYYYMMDDHyphen(targetDate);
        final Cursor cursor = db.rawQuery("SELECT * FROM "+timeUtil.getCurrentTableName()+" WHERE basic_date = ?", new String[]{targetDateHyphen});
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
            } else {
                allHolidaySwitch.setChecked(false);
                amHalfHolidaySwitch.setChecked(false);
                pmHalfHolidaySwitch.setChecked(false);
            }
        } catch (SQLException e) {
            GeneralUtils.createErrorDialog(getActivity(), "SQL SELECT エラー", "活性判定時のSELECT 処理に失敗しました:" + e.getLocalizedMessage(),"OK");
            Log.e("SQLException SELECT", e.toString());
        } finally {
            cursor.close();
        }
        return;
    }
}
