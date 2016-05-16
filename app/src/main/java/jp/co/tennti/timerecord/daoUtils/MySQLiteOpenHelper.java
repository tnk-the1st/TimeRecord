package jp.co.tennti.timerecord.daoUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import jp.co.tennti.timerecord.commonUtils.TimeUtils;

/**
 * Created by TENNTI on 2016/04/09.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "time_record_db";
    static final int DB_VERSION = 1;

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
        TimeUtils timeUtil = new TimeUtils();
        String CUR_TIME_TABLE_NAME =  timeUtil.createTableName();//builder.toString();


        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?;",  new String[]{CUR_TIME_TABLE_NAME});
        try {
            if (cursor.moveToNext()) {
                cursor.moveToFirst();
                String result = cursor.getString(0);
                if(result.equals("0")){
                    db.execSQL("create table "+CUR_TIME_TABLE_NAME+" ( key_cd text , basic_date text primary key , year_month_date text,"+
                            " leaving_date text not null , week text );");//,primary key (key_cd, basic_date)
                }
            }
        } catch (SQLException ex) {
            Log.e("ERROR", ex.toString());
        }
        finally {
            timeUtil = null;
            cursor.close();
        }

        //db.execSQL("create table person(" + " name text not null," + "age text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //テーブル月更新再作成用
    public void reloadOnFire(SQLiteDatabase db) {
        //テーブル名作成
        StringBuilder builder = new StringBuilder();
        builder.append("time_record_");
        builder.append(TimeUtils.getCurrentYearAndMonth());
        TimeUtils timeUtil = new TimeUtils();
        String CUR_TIME_TABLE_NAME =  timeUtil.createTableName();//builder.toString();


        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?;",  new String[]{CUR_TIME_TABLE_NAME});
        try {
            if (cursor.moveToNext()) {
                cursor.moveToFirst();
                String result = cursor.getString(0);
                if(result.equals("0")){
                    db.execSQL("create table "+CUR_TIME_TABLE_NAME+" ( key_cd text , basic_date text primary key , year_month_date text,"+
                            " leaving_date text not null , week text );");//,primary key (key_cd, basic_date)
                }
            }
        } catch (SQLException ex) {
            Log.e("ERROR", ex.toString());
        }
        finally {
            timeUtil = null;
            cursor.close();
        }
    }
}
