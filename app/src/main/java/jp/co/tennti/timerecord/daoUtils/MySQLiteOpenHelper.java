package jp.co.tennti.timerecord.daoUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import jp.co.tennti.timerecord.commonUtils.TimeUtils;

/**
 * Created by TENNTI on 2016/04/09.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_DIRECTORY =
            Environment.getExternalStorageDirectory() +
                    "/time_record/db/";
    private static final String DB_NAME =
            DB_DIRECTORY + "time_record_db.db";
    static final int DB_VERSION = 1;
    private static final String TABLE_COLUMN_NAME="( basic_date text not null primary key , leaving_date text not null , overtime text , week text );";

    public MySQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //if (isExistDatabase(mContext) == true){
        //DBがあれば削除
        //mContext.deleteDatabase("NameAgeDB");
        //}
        //テーブル名作成
        StringBuilder builder = new StringBuilder();
        builder.append("time_record_");
        builder.append(TimeUtils.getCurrentYearAndMonth());
        final TimeUtils timeUtil = new TimeUtils();

        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?;",  new String[]{timeUtil.getCurrentTableName().toString()});
        try {
            if (cursor.moveToNext()) {
                cursor.moveToFirst();
                if(cursor.getString(0).equals("0")){
                    db.execSQL("CREATE TABLE "+timeUtil.getCurrentTableName().toString()+TABLE_COLUMN_NAME);
                }
            }
        } catch (SQLException ex) {
            Log.e("ERROR", ex.toString());
        }
        finally {
            cursor.close();
        }
        //db.execSQL("create table person(" + " name text not null," + "age text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * テーブル月更新再作成用
     * @param SQLiteDatabase db DBアクセッサ
     */
    public void reloadOnFire(SQLiteDatabase db) {
        //テーブル名作成
        StringBuilder builder = new StringBuilder();
        builder.append("time_record_");
        builder.append(TimeUtils.getCurrentYearAndMonth());
        final TimeUtils timeUtil = new TimeUtils();

        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?;",  new String[]{timeUtil.getCurrentTableName().toString()});
        try {
            if (cursor.moveToNext()) {
                cursor.moveToFirst();
                if(cursor.getString(0).equals("0")){
                    db.execSQL("CREATE TABLE " + timeUtil.getCurrentTableName().toString() + TABLE_COLUMN_NAME);
                }
            }
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
        } finally {
            cursor.close();
        }
    }

    /**
     * テーブル存在判定
     * テーブルがあればresultは1、なければ0になるのでそれを利用してbooleanで返す。
     * @param  SQLiteDatabase db DBアクセッサ
     * @param  String targMonthTable テーブル名
     * @return boolean exitFlag 判定結果
     */
    public boolean isTarMonthTable(SQLiteDatabase db,String targMonthTable) {
        boolean exitFlag = false;
        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?;",  new String[]{targMonthTable});
        try {
            cursor.moveToFirst();
            if(cursor.getString(0).equals("1")){
                exitFlag = true;
            }
        } catch (SQLException e) {
            Log.e("SELECT COUNT(*) ERROR", e.toString());
        } finally {
            cursor.close();
        }
        return exitFlag;
    }

    /**
     * テーブル存在判定後の作成
     * @param  SQLiteDatabase db DBアクセッサ
     * @param  String targMonthTable テーブル名
     */
    public void createMonthTable(SQLiteDatabase db,String targMonthTable) {
        /*final Cursor cursor =*/
        try {
            db.execSQL("CREATE TABLE " + targMonthTable + TABLE_COLUMN_NAME);
        } catch (SQLException e) {
            Log.e("CREATE ERROR", e.toString());
        }
    }
}