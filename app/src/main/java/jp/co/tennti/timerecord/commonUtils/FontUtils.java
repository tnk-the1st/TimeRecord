package jp.co.tennti.timerecord.commonUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * フォント関連のユーティリティ
 */
public class FontUtils {
    /**
     * フォントファイルtt○を assets から読み込みます。
     * @param context コンテキスト。
     * @param path    フォント ファイルを示す assets フォルダからの相対パス。
     * @return 成功時は Typeface インスタンス。それ以外は null。
     */
    public static Typeface getTypefaceFromAssets( Context context, String path ) {
        return Typeface.createFromAsset( context.getAssets(), path );
    }
    /**
     * フォントZIPを assets から読み込みます。
     * @param context コンテキスト。
     * @param path    フォントZIP ファイルを示す assets フォルダからの相対パス。
     * @return 成功時は Typeface インスタンス。それ以外は null。
     */
    public static Typeface getTypefaceFromAssetsZip( Context context,String path ) {
        //zip圧縮用のメソッド

        try {
            AssetManager	am	= context.getAssets();
            InputStream is	= null;
            is = am.open(path, AssetManager.ACCESS_STREAMING);
            ZipInputStream	zis	= new ZipInputStream(is);
            ZipEntry ze	= zis.getNextEntry();
            if (ze != null) {
                path = context.getFilesDir().toString() + "/" + ze.getName();
                FileOutputStream fos = new FileOutputStream(path, false);
                byte[] buf = new byte[1024];
                int size = 0;
                while ((size = zis.read(buf, 0, buf.length)) > -1) {
                    fos.write(buf, 0, size);
                }
                fos.close();
                zis.closeEntry();
            }
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ZIP IOException" , e.getMessage());
        }
        Typeface typefaceOriginal = null;
        if(path != null){
            typefaceOriginal = Typeface.createFromFile(path);
        }
        return  typefaceOriginal;
    }
}