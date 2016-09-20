package jp.co.tennti.timerecord.AsyncTaskUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import jp.co.tennti.timerecord.R;
import jp.co.tennti.timerecord.commonUtils.BitmapUtils;

/**
 * Created by TENNTI on 2016/09/16.
 */
public class SetNavInfoAsyncTask extends AsyncTask<String, Void, String> {
    Activity mainActivity;
    String mail     = "";
    String fullName = "";
    Bitmap bitmap   = null;

    public SetNavInfoAsyncTask(Activity activity, String mail, String fullName, Bitmap bitmap) {
        mainActivity = activity;
        this.mail     = mail;
        this.fullName = fullName;
        this.bitmap   = bitmap;
    }

    @Override
    protected String doInBackground(String... builder){
        return "";
    }

    @Override
    protected void onPostExecute(String result){
        ImageView accountIconView = (ImageView)mainActivity.findViewById(R.id.accountIconView);
        TextView accountName      = (TextView)mainActivity.findViewById(R.id.accountName);
        TextView accountMail      = (TextView)mainActivity.findViewById(R.id.accountMail);
        if (accountIconView == null || accountName == null || accountMail == null) {
            return;
        }
        //accountIconView.setScaleType(ImageView.ScaleType.FIT_START);
        if (this.bitmap != null) {
            accountIconView.setScaleType(ImageView.ScaleType.FIT_START);
            accountIconView.setImageBitmap(BitmapUtils.toRoundBitmap(this.bitmap));
        }
        accountName.setText(this.fullName);
        accountMail.setText(this.mail);

    }
}
