package jp.co.tennti.timerecord.commonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by peko on 2016/03/06.
 */
public class TimeUtils {

    public static final String PROVIS_TIME="18:00:00";
    /**
     * システム日付でのテーブル作成
     */
    public String createTableName() {
        //テーブル名作成
        StringBuffer builder = new StringBuffer();
        builder.append("time_record_");
        builder.append(getCurrentYearAndMonth());
        //String CUR_TIME_TABLE_NAME =  builder.toString();
        return builder.toString();
    }
    /**
     * 現在時刻の取得
     *
     */
    /*public static void getCurrentTime() {
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
    }*/
    /**
     * 現在時刻 FULL の取得
     * @return 現在時刻 yyyy-MM-dd HH:mm:ss
     */
    public String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        StringBuffer buffer = new StringBuffer();
        String yearStr  = String.valueOf(year);
        String monthStr = String.valueOf(month);
        String dayStr   = String.valueOf(day);
        String hourStr  = String.valueOf(hour);
        String minuteStr= String.valueOf(minute);
        String secondStr= String.valueOf(second);

        buffer.append(yearStr);
        buffer.append("-");
        addZeroBuf(month, buffer);
        buffer.append(monthStr);
        buffer.append("-");
        addZeroBuf(day, buffer);
        buffer.append(dayStr);
        buffer.append(" ");
        addZeroBuf(hour, buffer);
        buffer.append(hourStr);
        buffer.append(":");
        addZeroBuf(minute, buffer);
        buffer.append(minuteStr);
        buffer.append(":");
        addZeroBuf(second, buffer);
        buffer.append(secondStr);

