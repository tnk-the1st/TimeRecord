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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import jp.co.tennti.timerecord.commonUtils.BitmapUtils;
import jp.co.tennti.timerecord.commonUtils.FontUtils;
import jp.co.tennti.timerecord.commonUtils.GeneralUtils;
import jp.co.tennti.timerecord.commonUtils.TimeUtils;
import jp.co.tennti.timerecord.daoUtils.MySQLiteOpenHelper;


public class EvacuationFragment extends Fragment {
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

        final View view = inflater.inflate(R.layout.fragment_evacuation, container, false);

        final Resources resource = getResources();
        if(mainImage!=null){
            mainImage.recycle();
        }
        mainImage = BitmapFactory.decodeResource(resource, R.mipmap.fleet_haruna);
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
        int day_id = Resources.getSystem().getIdentifier("day", "id", "android");
        int month_id = Resources.getSystem().getIdentifier("month", "id", "android");
        datePicker.findViewById( day_id ).setVisibility( View.GONE );
        datePicker.findViewById( month_id ).setVisibility( View.GONE );
        /**データピッカー**/
        dateTextView = (TextView)view.findViewById(R.id.editDateTextView);
        dateTextView.setText(timeUtil.getCurrentYearJaCal());
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
                                    String year ="1999";
                                    /**年月日**/
                                    year = String.valueOf(datePicker.getYear());
                                    //yearMonthDays = timeUtil.getJoinYYYYMMDDJaCal(datePicker.getYear(),datePicker.getMonth()+1,datePicker.getDayOfMonth());
                                    /**年月の判定 end**/
                                    year+="年";
                                    dateTextView.setText(year);
                                    //setHolidayFlag(db, yearMonthDays);
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



        /************ 退避ボタン start ************/
        // ボタンを設定
        final ImageButton timeCountButton = (ImageButton)view.findViewById(R.id.timeCountButton);
        timeCountButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_edit_times_day_switch));
        // リスナーをボタンに登録
        timeCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimeUtils timeUtil = new TimeUtils();
                /**年月日**/
                final TextView dateTextViewTemp = (TextView) view.findViewById(R.id.editDateTextView);
                StringBuffer builder = new StringBuffer();
                builder.append("time_record_");
                builder.append(timeUtil.getTargetYYYY(dateTextViewTemp.getText().toString()));
                GeneralUtils.exportCSV(getActivity(), builder.toString());
                // 第3引数は、表示期間（LENGTH_SHORT、または、LENGTH_LONG）
                Toast.makeText(getActivity(), dateTextViewTemp.getText().toString() + "のデータを保存しました", Toast.LENGTH_SHORT).show();
            }

        });
        /************ 退避ボタン end ************/

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

                alertDialogBuilder.setMessage(timeUtil.getTargetYYYY(dateTextViewTemp.getText().toString())+"のデータ削除を行いますがよろしいですか。");
                // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                alertDialogBuilder.setNeutralButton("実行",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        TimeUtils timeUtil = new TimeUtils();
                                        final TextView dateTextViewTemp = (TextView)view.findViewById(R.id.editDateTextView);
                                        StringBuffer builder = new StringBuffer();
                                        builder.append("time_record_");
                                        builder.append(timeUtil.getTargetYYYY(dateTextViewTemp.getText().toString()));
                                        GeneralUtils.deleteCSV(builder.toString());
                                        Toast.makeText(getActivity(), dateTextViewTemp.getText().toString() + "のデータを削除しました", Toast.LENGTH_SHORT).show();
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
        /************ 削除ボタン ************/
        ImageButton deleteButton = (ImageButton)getActivity().findViewById(R.id.deleteButtonEdit);
        deleteButton.setImageBitmap(null);
        deleteButton.setImageDrawable(null);
        deleteButton.setOnClickListener(null);

        datePickerView.setOnClickListener(null);
        datePickerView = null;
        timePickerView.setOnClickListener(null);
        timePickerView = null;
        dateTextView = null;
        timeTextView = null;
        builder_d = null;
        builder_t = null;
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

/*    public void setHolidayFlag(SQLiteDatabase db ,String targetDate){
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
    }*/
}
