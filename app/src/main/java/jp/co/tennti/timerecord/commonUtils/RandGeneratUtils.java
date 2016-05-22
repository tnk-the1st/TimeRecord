package jp.co.tennti.timerecord.commonUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


/**
 * Created by TENNTI on 2016/04/10.
 */
public class RandGeneratUtils {

    /**
     * 一意性を保証されたCDを取得します。 生成される文字列の長さは 15 バイトです。
     * @return ユニークCD
     * @memo 13+7桁
     */
    public String get() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(make());
        buffer.append(RandomStringUtils.randomAlphanumeric(7).toLowerCase());
        return buffer.toString();
    }
    /**
     * ユニークＩＤを作成します。
     * @return ユニークＩＤ
     */
    public static String make() {
        return getUniqueId();
    }


    private static final int max = Integer.parseInt("zz", 36);
    private static volatile int seq = (new Random()).nextInt(max);
    private static SimpleDateFormat datePattern = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    /**
     * ユニークなＩＤを作成します。<br/>
     * 現在のプロセスに対して一意性を保証するＩＤを生成します。
     * ＩＤは時間情報およびこのクラスが持つシーケンス番号から構成されています。
     * これによりメソッド呼び出しの度に異なる文字列を生成しＩＤとして返します。
     * 生成される文字列の長さは 13 です。
     * 本メソッドは、IDの一意性を保障する為に synchronized メソッドとなっています。
     *
     * @return ユニークＩＤ
     */
    public synchronized static final String getUniqueId(){
        final Date now = new Date();
        StringBuffer buffer = new StringBuffer();
        buffer.append(Long.toString(Long.parseLong(datePattern.format(now)), 36));
        buffer.append(sequencer());
        /*String dateFormat = Long.toString(Long.parseLong(datePattern.format(now)), 36);
        String sequence = sequencer();*/
        return buffer.toString();//dateFormat.concat(sequence);
    }
    /**
     * シーケンスを取得して返却。
     * シーケンスは、このメソッドがコールされるたびにインクリメントされる値。
     *
     * @return　返却値は、３６進数２桁の文字列。
     */
    private static final String sequencer(){

        if(seq == max){
            seq = 0;
        }
        else{
            seq++;
        }

        // 桁チェック後３６進数変換して返却
        if(seq < 36){
            StringBuffer buffer = new StringBuffer();
            buffer.append("0");
            buffer.append(Integer.toString(seq, 36));
            return buffer.toString();
        }
        else{
            return Integer.toString(seq, 36);
        }
    }
}