        return buffer.toString();
    }
    /**
     * 現在時間の取得
     * @return 現在時間 HH:mm:ss
     */
    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        StringBuffer buffer = new StringBuffer();

        addZeroBuf(hour, buffer);
        buffer.append(String.valueOf(hour));
        buffer.append(":");
        addZeroBuf(minute, buffer);
        buffer.append(String.valueOf(minute));
        buffer.append(":");
        addZeroBuf(second, buffer);
        buffer.append(String.valueOf(second));

        return buffer.toString();
    }
    /**
     * 追加関数
     * @param num 判定数値
     * @param buffer 結合文字列
     */
    protected static void addZeroBuf(int num , StringBuffer buffer) {
        if(is10low(num)){
            buffer.append("0");
        }
    }
    /**
     * @param num 判定数値
     * @return 引数が大きければtrue
     */
    protected static boolean is10over(int num) {
        if (num < 10) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * @param num 判定数値
     * @return 引数が小さければtrue
     */
    protected static boolean is10low(int num) {
        if (num < 10) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 現在曜日の取得
     *
     * @return 現在の曜日：○曜日
     */
    public String getCurrentWeek() {
        final List<String> asList = Arrays.asList("日曜日", "月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日");
        final Calendar cal = Calendar.getInstance();
        final int week = cal.get(Calendar.DAY_OF_WEEK) - 1;

        return asList.get(week);
    }
    /**
     * 現在曜日の取得
     *
     * @return 現在の曜日略式：(曜日)
     */
    public String getCurrentWeekOmit() {
        final List<String> asList = Arrays.asList("日", "月", "火", "水", "木", "金", "土");
        final Calendar cal = Calendar.getInstance();
        final int week = cal.get(Calendar.DAY_OF_WEEK) - 1;

        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        buffer.append(asList.get(week));
        buffer.append(")");

        return buffer.toString();
    }
    /**
     * 現在曜日の取得
     *y@param  String 指定対象日 yyy-MM-dd
     * @return 現在の曜日略式：(曜日)
     */
    public String getTargWeekOmit(String targDate) {
        final List<String> asList = Arrays.asList("日", "月", "火", "水", "木", "金", "土");
        final Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, Integer.parseInt(targDate.substring(0, 4)));
        cal.set(Calendar.MONTH, Integer.parseInt(targDate.substring(5,7)) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(targDate.substring(8,10)));
        final int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        buffer.append(asList.get(week));
        buffer.append(")");

        return buffer.toString();
    }
    /**
     * 指定日の曜日を取得
     * @param  String 指定対象日： yyyy-MM-dd
     * @return 指定日の曜日略式：(曜日)
     */
    public String getTargetWeekOmit(String tarDate) {
        final List<String> asList = Arrays.asList("日", "月", "火", "水", "木", "金", "土");
        final Calendar cal = Calendar.getInstance();
        final int year     = Integer.parseInt(tarDate.substring(0, 4));
        final int month    = Integer.parseInt(tarDate.substring(5, 7)) -1;
        final int day      = Integer.parseInt(tarDate.substring(8, 10));
        cal.set(year, month, day);
        final int week = cal.get(Calendar.DAY_OF_WEEK) - 1;

        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        buffer.append(asList.get(week));
        buffer.append(")");

        return buffer.toString();
    }

    /**
     * 現在年月の取得
     *
     * @return 現在時刻yyyymm
     */
    public static String getCurrentYearAndMonth() {

        //String JOIN_YEAR_MONTH;
        final Calendar cal = Calendar.getInstance();
        final int year     = cal.get(Calendar.YEAR);
        final int month    = cal.get(Calendar.MONTH) + 1;
        StringBuffer builder = new StringBuffer();
        final String yearStr  = String.valueOf(year);
        final String monthStr = String.valueOf(month);

        builder.append(yearStr);
        addZeroBuf(month, builder);
        builder.append(monthStr);
        //JOIN_YEAR_MONTH = builder.toString();
        return builder.toString();
    }

    /**
     * 現在年月の取得
     *
     * @return 現在時刻yyyy-MM
     */
    public String getCurrentYearMonthHyphen() {

        //String JOIN_YEAR_MONTH;
        final Calendar cal = Calendar.getInstance();
        final int year     = cal.get(Calendar.YEAR);
        final int month    = cal.get(Calendar.MONTH) + 1;
        StringBuffer buffer = new StringBuffer();
        final String yearStr  = String.valueOf(year);
        final String monthStr = String.valueOf(month);

        buffer.append(yearStr);
        buffer.append("-");
        addZeroBuf(month, buffer);
        buffer.append(monthStr);
        //JOIN_YEAR_MONTH = buffer.toString();
        return buffer.toString();
    }

    /**
     * 現在年月の取得
     *
     * @return 現在時刻yyyy年MM月
     */
    public String getCurrentYearMonthJaCal() {

        //String JOIN_YEAR_MONTH;
        final Calendar cal = Calendar.getInstance();
        final int year     = cal.get(Calendar.YEAR);
        final int month    = cal.get(Calendar.MONTH) + 1;
        StringBuffer buffer = new StringBuffer();
        final String yearStr  = String.valueOf(year);
        final String monthStr = String.valueOf(month);

        buffer.append(yearStr);
        buffer.append("年");
        addZeroBuf(month, buffer);
        buffer.append(monthStr);
        buffer.append("月");
        //JOIN_YEAR_MONTH = buffer.toString();
        return  buffer.toString();
    }
    /**
     * 現在年月の取得
     *
     * @return 現在時刻yyyy年MM月
     */
    public String getCurrentYearMonthDayJaCal() {

        //String JOIN_YEAR_MONTH;
        final Calendar cal = Calendar.getInstance();
        final int year     = cal.get(Calendar.YEAR);
        final int month    = cal.get(Calendar.MONTH) + 1;
        final int day      = cal.get(Calendar.DATE);
        StringBuffer buffer = new StringBuffer();
        final String yearStr  = String.valueOf(year);
        final String monthStr = String.valueOf(month);
        final String dayStr   = String.valueOf(day);

        buffer.append(yearStr);
        buffer.append("年");
        addZeroBuf(month, buffer);
        buffer.append(monthStr);
        buffer.append("月");
        addZeroBuf(day, buffer);
        buffer.append(dayStr);
        buffer.append("日");
        //JOIN_YEAR_MONTH = buffer.toString();
        return buffer.toString();
    }
    /**
     * 現在年月の取得
     *
     * @return 現在時刻yyyy-MM-dd
     */
    public String getCurrentYearMonthDay(){

        //String JOIN_YEAR_MONTH;
        final Calendar cal = Calendar.getInstance();
        final int year     = cal.get(Calendar.YEAR);
        final int month    = cal.get(Calendar.MONTH) + 1;
        final int day      = cal.get(Calendar.DATE);
        StringBuffer buffer = new StringBuffer();
        final String yearStr  = String.valueOf(year);
        final String monthStr = String.valueOf(month);
        final String dayStr   = String.valueOf(day);

        buffer.append(yearStr);
        buffer.append("-");
        addZeroBuf(month, buffer);
        buffer.append(monthStr);
        buffer.append("-");
        addZeroBuf(day, buffer);
        buffer.append(dayStr);
        //JOIN_YEAR_MONTH = buffer.toString();
        return buffer.toString();
    }

    /**
     * 現在年月の取得
     *
     * @return 現在時刻yyyy年MM月
     */
    public String getCurrentHourMinuteJaCal() {

        //String JOIN_HOUR_MINUTE;
        final Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        StringBuffer buffer = new StringBuffer();
        final String hourStr  = String.valueOf(hour);
        final String minutehStr = String.valueOf(minute);

        addZeroBuf(hour, buffer);
        buffer.append(hourStr);
        buffer.append("時");

        addZeroBuf(minute, buffer);
        buffer.append(minutehStr);
        buffer.append("分");
        //JOIN_HOUR_MINUTE = buffer.toString();
        return buffer.toString();
    }

    /**
     * 現在年月の取得
     * @param  yyyyMMdd 指定日付:yyyy_MM_dd
     * @return 指定時刻yyyy-MM
     */
    public String getTargetYYYYMMHyphen( String yyyyMMdd) {

        //String JOIN_HOUR_MINUTE;
        final String yearStr = yyyyMMdd.substring(0, 4);
        final String monthStr = yyyyMMdd.substring(5, 7);
        StringBuffer buffer = new StringBuffer();
        buffer.append(yearStr);
        buffer.append("-");
        buffer.append(monthStr);
        //JOIN_HOUR_MINUTE = buffer.toString();
        return buffer.toString();
    }
    /**
     * 現在年月日の変換
     * @param  指定日付:yyyy_MM_dd
     * @return 指定時刻yyyy-MM-dd
     */
    public String conTargetYYYYMMDDHyphen( String yyyyMMdd) {

        final String yearStr = yyyyMMdd.substring(0, 4);
        final String monthStr = yyyyMMdd.substring(5, 7);
        final String dayStr = yyyyMMdd.substring(8, 10);
        StringBuffer buffer = new StringBuffer();
        buffer.append(yearStr);
        buffer.append("-");
        buffer.append(monthStr);
        buffer.append("-");
        buffer.append(dayStr);
        return buffer.toString();
    }

    /**
     * 現在時刻全容の変換
     * @param  hm 指定時間:hh_mm
     * @return 指定時間HH:mm:ss
     */
    public String conTargetTime( String hm) {
        String regex = "[時分]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(hm);
        String result = m.replaceAll(":");
        final StringBuffer buffer = new StringBuffer();
        buffer.append(result);
        buffer.append(":");
        buffer.append(":00");
        return buffer.toString();
    }
    /**
     * 現在時刻全容の変換
     * @param  yyyyMMdd 指定日付:yyyy_MM_dd
     * @param  hms 指定時刻:hh_mm
     * @return 指定時刻yyyy-MM-dd HH:mm:ss
     */
    public String conTargetDateFullHyphen(String yyyyMMdd, String hm) {
        //String JOIN_HOUR_MINUTE;
        final  String yearStr = yyyyMMdd.substring(0, 4);
        final String monthStr = yyyyMMdd.substring(5, 7);
        final String dayStr = yyyyMMdd.substring(8, 10);
        final StringBuffer buffer = new StringBuffer();
        buffer.append(yearStr);
        buffer.append("-");
        buffer.append(monthStr);
        buffer.append("-");
        buffer.append(dayStr);
        final String hourStr = hm.substring(0, 2);
        final String minuteStr = hm.substring(3, 5);
        buffer.append(" ");
        buffer.append(hourStr);
        buffer.append(":");
        buffer.append(minuteStr);
        buffer.append(":00");
        //JOIN_HOUR_MINUTE = buffer.toString();
        return buffer.toString();
    }

    /**
     * 現在時刻[/]に変換の取得
     * @param  yyyyMMddhms 指定日付:yyyy_MM_dd
     * @return 指定時刻yyyy/MM/dd HH:mm:ss
     */
    public String conTargetDateFullSlash( String yyyyMMddhms) {

        final String yearStr = yyyyMMddhms.substring(0, 4);
        final String monthStr = yyyyMMddhms.substring(5, 7);
        final String dayStr = yyyyMMddhms.substring(8, 10);
        StringBuffer buffer = new StringBuffer();
        buffer.append(yearStr);
        buffer.append("/");
        buffer.append(monthStr);
        buffer.append("/");
        buffer.append(dayStr);
        buffer.append(" ");
        final String hms = yyyyMMddhms.substring(11, 19);
        buffer.append(hms);

        return buffer.toString();
    }

    /**
     * 現在年月日の取得
     * @param  yyyyMM 指定日付yyyy_MM
     * @return 指定時刻yyyy年MM月
     */
    public String getTargetYYYYMM( String yyyyMM) {

        final String yearStr  = yyyyMM.substring(0, 4);
        final String monthStr = yyyyMM.substring(5, 7);
        StringBuffer buffer = new StringBuffer();
        buffer.append(yearStr);
        buffer.append(monthStr);
        return  buffer.toString();
    }
    /**
     * 現在年月日の取得
     * @param  yyyyMMdd 指定日付 yyyy_MM_dd
     * @return 指定時刻yyyy年MM月
     */
    public String getTargetYYYYMMJaCal( String yyyyMMdd) {

        final String yearStr = yyyyMMdd.substring(0, 4);
        final String monthStr = yyyyMMdd.substring(5, 7);
        final String dayStr = yyyyMMdd.substring(8, 10);
        StringBuffer buffer = new StringBuffer();
        buffer.append(yearStr);
        buffer.append("年");
        buffer.append(monthStr);
        buffer.append("月");
        return  buffer.toString();
    }
    /**
     * 年+月の結合
     * @param String year 指定年 yyyy
     * @param int month 指定月 MM
     * @return 結合時刻yyyy年MM月
     */
    public String joinTarYYYYMMJaCal( String year ,int month) {
        final StringBuffer buffer = new StringBuffer();
        final String monthStr     = String.valueOf(month);
        buffer.append(year);
        buffer.append("年");
        addZeroBuf(month, buffer);
        buffer.append(monthStr);
        buffer.append("月");
        return  buffer.toString();
    }
    /**
     * 現在年月日の取得
     * @param  yyyyMMdd 指定日付:yyyy_MM_dd
     * @return 指定時刻yyyy年MM月dd日
     */
    public String getTargetYYYYMMDDJaCal( String yyyyMMdd) {

        final String yearStr  = yyyyMMdd.substring(0, 4);
        final String monthStr = yyyyMMdd.substring(5, 7);
        final String dayStr   = yyyyMMdd.substring(8, 10);
        StringBuffer buffer = new StringBuffer();
        buffer.append(yearStr);
        buffer.append("年");
        buffer.append(monthStr);
        buffer.append("月");
        buffer.append(dayStr);
        buffer.append("日");
        return  buffer.toString();
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

        final String yearStr  = String.valueOf(year);
        final String monthStr = String.valueOf(month);
        final String dayStr   = String.valueOf(day);

        StringBuffer buffer = new StringBuffer();
        buffer.append(yearStr);
        buffer.append("年");
        addZeroBuf(month, buffer);
        buffer.append(monthStr);
        buffer.append("月");
        addZeroBuf(day, buffer);
        buffer.append(dayStr);
        buffer.append("日");
        return  buffer.toString();
    }

    /**
     * 現在時刻の取得
     * @param  hour 指定日付（時）:HH
     * @param  minute 指定日付（分）:mm
     * @return 指定時刻HH時mm分
     */
    public String getTargetHourMinuteJaCal(int hour,int minute) {

        final StringBuffer buffer = new StringBuffer();
        final String hourStr      = String.valueOf(hour);
        final String minuteStr    = String.valueOf(minute);

        addZeroBuf(hour, buffer);
        buffer.append(hourStr);
        buffer.append("時");
        addZeroBuf(minute, buffer);
        buffer.append(minuteStr);
        buffer.append("分");

        return  buffer.toString();
    }



    /**
     * 時刻の差分
     * @param  endDateStr 退社時間 yy/MM/dd HH:mm:ss
     * @return 差分時刻 HH:mm
     * @throws ParseException 型変換時に起こりうる例外
     */
    public String getTimeDiff(String endDateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat ("yy/MM/dd HH:mm:ss");
        String provisDateStr       = endDateStr.substring(0,10) +" "+ PROVIS_TIME;
        Date provisDate            = null; // 規定時刻
        Date endDate               = null; // 終了時刻
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
        final long diffTime =  endDate.getTime()-provisDate.getTime();
        // 結果出力用フォーマット
        SimpleDateFormat timeFormatter = new SimpleDateFormat ("HH:mm:ss");
        timeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return timeFormatter.format(new Date(diffTime));
    }
    /**
     * 金曜日判定クラス
     * @param  string 判定用曜日
     * @return boolean 判定結果
     */
    public boolean isFriday(String tarWeek){
        if(tarWeek.equals("(金)")){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 土曜日判定クラス
     * @param  string 判定用曜日
     * @return boolean 判定結果
     */
    public boolean isSaturday(String tarWeek){
        if(tarWeek.equals("(土)")){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 日曜日判定クラス
     * @param  string 判定用曜日
     * @return boolean 判定結果
     */
    public boolean isSunday(String tarWeek){
        if(tarWeek.equals("(日)")){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 日曜日判定クラス
     * @param  string 判定用曜日
     * @return int 判定結果
     * 日曜 :0 , 月曜:1 ,火曜:2 , 水曜:3 , 木曜:4 , 金曜:5, 土曜:6 , 判定不能:99
     */
    public int isWeekDrive(String tarWeek){
        if(tarWeek.equals("(日)")){
            return 0;
        }
        if(tarWeek.equals("(月)")){
            return 1;
        }
        if(tarWeek.equals("(火)")){
            return 2;
        }
        if(tarWeek.equals("(水)")){
            return 3;
        }
        if(tarWeek.equals("(木)")){
            return 4;
        }
        if(tarWeek.equals("(金)")){
            return 5;
        }
        if(tarWeek.equals("(土)")){
            return 6;
        }
        return 99;
    }
}