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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import jp.co.tennti.timerecord.commonUtils.GeneralUtils;
import jp.co.tennti.timerecord.commonUtils.RandGeneratUtils;
import jp.co.tennti.timerecord.commonUtils.TimeUtils;
import jp.co.tennti.timerecord.daoUtils.MySQLiteOpenHelper;


public class MainFragment extends Fragment {

    private Bitmap mainImage               = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_4444);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(getActivity().getApplicationContext());
        final SQLiteDatabase db = helper.getWritableDatabase();

        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        final Resources resource = getResources();
        if(mainImage!=null){
            mainImage.recycle();
        }
        mainImage = BitmapFactory.decodeResource(resource, R.mipmap.main_disp_kongou);
        final ImageView imgView = (ImageView)view.findViewById(R.id.contentImageView);

        imgView.setImageDrawable(null);
        imgView.setImageBitmap(null);
        imgView.setImageBitmap(mainImage);
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        
        /************ 登録ボタン start ************/
        // ボタンを設定
        final ImageButton timeCountButton = (ImageButton)view.findViewById(R.id.timeCountButton);
        timeCountButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_times_day_switch));

        final TimeUtils timeUtil = new TimeUtils();
        /** 初期表示時にボタンを非活性にする判定**/
        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+timeUtil.createTableName()+" WHERE basic_date = ?", new String[]{timeUtil.getCurrentYearMonthDay()});
        try {
            if (cursor.moveToNext()) {
                cursor.moveToFirst();
                if(cursor.getString(0).equals("0")){
                    //timeCountButton.setEnabled(true);
                } else {
                    timeCountButton.setEnabled(false);
                    timeCountButton.setColorFilter(Color.argb(100, 0, 0, 0));
                }
            }
        } catch (SQLException ex) {
            GeneralUtils.createErrorDialog(getActivity(), "SQL SELECT COUNTエラー", "活性判定時のSELECT COUNT処理に失敗しました:" + ex.getLocalizedMessage(),"OK");
            Log.e("SELECT COUNT ERROR", ex.toString());
        } finally {
            cursor.close();
        }

        // リスナーをボタンに登録
        timeCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.beginTransaction();
                try {
                    final RandGeneratUtils randGenerat = new RandGeneratUtils();
                    final TimeUtils timeUtil = new TimeUtils();

                    final SQLiteStatement statement = db.compileStatement("INSERT INTO "+timeUtil.createTableName()+" VALUES (?,?,?,?)");
                    try {
//                        statement.bindString(1, randGenerat.get());
//                        statement.bindString(2, timeUtil.getCurrentYearMonthDay());
                        statement.bindString(1, timeUtil.getCurrentYearMonthHyphen());
                        statement.bindString(2, timeUtil.getCurrentDate());
                        statement.bindString(3, timeUtil.getTimeDiff(timeUtil.conTargetDateFullSlash(timeUtil.getCurrentDate())));
                        statement.bindString(4, timeUtil.getCurrentWeekOmit());
                        statement.executeInsert();
                        timeCountButton.setEnabled(false);
                        timeCountButton.setColorFilter(Color.argb(100, 0, 0, 0));
                        // 第3引数は、表示期間（LENGTH_SHORT、または、LENGTH_LONG）
                        Toast.makeText(getActivity(), "現在時刻を登録しました", Toast.LENGTH_SHORT).show();
                    }  catch (SQLException ex) {
                        GeneralUtils.createErrorDialog(getActivity(),"SQL INSERT エラー","insert処理に失敗しました:" + ex.getLocalizedMessage(),"OK");
                        Log.e("INSERT ERROR", ex.toString());
                    } finally {
                        statement.close();
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        });
        /************ 登録ボタン end ************/
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
                timeCountButton.setEnabled(true);
                timeCountButton.setColorFilter(null);
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
                                    final SQLiteStatement statement = db.compileStatement("DELETE FROM "+timeUtil.createTableName()+" WHERE basic_date=?");
                                    try {
                                        /**年月の判定 start**/
                                        /**年月の判定 end**/
                                        statement.bindString(1, timeUtil.getCurrentYearMonthDay());
                                        statement.executeUpdateDelete();
                                        // 第3引数は、表示期間（LENGTH_SHORT、または、LENGTH_LONG）
                                        Toast.makeText(getActivity(), "対象日付のデータを削除しました", Toast.LENGTH_SHORT).show();
                                    }  catch (SQLException ex) {
                                        GeneralUtils.createErrorDialog(getActivity(),"SQL DELETE エラー","delete処理に失敗しました:" + ex.getLocalizedMessage(),"OK");
                                        Log.e("DELETE ERROR", ex.toString());
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
        mainImage=null;
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
        ImageButton deleteButton = (ImageButton) getActivity().findViewById(R.id.deleteButtonMain);
        deleteButton.setImageBitmap(null);
        deleteButton.setImageDrawable(null);
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
}
