package com.zar.weupdateyou.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by KJS on 11/18/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NEWS = "tbl_news";
    public static final String TABLE_DOWNLOADS = "tbl_downloads";
    public static final String TABLE_DOMAIN = "tbl_domain";
    private static final String DATABASE_NAME = "newsapp_db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String CREATE_TABLE_NEWS = "CREATE TABLE IF NOT EXISTS " + TABLE_NEWS + " ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + "guid TEXT, "
            + "title TEXT, "
            + "link TEXT, "
            + "description TEXT, "
            + "comments TEXT, "
            + "imglink TEXT, "
            + "icolink TEXT, "
            + "source TEXT, "
            + "nice_source TEXT, "
            + "short_link TEXT, "
            + "shares TEXT, "
            + "mread TEXT, "
            + "showall TEXT, "
            + "local_imglink TEXT, "
            + "YourTopStories TEXT, "
            + "pubDate TEXT, "
            + "category TEXT, "
            + "found TEXT, "
            + "difference TEXT, "
            + "period TEXT, "
            + "extention TEXT, "
            + "label TEXT"
            + ");";

    private static final String CREATE_TABLE_DOWNLOADS = "CREATE TABLE IF NOT EXISTS " + TABLE_DOWNLOADS + " ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + "url TEXT, "
            + "local_url TEXT, "
            + "time INTEGER"
            + ");";

    public DBHelper(Context context) {
        super(context, MyDB.DB_PATH, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

     //   database.execSQL(CREATE_TABLE_NEWS);
     //   database.execSQL(CREATE_TABLE_DOWNLOADS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    //    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
    //    db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADS);
    //    onCreate(db);
    }


}
