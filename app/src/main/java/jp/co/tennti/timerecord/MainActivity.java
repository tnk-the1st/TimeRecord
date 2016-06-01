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

import jp.co.tennti.timerecord.daoUtils.DatabaseAccess;
import jp.co.tennti.timerecord.daoUtils.MySQLiteOpenHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*フラグメントの初期読込み*/
        if (savedInstanceState == null) {
            final MainFragment fragment = new MainFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_main, fragment).commit();
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
        DatabaseAccess aba = new DatabaseAccess();
        aba.openDatabase();
        /******月ごとテーブル再作成 START******/
        /**DB接続**/
        final MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this.getApplicationContext());
        final SQLiteDatabase db = helper.getWritableDatabase();
        /**DB接続**/
        final MySQLiteOpenHelper sqlLiteAdepter = new MySQLiteOpenHelper(this);
        sqlLiteAdepter.reloadOnFire(db);
        /******月ごとテーブル再作成 END******/
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
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        final ListViewFragment listViewFragment = new ListViewFragment();
        final MainFragment mainFragment         = new MainFragment();
        final EditFragment editFragment         = new EditFragment();
        switch (item.getItemId()) {
            case R.id.list_screen:
                // 一覧を起動
                transaction.replace(R.id.fragment_main, listViewFragment).addToBackStack(null).commit();
                break;
            case R.id.regist_screen:
                // 登録画面を起動
                //finish();
                transaction.replace(R.id.fragment_main, mainFragment).addToBackStack(null).commit();
                break;
            case R.id.edit_screen:
                // 編集画面を起動
                transaction.replace(R.id.fragment_main, editFragment).addToBackStack(null).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();
        final FragmentTransaction transaction   = getSupportFragmentManager().beginTransaction();
        final MainFragment mainFragment         = new MainFragment();
        final ListViewFragment listViewFragment = new ListViewFragment();
        final EditFragment editFragment         = new EditFragment();
        if (id == R.id.main_content) {
            transaction.replace(R.id.fragment_main, mainFragment).addToBackStack(null).commit();
        } else if (id == R.id.list_content) {
            transaction.replace(R.id.fragment_main, listViewFragment).addToBackStack(null).commit();
        } else if (id == R.id.edit_content) {
            transaction.replace(R.id.fragment_main, editFragment).addToBackStack(null).commit();
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    protected static final void cleanupView(View view) {
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
            final int size = vg.getChildCount();
            for(int i = 0; i < size; i++) {
                cleanupView(vg.getChildAt(i));
            }
        }
    }
}
