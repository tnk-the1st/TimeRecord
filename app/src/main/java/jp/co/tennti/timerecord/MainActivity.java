package jp.co.tennti.timerecord;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import jp.co.tennti.timerecord.daoUtils.MySQLiteOpenHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*フラグメントの初期読込み*/
        if (savedInstanceState == null) {
            MainFragment fragment = new MainFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_main, fragment).commit();
        }
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        //タイトルバーの背景色セット
        Drawable titleGrad = getDrawable(R.drawable.gradient_black);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackground(titleGrad);
        setSupportActionBar(toolbar);
        
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /******基本処理******/
        /******月ごとテーブル再作成 START******/
        /**DB接続**/
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this.getApplicationContext());
        final SQLiteDatabase db = helper.getWritableDatabase();
        /**DB接続**/        MySQLiteOpenHelper sqlLiteAdepter = new MySQLiteOpenHelper(this);
        sqlLiteAdepter.reloadOnFire(db);
        /******月ごとテーブル再作成 END******/
        // ボタンを設定
        /*final ImageButton timeCountButton = (ImageButton)findViewById(R.id.timeCountButton);

        // リスナーをボタンに登録
        timeCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeUtils.getCurrentTime();
                timeCountButton.setEnabled(false);
                timeCountButton.setColorFilter(Color.argb(100, 0, 0, 0));
            }
        });

        // ボタンを設定
        final ImageButton controlButton = (ImageButton)findViewById(R.id.controlButton);
        // リスナーをボタンに登録
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeCountButton.setEnabled(true);
                timeCountButton.setColorFilter(null);
            }
        });*/
        /******基本処理******/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // メニュー作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    // メニューアイテム選択イベント
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.list_screen:
                // 一覧を起動
                /*Intent intent = new Intent(getApplication(),ListViewFragment.class);
                startActivity(intent);*/
                /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ListViewFragment next = new ListViewFragment();
                ft.replace(R.id.bar_container,next);
                //ft.addToBackStack(null);
                ft.commit();*/

                /*ImageView imgView = (ImageView)findViewById(R.id.contentImageView);
                *//*imgView.setImageDrawable(null);
                imgView.setImageBitmap(null);*//*
                Drawable drawable = imgView.getDrawable();
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                bitmap.recycle();*/

                ListViewFragment listViewFragment = new ListViewFragment();
                transaction.replace(R.id.fragment_main, listViewFragment).addToBackStack(null).commit();
                break;
            case R.id.regist_screen:
                // 登録画面を起動
                //finish();

                /*ImageView imgView2 = (ImageView)findViewById(R.id.listImageView);
                *//*imgView2.setImageDrawable(null);
                imgView2.setImageBitmap(null);*//*
                Drawable drawable2 = imgView2.getDrawable();
                BitmapDrawable bitmapDrawable2 = (BitmapDrawable) drawable2;
                Bitmap bitmap2 = bitmapDrawable2.getBitmap();
                bitmap2.recycle();*/

                MainFragment mainFragment = new MainFragment();
                transaction.replace(R.id.fragment_main, mainFragment).addToBackStack(null).commit();
                break;
            case R.id.edit_screen:
                // 編集画面を起動
                EditFragment editFragment = new EditFragment();
                transaction.replace(R.id.fragment_main, editFragment).addToBackStack(null).commit();
                break;
        }
        System.gc();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupView(findViewById(R.id.fragment_main));
        cleanupView(findViewById(R.id.regist_screen));
        cleanupView(findViewById(R.id.drawer_layout));
    }

    /**
     * 指定したビュー階層内のドローワブルをクリアする。
     * （ドローワブルをのコールバックメソッドによるアクティビティのリークを防ぐため）
     * @param view
     */
    public static final void cleanupView(View view) {
        if(view instanceof ImageButton) {
            ImageButton ib = (ImageButton)view;
            ib.setImageDrawable(null);
            ib.setImageBitmap(null);
        } else if(view instanceof ImageView) {
            ImageView iv = (ImageView)view;
            iv.setImageDrawable(null);
            iv.setImageBitmap(null);
        } else if(view instanceof SeekBar) {
            SeekBar sb = (SeekBar)view;
            sb.setProgressDrawable(null);
            sb.setThumb(null);
            // } else if(view instanceof( xxxx )) {  -- 他にもDrawable
            //を使用するUIコンポーネントがあれば追加
        }
        view.setBackgroundDrawable(null);
        if(view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup)view;
            int size = vg.getChildCount();
            for(int i = 0; i < size; i++) {
                cleanupView(vg.getChildAt(i));
            }
        }
    }

}
