package jp.co.tennti.timerecord.AsyncTaskUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jp.co.tennti.timerecord.R;
import jp.co.tennti.timerecord.commonUtils.BitmapUtils;

/**
 * Created by TENNTI on 2016/09/16.
 */
public class SetNavInfoAsyncTask extends AsyncTask<String, Void, String> {
    private Activity mainActivity;
    String mail     = "";
    String fullName = "";
    Bitmap bitmap   = null;
    private ImageView accountIconView = null;
    private TextView accountName = null;
    private TextView accountMail = null;

    public SetNavInfoAsyncTask(Activity activity, String mail, String fullName, Bitmap bitmap) {
        this.mainActivity = activity;
        this.mail     = mail;
        this.fullName = fullName;
        this.bitmap   = bitmap;
    }

    @Override
    protected String doInBackground(String... builder){
        System.out.println("----------------------------------------------------0");
        return "";
    }

    @Override
    protected void onPostExecute(String result){
        System.out.println(mainActivity);
        System.out.println("----------------------------------------------------");
        accountIconView  = (ImageView)mainActivity.findViewById(R.id.accountIconView);
        accountName      = (TextView)mainActivity.findViewById(R.id.accountName);
        accountMail      = (TextView)mainActivity.findViewById(R.id.accountMail);
        if (accountIconView == null || accountName == null || accountMail == null) {
            //mainActivity.setContentView(R.layout.activity_main);
            NavigationView navigationView = (NavigationView) mainActivity.findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);//findViewById(R.layout.nav_header_main);
            accountIconView  = (ImageView)header.findViewById(R.id.accountIconView);
            accountName      = (TextView)header.findViewById(R.id.accountName);
            accountMail      = (TextView)header.findViewById(R.id.accountMail);
            accountIconView.setImageBitmap(BitmapUtils.toRoundBitmap(this.bitmap));
            accountName.setText(this.fullName);
            accountMail.setText(this.mail);
            accountIconView.setScaleType(ImageView.ScaleType.FIT_START);
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
