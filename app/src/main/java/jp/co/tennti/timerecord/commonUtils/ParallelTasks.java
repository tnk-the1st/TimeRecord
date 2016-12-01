package jp.co.tennti.timerecord.commonUtils;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.PrintWriter;
import java.util.concurrent.Callable;

/**
 * Created by tennti on 2016/11/30.
 */

// 並列計算する内容
class ParallelTasks implements Callable<String> {
    int    length;
    Cursor cursor;
    PrintWriter pw;
    public ParallelTasks(Cursor cursor , PrintWriter pw , int length){
        this.length = length;
        this.cursor = cursor;
        this.pw     = pw;
    }

    @Override
    public String call() throws Exception{
        do {
            for (int i = 0; i < length; i++) {
                String columnValue;
                try {
                    columnValue = cursor.getString(i);
                    pw.print(columnValue);
                    if( i != length-1 ){
                        pw.print(",");
                    }
                } catch (SQLiteException e) {
                    Log.e("SQLiteException", e.toString());
                }
            }
            pw.println();
        } while (cursor.moveToNext());
        return "task";
    }
}