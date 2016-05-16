package jp.co.tennti.timerecord.commonUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by TENNTI on 2016/04/15.
 */
public class BitmapUtils {
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // 画像の元サイズ
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {

        // inJustDecodeBounds=true で画像のサイズをチェック
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // inSampleSize を計算
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // inSampleSize をセットしてデコード
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
}
