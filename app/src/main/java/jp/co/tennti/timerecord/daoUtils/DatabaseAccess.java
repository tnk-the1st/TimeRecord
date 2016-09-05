package jp.co.tennti.timerecord.daoUtils;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

import jp.co.tennti.timerecord.contacts.Constants;

/**
 * Created by TENNTI on 2016/06/02.
 */
public class DatabaseAccess {

    /** データベースファイルのフルパス */
    private static final String DB_NAME = Constants.DB_DIRECTORY + "time_record_db.db";

    private SQLiteDatabase db = null;

    /**
     * SDカード上のデータベースを開く。もしデータベースが開けない、または
     * 作成できない場合は例外を投げる。
     *
     * @return true if successful
     * @throws SQLException if the database is unable to be opened or created
     */
    public boolean openDatabase() throws SQLException {
        if (db != null && db.isOpen()) {
            return true;
        } else {
            if (!new File(Constants.DB_DIRECTORY).exists()) {
                new File(Constants.DB_DIRECTORY).mkdirs();
            }

            try {
                db = SQLiteDatabase.openOrCreateDatabase(DB_NAME, null);
            } catch (SQLException e) {
                throw e;
            }
        }
        return true;
    }
}
