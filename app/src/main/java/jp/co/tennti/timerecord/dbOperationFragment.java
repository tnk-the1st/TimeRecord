package jp.co.tennti.timerecord;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import jp.co.tennti.timerecord.commonUtils.BitmapUtils;
import jp.co.tennti.timerecord.commonUtils.FontUtils;
import jp.co.tennti.timerecord.commonUtils.GeneralUtils;
import jp.co.tennti.timerecord.commonUtils.TimeUtils;
import jp.co.tennti.timerecord.contacts.Constants;
import jp.co.tennti.timerecord.daoUtils.MySQLiteOpenHelper;


public class DBOperationFragment extends Fragment {
    private Bitmap mainImage = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_4444);

    AlertDialog.Builder builder_d = null;
    AlertDialog.Builder builder_t = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** font 指定 */
        final Typeface meiryobType = FontUtils.getTypefaceFromAssetsZip(getContext(),"font/meiryob_first_level.zip");
        //final Typeface meiryoType  = FontUtils.getTypefaceFromAssetsZip(getContext(),"font/meiryo_first_level.zip");

        final MySQLiteOpenHelper helper = new MySQLiteOpenHelper(getActivity().getApplicationContext());
        final SQLiteDatabase db = helper.getWritableDatabase();

        final View view = inflater.inflate(R.layout.fragment_db_operation, container, false);

        final Resources resource = getResources();
        if(mainImage!=null){
            mainImage.recycle();
        }
        mainImage = BitmapFactory.decodeResource(resource, R.mipmap.fleet_kongou_all_sd);
        final ImageView imgView = (ImageView)view.findViewById(R.id.contentImageView);

        imgView.setImageDrawable(null);
        imgView.setImageBitmap(null);
        BitmapUtils bu = new BitmapUtils();
        DisplayMetrics displayMetrics = bu.getDisplayMetrics(getContext());
        imgView.setImageBitmap(bu.resize(mainImage,displayMetrics.widthPixels,displayMetrics.heightPixels));
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        
        final TimeUtils timeUtil = new TimeUtils();

        /************ 一覧レイアウト start ************/
        final TableLayout csvConfirmList = (TableLayout) view.findViewById(R.id.tableConfirmList);
        csvConfirmList.removeAllViews();

        List<String> cstList = GeneralUtils.getDirCSVNameList();
        for(String csvName :cstList){
            TableRow row = new TableRow(getContext());
            final TableRow.LayoutParams params = new TableRow.LayoutParams(0, 0);
            params.weight = 0.1f;
            params.height = Constants.ROW_HIGHT_SIZE;
            TextView title = new TextView(getContext());
            title.setTextSize(10.0f);
            title.setTextColor(Color.BLACK);
            title.setTypeface(meiryobType);
            title.setGravity(Constants.GRAVITY_CENTER);
            title.setText(csvName);
            row.addView(title, params);
            csvConfirmList.addView(row);
        }
        /************ 一覧レイアウト end ************/

        /************ 退避ボタン start ************/
        // ボタンを設定
        final ImageButton dbZipButton = (ImageButton)view.findViewById(R.id.dbZipButton);
        dbZipButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_zip_switch));
        // リスナーをボタンに登録
        dbZipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtils.createDBZipFile();
                // 第3引数は、表示期間（LENGTH_SHORT、または、LENGTH_LONG）
                Toast.makeText(getActivity(), "DBファイルを圧縮しました", Toast.LENGTH_SHORT).show();
            }

        });
        /************ 退避ボタン end ************/

        /************ DROP Tableボタン start ************/
        // ボタンを設定
        final ImageButton dropTableButton = (ImageButton)view.findViewById(R.id.dropTableButton);
        dropTableButton.setImageBitmap(null);
        dropTableButton.setImageDrawable(null);
        dropTableButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_drop_table_switch));

        // リスナーをボタンに登録
        dropTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ダイアログの生成
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                // アラートダイアログのタイトルを設定します
                alertDialogBuilder.setTitle("DB Table削除ダイアログ");
                // アラートダイアログのメッセージを設定します
                alertDialogBuilder.setMessage("このアプリケーションDBの全テーブルを削除しますがよろしいですか?");
                // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                alertDialogBuilder.setNegativeButton(Constants.CANCEL_CONFIRM_NAME , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                        .setNeutralButton("実行", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // OK button pressed
                                final MySQLiteOpenHelper helper = new MySQLiteOpenHelper(getActivity());
                                final SQLiteDatabase db = helper.getWritableDatabase();
                                List<String> list = helper.getTableName(db);
                                helper.dropTableAll(db,list);
                                Toast.makeText(getActivity(), "全tableを削除しました。", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });
        /************ DROP Tableボタン end ************/

        /************ DBファイル削除ボタン start ************/
        // ボタンを設定
        final ImageButton deleteDBFileButton = (ImageButton)view.findViewById(R.id.deleteDBFileButton);
        deleteDBFileButton.setImageBitmap(null);
        deleteDBFileButton.setImageDrawable(null);
        deleteDBFileButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_delete_db_switch));
        // リスナーをボタンに登録
        deleteDBFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ダイアログの生成
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                // アラートダイアログのタイトルを設定します
                alertDialogBuilder.setTitle("DBファイル削除確認ダイアログ");
                // アラートダイアログのメッセージを設定します
                alertDialogBuilder.setMessage("このアプリケーションのDBファイルを削除しますがよろしいですか?");
                // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                alertDialogBuilder.setNegativeButton(Constants.CANCEL_CONFIRM_NAME , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                        .setNeutralButton("実行", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // OK button pressed
                                if (!GeneralUtils.deleteSDCardFile(Constants.APP_FOLDER_DIR + Constants.DB_FILE_NAME)) {
                                    Toast.makeText(getActivity(), "DBファイルを削除出来ませんでした。", Toast.LENGTH_SHORT).show();
                                } else {
                                    final Toast toast = Toast.makeText(getActivity(), "DBファイルを削除しました。", Toast.LENGTH_SHORT);
                                    new FrameLayout(getActivity()) {
                                        {
                                            addView(toast.getView()); // toastのviewをframelayoutでくるむ
                                            toast.setView(this); // framelayoutを新しくtoastに設定する
                                        }
                                        @Override
                                        public void onDetachedFromWindow() {
                                            super.onDetachedFromWindow();
                                            // Toastが終了したあとの処理をする
                                            Intent intent = getActivity().getIntent();
                                            getActivity().finish();
                                            startActivity(intent);
                                        }
                                    };
                                    toast.show();
                                }
                            }
                        }).show();
            }
        });
        /************ DBファイル削除ボタン end ************/
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
        /************ 退避ボタン ************/
        ImageButton dbZipButton = (ImageButton)getActivity().findViewById(R.id.dbZipButton);
        dbZipButton.setImageDrawable(null);
        dbZipButton.setImageBitmap(null);
        dbZipButton.setOnClickListener(null);
        /************ Table削除ボタン ************/
        ImageButton dropTableButton = (ImageButton)getActivity().findViewById(R.id.dropTableButton);
        dropTableButton.setImageBitmap(null);
        dropTableButton.setImageDrawable(null);
        dropTableButton.setOnClickListener(null);
        /************ DB全削除ボタン ************/
        ImageButton deleteDBFileButton = (ImageButton)getActivity().findViewById(R.id.deleteDBFileButton);
        deleteDBFileButton.setImageBitmap(null);
        deleteDBFileButton.setImageDrawable(null);
        deleteDBFileButton.setOnClickListener(null);
        /************ 一覧ビュー ************/
        TableLayout tableConfirmList = (TableLayout) getActivity().findViewById(R.id.tableConfirmList);
        tableConfirmList.removeAllViews();

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
