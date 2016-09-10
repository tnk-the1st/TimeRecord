package jp.co.tennti.timerecord.AsyncTaskUtils;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.tennti.timerecord.commonUtils.TimeUtils;
import jp.co.tennti.timerecord.contacts.Constants;

public class OnloadListAsyncTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> implements DialogInterface.OnCancelListener {
    ProgressDialog dialog;
    Context context;
    SQLiteDatabase db;
    List<HashMap<String, String>> listMap = new ArrayList<HashMap<String, String>>();

    public OnloadListAsyncTask(Context context,SQLiteDatabase db) {
        this.context = context;
        this.db = db;
    }

    Cursor cursor = null;

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected List<HashMap<String, String>> doInBackground(String... params) {
        final TimeUtils timeUtil = new TimeUtils();
        /**DB接続 実行処理**/
        try {
            cursor = db.rawQuery("SELECT basic_date,leaving_date,overtime,week,holiday_flag FROM "
                    + timeUtil.getCurrentTableName() +
                    " ORDER BY basic_date LIMIT 31;", new String[]{});
            // WHERE year_month_date=? timeUtil.getCurrentYearMonthHyphen()
        } catch (SQLException e) {
            Log.e("SELECT ERROR", e.toString());
        }

        if (cursor.moveToFirst()) {
            do {
                String holidayValue= "";
                if (cursor.getString(cursor.getColumnIndex("holiday_flag")).equals(Constants.ALL_DAYS_HOLIDAY_FLAG)) {
                    holidayValue = Constants.ALL_DAYS_HOLIDAY_DISP;
                }
                if (cursor.getString(cursor.getColumnIndex("holiday_flag")).equals(Constants.AM_HALF_HOLIDAY_FLAG)) {
                    holidayValue = Constants.AM_HALF_HOLIDAY_DISP;
                }
                if (cursor.getString(cursor.getColumnIndex("holiday_flag")).equals(Constants.PM_HALF_HOLIDAY_FLAG)) {
                    holidayValue = Constants.PM_HALF_HOLIDAY_DISP;
                }
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("basic_date", cursor.getString(cursor.getColumnIndex("basic_date")));
                map.put("leaving_date", cursor.getString(cursor.getColumnIndex("leaving_date")));
                map.put("overtime", cursor.getString(cursor.getColumnIndex("overtime")));
                map.put("week", cursor.getString(cursor.getColumnIndex("week")));
                map.put("holiday_flag", holidayValue);
                /**通常処理**/
                listMap.add(map);
            } while (cursor.moveToNext());

        } else {
            //Log.e("SELECT ERROR", "検索結果 0件");
            //Toast.makeText(context, "検索結果 0件", Toast.LENGTH_SHORT).show();
            List<HashMap<String, String>> arrayTmp = new ArrayList<HashMap<String, String>>();
            return arrayTmp;
        }
        /****** 空行のデータ整形 ******/
        final int MAX_LENGTH_I = 31;
        final int MAX_LENGTH_J = listMap.size();
        List<HashMap<String, String>> arrayTmp = new ArrayList<HashMap<String, String>>();
        String addFlag = "0";
        for (int i = 1; i <= MAX_LENGTH_I; i++) {
            for (int j = 0; j < MAX_LENGTH_J; j++) {
                int str_len = listMap.get(j).get("basic_date").length();
                if (str_len != 0) {
                    if (Integer.parseInt(listMap.get(j).get("basic_date").substring(str_len - 2, str_len)) == i) {
                        arrayTmp.add(i - 1, listMap.get(j));
                        addFlag = "1";
                    }
                }
            }
            if (!addFlag.equals("1")) {
                HashMap<String, String> map = new HashMap<String, String>();
                StringBuffer buffer = new StringBuffer();
                if (i < 10) {
                    buffer.append("0");
                }
                map.put("basic_date", timeUtil.getCurrentYearMonthHyphen() + "-" + buffer.append(i).toString());
                map.put("leaving_date", "");
                map.put("overtime", "--:--:--");
                map.put("week", timeUtil.getTargWeekOmit(timeUtil.getCurrentYearMonthHyphen() + "-" + buffer.append(i).toString()));
                map.put("holiday_flag", "");
                arrayTmp.add(map);
            } else {
                addFlag = "0";
            }
        }
        /****** 空行のデータ整形 ******/
        return arrayTmp;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //Log.d(TAG, "onProgressUpdate - " + values[0]);
        //dialog.setProgress(values[0]);
    }

    @Override
    protected void onCancelled() {
        //Log.d(TAG, "onCancelled"); //dialog.dismiss();
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> result) {
        //Log.d(TAG, "onPostExecute - " + result);
        // dialog.dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        //Log.d(TAG, "Dialog onCancell... calling cancel(true)");
        this.cancel(true);
    }
}