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
    private static final String DB_DIRECTORY = Environment.getExternalStorageDirectory() + "/time_record/db/";
    private static final String DB_NAME = DB_DIRECTORY + "time_record_db.db";
    static final int DB_VERSION = 1;
    private static final String TABLE_COLUMN_NAME = "( basic_date text not null primary key ," +
            " leaving_date text not null ," +
            " overtime text ," +
            " week text ," +
            " holiday_flag text ," +
            " user_cd text);";

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
            Log.e("SQLException", ex.toString());
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
            Log.e("SQLException ERROR", e.toString());
        } finally {
            cursor.close();
        }
    }

    /**
     * テーブル存在判定
     * テーブルがあればresultは1、なければ0になるのでそれを利用してbooleanで返す。
     * @param  SQLiteDatabase db DBアクセッサ
     * @param  String targetMonthTable テーブル名
     * @return boolean exitFlag 判定結果
     */
    public boolean isTarMonthTable(SQLiteDatabase db,String targetMonthTable) {
        boolean exitFlag = false;
        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?;",  new String[]{targetMonthTable});
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
     * テーブルデータ数取得
     * 対象月のレコード数を返す。
     * @param  SQLiteDatabase db DBアクセッサ
     * @param  String targetMonthTable テーブル名
     * @return int exitFlag 判定結果
     */
    public int countTargetMonthData(SQLiteDatabase db,String targetMonthTable) {
        int tableDataNum = 0;
        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+targetMonthTable+" ORDER BY basic_date LIMIT 31;",  new String[]{});
        try {
            cursor.moveToFirst();
            if(cursor.getString(0).equals("1")){
                tableDataNum =Integer.parseInt(cursor.getString(0));
            }
        } catch (SQLException e) {
            Log.e("SQLException COUNT", e.toString());
        } finally {
            cursor.close();
        }
        return tableDataNum;
    }

    /**
     * テーブル存在判定後の作成
     * @param  SQLiteDatabase db DBアクセッサ
     * @param  String targMonthTable テーブル名
     */
    public void createMonthTable(SQLiteDatabase db,String targMonthTable) {
        try {
            db.execSQL("CREATE TABLE " + targMonthTable + TABLE_COLUMN_NAME);
        } catch (SQLException e) {
            Log.e("SQLException CREATE", e.toString());
        }
    }
    /**
     * 対象日のデータが存在するか判定する。あった場合TRUE
     * @param  SQLiteDatabase db DBアクセッサ
     * @param  String tagetTableName テーブル名
     * @param  String targetDate 対象日付
     */
    public boolean isCurrentDate(SQLiteDatabase db ,String tagetTableName  , String targetDate) {
        boolean exitFlag = false;
        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+ tagetTableName +" WHERE basic_date = ?", new String[]{targetDate});
        try {
            cursor.moveToFirst();
            if(cursor.getString(0).equals("1")){
                exitFlag = true;
            }
        } catch (SQLException e) {
            Log.e("SQLException COUNT", e.toString());
        } finally {
            cursor.close();
        }
        return exitFlag;
    }
}