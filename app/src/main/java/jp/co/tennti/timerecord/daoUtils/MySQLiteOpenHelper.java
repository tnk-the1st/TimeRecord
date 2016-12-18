package jp.co.tennti.timerecord.daoUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.tennti.timerecord.commonUtils.GeneralUtils;
import jp.co.tennti.timerecord.commonUtils.TimeUtils;
import jp.co.tennti.timerecord.contacts.Constants;

/**
 * Created by TENNTI on 2016/04/09.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    //private static final String DB_DIRECTORY = Environment.getExternalStorageDirectory() + "/time_record/db/";
    private static final String DB_NAME      = Constants.DB_FULL_NAME;// + "time_record_db.db";
    static final int DB_VERSION              = 1;
    private static final String TABLE_COLUMN_NAME = "( basic_date text not null primary key ," +
                                                    " leaving_date text not null ," +
                                                    " overtime text ," +
                                                    " week text ," +
                                                    " holiday_flag text ," +
                                                    " user_cd text);";
    private static final String GOOGLE_OAUTH2_TABLE = "google_oauth2_data";
    private static final String OAUTH2_TABLE_COLUMN_NAME =
            "( account_name text not null primary key," +
            " auth_token text not null ," +
            " id text ," +
            " name text ," +
            " given_name text ," +
            " family_name text ," +
            " link text ," +
            " picture text ," +
            " gender text ," +
            " locale text ," +
            " create_date text);";

    public MySQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //通常のテーブル
        reloadOnFire(db);
        //認証用のテーブル
        reloadOnFireOAuth2(db);
        //テーブル名作成
        /*StringBuilder builder = new StringBuilder();
        builder.append("time_record_");
        builder.append(TimeUtils.getCurrentYearAndMonth());
        final TimeUtils timeUtil = new TimeUtils();

        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?;",  new String[]{timeUtil.getCurrentTableName().toString()});
        try {
            if (cursor.moveToNext()) {
                cursor.moveToFirst();
                if(cursor.getString(0).equals("0")){
                    //通常のテーブル
                    db.execSQL("CREATE TABLE "+timeUtil.getCurrentTableName().toString()+TABLE_COLUMN_NAME);
                    //認証用のテーブル
                    reloadOnFireAuth2(db);
                }
            }
        } catch (SQLException ex) {
            Log.e("SQLException", ex.toString());
        } finally {
            cursor.close();
        }*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * テーブル月更新再作成用
     * @param db SQLiteDatabase DBアクセッサ
     */
    public void reloadOnFire(SQLiteDatabase db) {
        //テーブル名作成
        StringBuilder builder = new StringBuilder();
        builder.append("time_record_");
        builder.append(TimeUtils.getCurrentYearAndMonth());
        final TimeUtils timeUtil = new TimeUtils();

        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?;"
                , new String[]{timeUtil.getCurrentTableName().toString()});
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
     * Google認証データ再作成用
     * @param db SQLiteDatabase DBアクセッサ
     */
    public void reloadOnFireOAuth2(SQLiteDatabase db) {
        //テーブル名作成
        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?;"
                , new String[]{GOOGLE_OAUTH2_TABLE});
        try {
            if (cursor.moveToNext()) {
                cursor.moveToFirst();
                if(cursor.getString(0).equals("0")){
                    db.execSQL("CREATE TABLE " + GOOGLE_OAUTH2_TABLE + OAUTH2_TABLE_COLUMN_NAME);
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
     * @param  db DBアクセッサ SQLiteDatabase
     * @param  targetTable テーブル名 String
     * @return boolean exitFlag 判定結果
     */
    public boolean isTargetTable(SQLiteDatabase db, String targetTable) {
        boolean exitFlag = false;
        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?;",  new String[]{targetTable});
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
     * テーブル名取得
     * @param  db DBアクセッサ SQLiteDatabase
     * @return List<String>  list 対象のテーブル名
     * */
    public List<String> getTableName(SQLiteDatabase db) {
        List<String> list = new ArrayList<>();
        final Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;",  new String[]{});
        try {
            cursor.moveToFirst();
            do {
                if (!cursor.getString(0).isEmpty() && cursor.getString(0).matches(".*time_record_.*")) {
                    list.add(cursor.getString(0));
                }
            } while (cursor.moveToNext());
        } catch (SQLException e) {
            Log.e("SELECT table ERROR", e.toString());
        } finally {
            cursor.close();
        }
        return list;
    }
    /**
     * DROPテーブル
     * @param  db SQLiteDatabase DBアクセッサ
     * @param  list List<String> 対象のテーブル名
     * */
    public void dropTableAll(SQLiteDatabase db,List<String> list) {
        try {
            //db.setTransactionSuccessful();
            for (String tableName : list) {
                //Log.d("tableName", tableName);
                db.execSQL("drop table " + tableName); //, new String[]{tableName});
            }
        } catch (SQLException e) {
            Log.e("SQLException ", e.toString());
        }
        /*} finally {
            db.endTransaction();
        }*/
    }

    /**
     * テーブルデータ数取得
     * 対象月のレコード数を返す。
     * @param  db SQLiteDatabase DBアクセッサ
     * @param  targetTable String テーブル名
     * @param  targetMonth String
     * @return int exitFlag 判定結果
     */
    public int countTargetMonthData(SQLiteDatabase db,String targetTable,String targetMonth) {
        int tableDataNum = 0;
        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+targetTable+" WHERE basic_date LIKE \""+targetMonth+"%\";",  new String[]{});
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
     * @param  db  SQLiteDatabase DBアクセッサ
     * @param  targMonthTable String テーブル名
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
     * @param  db SQLiteDatabase DBアクセッサ
     * @param  tagetTableName String テーブル名
     * @param  targetDate String 対象日付
     */
    public boolean isCurrentDate(SQLiteDatabase db ,String tagetTableName  , String targetDate) {
        //テーブルを削除した時用に新規テーブル判定と作成を行う
        if(!isTargetTable(db,tagetTableName)){
            createMonthTable(db,tagetTableName);
        }
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

    /**
     * 対象年のテーブルデータを取得する。
     * @param  db SQLiteDatabase DBアクセッサ
     * @param  tagetTableName String テーブル名
     */
    public static Cursor getCurrentList(SQLiteDatabase db ,String tagetTableName ) {
        Cursor cursor = null;
        try {
            db.beginTransaction();
            cursor = db.rawQuery("SELECT * FROM "
                    + tagetTableName +
                    " ORDER BY basic_date ASC;", new String[]{});
            // WHERE year_month_date=? timeUtil.getCurrentYearMonthHyphen()
            //System.out.println(cursor.getCount());
        } catch (SQLException e) {
            Log.e("SQLException SELECT", e.toString());
        } finally {
            db.endTransaction();
        }
        return cursor;
    }

    /**
     * 認証テーブル存在判定
     * 対象月のレコード数を返す。
     * @param  db SQLiteDatabase DBアクセッサ
     * @return boolean exitFlag 判定結果
     */
    public boolean isOAuth2Data(SQLiteDatabase db) {
        boolean exitFlag = false;
        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "
                + GOOGLE_OAUTH2_TABLE +
                ";",  new String[]{});
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
     *  認証データを挿入する。
     * @param  db SQLiteDatabase DBアクセッサ
     * @param  map Map<String, String> 挿入データ
     */
    public static void insertOAuth2Data(SQLiteDatabase db , Map<String, String> map) {
        db.beginTransaction();
        try {
            final SQLiteStatement statement = db.compileStatement(
                    "INSERT INTO "
                    + GOOGLE_OAUTH2_TABLE +
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            try {
                statement.bindString(1,  map.get("account_name"));
                statement.bindString(2,  map.get("auth_token"));
                statement.bindString(3,  map.get("id"));
                statement.bindString(4,  map.get("name"));
                statement.bindString(5,  map.get("given_name"));
                statement.bindString(6,  map.get("family_name"));
                statement.bindString(7,  map.get("link"));
                statement.bindString(8,  map.get("picture"));
                statement.bindString(9,  map.get("gender"));
                statement.bindString(10, map.get("locale"));
                statement.bindString(11, map.get("create_date"));
                statement.executeInsert();
            } finally {
                statement.close();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    /**
     *  認証データを更新する。
     * @param  db SQLiteDatabase DBアクセッサ
     * @param  map Map<String, String> 挿入データ
     */
    public static void updateOAuth2Data(SQLiteDatabase db , Map<String, String> map) {
        db.beginTransaction();
        try {
            final SQLiteStatement statement = db.compileStatement("UPDATE " + GOOGLE_OAUTH2_TABLE + " SET "+
                    //" account_name=?," +
                    " auth_token=?," +
                    " id=?," +
                    " name=?," +
                    " given_name=?," +
                    " family_name=?," +
                    " link=?," +
                    " picture=?," +
                    " gender=?," +
                    " locale=?," +
                    " create_date=?" +
                    " WHERE account_name = ?");
            try {
                statement.bindString(11, map.get("account_name"));
                statement.bindString(1,  map.get("auth_token"));
                statement.bindString(2,  map.get("id"));
                statement.bindString(3,  map.get("name"));
                statement.bindString(4,  map.get("given_name"));
                statement.bindString(5,  map.get("family_name"));
                statement.bindString(6,  map.get("link"));
                statement.bindString(7,  map.get("picture"));
                statement.bindString(8,  map.get("gender"));
                statement.bindString(9,  map.get("locale"));
                statement.bindString(10, map.get("create_date"));
                statement.executeInsert();
            } finally {
                statement.close();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    /**
     * 認証データを取得する。
     * @param  db SQLiteDatabase DBアクセッサ
     * @return  map Map<String, String> 挿入データ
     */
    public static Map<String, String> getOAuth2Data(SQLiteDatabase db) {
        final Cursor cursor = db.rawQuery(
                "SELECT * FROM "
                        + GOOGLE_OAUTH2_TABLE +
                        ";", new String[]{});
        final Map<String, String> map =
                new HashMap<String, String>() {{
                    put("account_name", "");
                    put("auth_token", "");
                    put("id", "");
                    put("name", "");
                    put("given_name", "");
                    put("family_name", "");
                    put("link", "");
                    put("picture", "");
                    put("gender", "");
                    put("locale", "");
                    put("create_date", "");
                }};
        try {
            if (cursor.moveToFirst()) {
                map.put("account_name",GeneralUtils.nullToBlank(cursor.getString(cursor.getColumnIndex("account_name"))));
                map.put("auth_token", GeneralUtils.nullToBlank(cursor.getString(cursor.getColumnIndex("auth_token"))));
                map.put("id",         GeneralUtils.nullToBlank(cursor.getString(cursor.getColumnIndex("id"))));
                map.put("name",       GeneralUtils.nullToBlank(cursor.getString(cursor.getColumnIndex("name"))));
                map.put("given_name", GeneralUtils.nullToBlank(cursor.getString(cursor.getColumnIndex("given_name"))));
                map.put("family_name",GeneralUtils.nullToBlank(cursor.getString(cursor.getColumnIndex("family_name"))));
                map.put("link",       GeneralUtils.nullToBlank(cursor.getString(cursor.getColumnIndex("link"))));
                map.put("picture",    GeneralUtils.nullToBlank(cursor.getString(cursor.getColumnIndex("picture"))));
                map.put("gender",     GeneralUtils.nullToBlank(cursor.getString(cursor.getColumnIndex("gender"))));
                map.put("locale",     GeneralUtils.nullToBlank(cursor.getString(cursor.getColumnIndex("locale"))));
                map.put("create_date",GeneralUtils.nullToBlank(cursor.getString(cursor.getColumnIndex("create_date"))));
            }
        } catch (SQLException e) {
            Log.e("SQLException ERROR", e.toString());
        } finally {
            cursor.close();
        }
        return map;
    }
    /**
     * 認証テーブル
     * @param  db SQLiteDatabase DBアクセッサ
     * @return boolean exitFlag 判定結果
     */
    public void deleteAllOAuth2Data(SQLiteDatabase db) {
            db.delete(GOOGLE_OAUTH2_TABLE, null, null);
    }
}