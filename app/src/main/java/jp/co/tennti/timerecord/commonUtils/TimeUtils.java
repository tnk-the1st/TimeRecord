package jp.co.tennti.timerecord.commonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by peko on 2016/03/06.
 */
public class TimeUtils {

    public static final String PROVIS_TIME="18:00:00";
    /**
     * 現在時刻の取得
     *
     */
    public String createTableName() {
        //テーブル名作成
        StringBuilder builder = new StringBuilder();
        builder.append("time_record_");
        builder.append(getCurrentYearAndMonth());
        //String CUR_TIME_TABLE_NAME =  builder.toString();
        return builder.toString();
    }
    /**
     * 現在時刻の取得
     *
     */
    public static void getCurrentTime() {
        List<String> asList = Arrays.asList("日曜日", "月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int day_of_year = cal.get(Calendar.DAY_OF_YEAR);

        System.out.println("現在の日時は" + year + "年" + month + "月" + day + "日" + "(" + asList.get(week) + ")");
        System.out.println(hour + "時" + minute + "分" + second + "秒");
        System.out.println("今日は今年の" + day_of_year + "日目です");
    }
    /**
     * 現在時刻の取得
     *
     * @return 現在時刻 yyyy-MM-dd HH:mm:ss
     */
    public String getCurrentDate() {
        //List<String> asList = Arrays.asList("日曜日", "月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        /*int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int day_of_year = cal.get(Calendar.DAY_OF_YEAR);*/

        StringBuilder builder = new StringBuilder();
        String yearStr  = String.valueOf(year);
        String monthStr = String.valueOf(month);
        String dayStr   = String.valueOf(day);
        String hourStr  = String.valueOf(hour);
        String minuteStr= String.valueOf(minute);
        String secondStr= String.valueOf(second);

        builder.append(yearStr);
        builder.append("-");
        /*if (month < 10) {
            builder.append("0");
        }*/
        is10over(month, builder);
        builder.append(monthStr);
        builder.append("-");
        /*if (day < 10) {
            builder.append("0");
        }*/
        is10over(day , builder);
        builder.append(dayStr);
        builder.append(" ");
        /*if (hour < 10) {
            builder.append("0");
        }*/
        is10over(hour , builder);
        builder.append(hourStr);
        builder.append(":");
        /*if (minute < 10) {
            builder.append("0");
        }*/
        is10over(minute , builder);
        builder.append(minuteStr);
        builder.append(":");
        /*if (second < 10) {
            builder.append("0");
        }*/
        is10over(second , builder);
        builder.append(secondStr);

        return builder.toString();
    }
    public static void is10over(int num , StringBuilder builder) {
        if (num < 10) {
            builder.append("0");
        }
    }
    /**
     * 現在曜日の取得
     *
     * @return 現在の曜日：○曜日
     */
    public String getCurrentWeek() {
        List<String> asList = Arrays.asList("日曜日", "月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日");
        Calendar cal = Calendar.getInstance();
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;

        return asList.get(week);
    }
    /**
     * 現在曜日の取得
     *
     * @return 現在の曜日略式：(曜日)
     */
    public String getCurrentWeekOmit() {
        List<String> asList = Arrays.asList("日", "月", "火", "水", "木", "金", "土");
        Calendar cal = Calendar.getInstance();
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;

        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append(asList.get(week));
        builder.append(")");

        return builder.toString();
    }
    /**
     * 指定日の曜日を取得
     * @param  yyyyMMdd 指定対象日： yyyyMMdd
     * @return 指定日の曜日略式：(曜日)
     */
    public String getTargetWeekOmit(String yyyyMMdd) {
        List<String> asList = Arrays.asList("日", "月", "火", "水", "木", "金", "土");
        Calendar cal = Calendar.getInstance();
        int year = Integer.parseInt(yyyyMMdd.substring(0, 4));
        int month = Integer.parseInt(yyyyMMdd.substring(5, 7))-1;
        int day = Integer.parseInt(yyyyMMdd.substring(8, 10));
        cal.set(year, month, day);
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;

        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append(asList.get(week));
        builder.append(")");

        return builder.toString();
    }

    /**
     * 現在年月の取得
     *
     * @return 現在時刻yyyymm
     */
    public static String getCurrentYearAndMonth() {

        String JOIN_YEAR_MONTH;
        Calendar cal = Calendar.getInstance();
        int year     = cal.get(Calendar.YEAR);
        int month    = cal.get(Calendar.MONTH) + 1;
        StringBuilder builder = new StringBuilder();
        String yearStr  = String.valueOf(year);
        String monthStr = String.valueOf(month);

        builder.append(yearStr);
        /*if (month < 10) {
            builder.append("0");
        }*/
        is10over(month, builder);
        builder.append(monthStr);
        JOIN_YEAR_MONTH = builder.toString();
        return JOIN_YEAR_MONTH;
    }

    /**
     * 現在年月の取得
     *
     * @return 現在時刻yyyy-MM
     */
    public String getCurrentYearMonthHyphen() {

        String JOIN_YEAR_MONTH;
        Calendar cal = Calendar.getInstance();
        int year     = cal.get(Calendar.YEAR);
        int month    = cal.get(Calendar.MONTH) + 1;
        StringBuilder builder = new StringBuilder();
        String yearStr  = String.valueOf(year);
        String monthStr = String.valueOf(month);

        builder.append(yearStr);
        builder.append("-");
        /*if (month < 10) {
            builder.append("0");
        }*/
        is10over(month, builder);
        builder.append(monthStr);
        JOIN_YEAR_MONTH = builder.toString();
        return JOIN_YEAR_MONTH;
    }

    /**
     * 現在年月の取得
     *
     * @return 現在時刻yyyy年MM月
     */
    public String getCurrentYearMonthJaCal() {

        String JOIN_YEAR_MONTH;
        Calendar cal = Calendar.getInstance();
        int year     = cal.get(Calendar.YEAR);
        int month    = cal.get(Calendar.MONTH) + 1;
        StringBuilder builder = new StringBuilder();
        String yearStr  = String.valueOf(year);
        String monthStr = String.valueOf(month);

        builder.append(yearStr);
        builder.append("年");
        /*if (month < 10) {
            builder.append("0");
        }*/
        is10over(month , builder);
        builder.append(monthStr);
        builder.append("月");
        JOIN_YEAR_MONTH = builder.toString();
        return JOIN_YEAR_MONTH;
    }
    /**
     * 現在年月の取得
     *
     * @return 現在時刻yyyy年MM月
     */
    public String getCurrentYearMonthDayJaCal() {

        String JOIN_YEAR_MONTH;
        Calendar cal = Calendar.getInstance();
        int year     = cal.get(Calendar.YEAR);
        int month    = cal.get(Calendar.MONTH) + 1;
        int day      = cal.get(Calendar.DATE);
        StringBuilder builder = new StringBuilder();
        String yearStr  = String.valueOf(year);
        String monthStr = String.valueOf(month);
        String dayStr   = String.valueOf(day);

        builder.append(yearStr);
        builder.append("年");
        /*if (month < 10) {
            builder.append("0");
        }*/
        is10over(month , builder);
        builder.append(monthStr);
        builder.append("月");
        is10over(day, builder);
        builder.append(dayStr);
        builder.append("日");
        JOIN_YEAR_MONTH = builder.toString();
        return JOIN_YEAR_MONTH;
    }
    /**
     * 現在年月の取得
     *
     * @return 現在時刻yyyy-MM-dd
     */
    public String getCurrentYearMonthDay(){

        String JOIN_YEAR_MONTH;
        Calendar cal = Calendar.getInstance();
        int year     = cal.get(Calendar.YEAR);
        int month    = cal.get(Calendar.MONTH) + 1;
        int day      = cal.get(Calendar.DATE);
        StringBuilder builder = new StringBuilder();
        String yearStr  = String.valueOf(year);
        String monthStr = String.valueOf(month);
        String dayStr   = String.valueOf(day);

        builder.append(yearStr);
        builder.append("-");
        /*if (month < 10) {
            builder.append("0");
        }*/
        is10over(month, builder);
        builder.append(monthStr);
        builder.append("-");
        is10over(day, builder);
        builder.append(dayStr);
        JOIN_YEAR_MONTH = builder.toString();
        return JOIN_YEAR_MONTH;
    }

    /**
     * 現在年月の取得
     *
     * @return 現在時刻yyyy年MM月
     */
    public String getCurrentHourMinuteJaCal() {

        String JOIN_HOUR_MINUTE;
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        StringBuilder builder = new StringBuilder();
        String hourStr  = String.valueOf(hour);
        String minutehStr = String.valueOf(minute);

        is10over(hour, builder);
        builder.append(hourStr);
        builder.append("時");

        is10over(minute, builder);
        builder.append(minutehStr);
        builder.append("分");
        JOIN_HOUR_MINUTE = builder.toString();
        return JOIN_HOUR_MINUTE;
    }

    /**
     * 現在年月の取得
     * @param  yyyyMMdd 指定日付:yyyy_MM_dd
     * @return 指定時刻yyyy-MM
     */
    public String getTargetYYYYMMHyphen( String yyyyMMdd) {

        String JOIN_HOUR_MINUTE;
        String yearStr = yyyyMMdd.substring(0, 4);
        String monthStr = yyyyMMdd.substring(5, 7);
        String dayStr = yyyyMMdd.substring(8, 10);
        StringBuilder builder = new StringBuilder();
        builder.append(yearStr);
        builder.append("-");
        builder.append(monthStr);
        JOIN_HOUR_MINUTE = builder.toString();
        return JOIN_HOUR_MINUTE;
    }
    /**
     * 現在年月日の取得
     * @param  指定日付:yyyy_MM_dd
     * @return 指定時刻yyyy-MM-dd
     */
    public String getTargetYYYYMMDDHyphen( String yyyyMMdd) {

        String JOIN_HOUR_MINUTE;
        String yearStr = yyyyMMdd.substring(0, 4);
        String monthStr = yyyyMMdd.substring(5, 7);
        String dayStr = yyyyMMdd.substring(8, 10);
        StringBuilder builder = new StringBuilder();
        builder.append(yearStr);
        builder.append("-");
        builder.append(monthStr);
        builder.append("-");
        builder.append(dayStr);
        JOIN_HOUR_MINUTE = builder.toString();
        return JOIN_HOUR_MINUTE;
    }

    /**
     * 現在時刻全容の取得
     * @param  yyyyMMdd 指定日付:yyyy_MM_dd
     * @param  hms 指定時刻:hh_mm_ss
     * @return 指定時刻yyyy-MM-dd HH:mm:ss
     */
    public String getTargetDateFullHyphen( String yyyyMMdd,String hms) {

        String JOIN_HOUR_MINUTE;
        String yearStr = yyyyMMdd.substring(0, 4);
        String monthStr = yyyyMMdd.substring(5, 7);
        String dayStr = yyyyMMdd.substring(8, 10);
        StringBuilder builder = new StringBuilder();
        builder.append(yearStr);
        builder.append("-");
        builder.append(monthStr);
        builder.append("-");
        builder.append(dayStr);
        String hourStr = hms.substring(0, 2);
        String minuteStr = hms.substring(3, 5);
        builder.append(" ");
        builder.append(hourStr);
        builder.append(":");
        builder.append(minuteStr);
        builder.append(":00");
        JOIN_HOUR_MINUTE = builder.toString();
        return JOIN_HOUR_MINUTE;
    }

    /**
     * 現在時刻[/]に変換の取得
     * @param  yyyyMMddhms 指定日付:yyyy_MM_dd
     * @return 指定時刻yyyy/MM/dd HH:mm:ss
     */
    public String conTargetDateFullSlash( String yyyyMMddhms) {

        String yearStr = yyyyMMddhms.substring(0, 4);
        String monthStr = yyyyMMddhms.substring(5, 7);
        String dayStr = yyyyMMddhms.substring(8, 10);
        StringBuilder builder = new StringBuilder();
        builder.append(yearStr);
        builder.append("/");
        builder.append(monthStr);
        builder.append("/");
        builder.append(dayStr);
        builder.append(" ");
        String hms = yyyyMMddhms.substring(11, 19);
        builder.append(hms);

        return builder.toString();
    }

    /**
     * 現在年月日の取得
     * @param  yyyyMM 指定日付yyyy_MM
     * @return 指定時刻yyyy年MM月
     */
    public String getTargetYYYYMM( String yyyyMM) {

        String yearStr = yyyyMM.substring(0, 4);
        String monthStr = yyyyMM.substring(5, 7);
        StringBuilder builder = new StringBuilder();
        builder.append(yearStr);
        builder.append(monthStr);
        return  builder.toString();
    }
        /**
         * 現在年月日の取得
         * @param  yyyyMMdd 指定日付 yyyy_MM_dd
         * @return 指定時刻yyyy年MM月
         */
    public String getTargetYYYYMMJaCal( String yyyyMMdd) {

        String yearStr = yyyyMMdd.substring(0, 4);
        String monthStr = yyyyMMdd.substring(5, 7);
        String dayStr = yyyyMMdd.substring(8, 10);
        StringBuilder builder = new StringBuilder();
        builder.append(yearStr);
        builder.append("年");
        builder.append(monthStr);
        builder.append("月");
        return  builder.toString();
    }
    /**
     * 現在年月日の取得
     * @param  yyyyMMdd 指定日付:yyyy_MM_dd
     * @return 指定時刻yyyy年MM月dd日
     */
    public String getTargetYYYYMMDDJaCal( String yyyyMMdd) {

        String yearStr = yyyyMMdd.substring(0, 4);
        String monthStr = yyyyMMdd.substring(5, 7);
        String dayStr = yyyyMMdd.substring(8, 10);
        StringBuilder builder = new StringBuilder();
        builder.append(yearStr);
        builder.append("年");
        builder.append(monthStr);
        builder.append("月");
        builder.append(dayStr);
        builder.append("日");
        return  builder.toString();
    }
    /**
     * 現在年月日の結合
     * 月に関しては渡す前にタス１する
     * @param year 対象年:yyyy
     *            @param month 対象月:MM
     *                       @param day 対象日:dd
     * @return 指定時刻yyyy年MM月dd日
     */
    public String getJoinYYYYMMDDJaCal( int year,int month,int day) {

        String yearStr =  String.valueOf(year);
        String monthStr = String.valueOf(month);
        String dayStr =String.valueOf(day);

        StringBuilder builder = new StringBuilder();
        builder.append(yearStr);
        builder.append("年");
        is10over(month, builder);
        builder.append(monthStr);
        builder.append("月");
        is10over(day, builder);
        builder.append(dayStr);
        builder.append("日");
        return  builder.toString();
    }

    /**
     * 現在時刻の取得
     * @param  hour 指定日付（時）:HH
     * @param  minute 指定日付（分）:mm
     * @return 指定時刻HH時mm分
     */
    public String getTargetHourMinuteJaCal(int hour,int minute) {

        StringBuilder builder = new StringBuilder();
        String hourStr  = String.valueOf(hour);
        String minuteStr = String.valueOf(minute);

        is10over(hour, builder);
        builder.append(hourStr);
        builder.append("時");
        is10over(minute, builder);
        builder.append(minuteStr);
        builder.append("分");

        return  builder.toString();
    }



    /**
     * 時刻の差分
     * @param  endDateStr 退社時間 yy/MM/dd HH:mm:ss
     * @return 差分時刻 HH:mm
     * @throws ParseException 型変換時に起こりうる例外
     */
    public String getTimeDiff(String endDateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat ("yy/MM/dd HH:mm:ss");
        String provisDateStr = endDateStr.substring(0,10) +" "+ PROVIS_TIME;
        Date provisDate = null; // 規定時刻
        Date endDate = null; // 終了時刻
        try {
            provisDate = formatter.parse(provisDateStr);
            endDate = formatter.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int diff = endDate.compareTo(provisDate);
        String timeDiffStr;
        if (diff == 0) {
            //日付1と日付2は同じです
            timeDiffStr=getTimeCalculation(provisDate,endDate);
        } else if (diff > 0) {
            //日付1は日付2より未来の日付です
            timeDiffStr=getTimeCalculation(provisDate,endDate);
        } else {
            //日付1は日付2より過去の日付です
            timeDiffStr="00:00:00";
        }
        return timeDiffStr;
    }
    /**
     * 時刻の差分計算クラス
     * @param  provisDate 規定時刻 yy/MM/dd HH:mm:ss
     * @param  endDate 退社時刻 yy/MM/dd HH:mm:ss
     * @return 差分時刻 HH:mm
     */
    public String getTimeCalculation(Date provisDate,Date endDate){
        long diffTime =  endDate.getTime()-provisDate.getTime();
        // 結果出力用フォーマット
        SimpleDateFormat timeFormatter = new SimpleDateFormat ("HH:mm:ss");
        timeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String diffTimeStr = timeFormatter.format(new Date(diffTime));
        return diffTimeStr;
    }
}