package com.zar.weupdateyou.provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.SyncStateContract;
import android.util.Log;

import com.zar.weupdateyou.doc.Constants;
import com.zar.weupdateyou.model.NewsModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KJS on 11/18/2016.
 */
public class MyDB {
    public final static String DB_PATH = "/data/data/" + Constants.APP_PAKAGE_NAME + "/databases/" + Constants.OFFINE_DB_NAME;

    private SQLiteDatabase database;
    private DBHelper mDBHelper;
    private Context ctx;
    public MyDB(Context ctx){

        this.ctx = ctx;

        // install db.
        boolean init = initDB();

        mDBHelper = new DBHelper(ctx);
        database = mDBHelper.getWritableDatabase();

    }

    public Context GetContext() {
        return ctx;
    }

    private boolean initDB()
    {
        try {
            Context ctx = GetContext();
            boolean bResult = isCheckDB(ctx);  // Is there a DB?
            Log.d("NewsApp", "DB Check=" + bResult);
            if(!bResult){   // If there is no
                copyDB(ctx);
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Is there a db?
    public boolean isCheckDB(Context mContext){
        String filePath = "/data/data/" + Constants.APP_PAKAGE_NAME + "/databases/" + Constants.OFFINE_DB_NAME;
        File file = new File(filePath);

        if (file.exists()) {
            return true;
        }

        return false;

    }
    // copy db file to internal mermory.
    public void copyDB(Context mContext){
        Log.d("NewsApp", "copyDB");
        AssetManager manager = mContext.getAssets();
        String folderPath = "/data/data/" + Constants.APP_PAKAGE_NAME + "/databases";
        String filePath = "/data/data/" + Constants.APP_PAKAGE_NAME + "/databases/" + Constants.OFFINE_DB_NAME;
        File folder = new File(folderPath);
        File file = new File(filePath);

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            InputStream is = manager.open(Constants.OFFINE_DB_NAME);
            BufferedInputStream bis = new BufferedInputStream(is);

            if (folder.exists()) {
            }else{
                folder.mkdirs();
            }


            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            int read = -1;
            byte[] buffer = new byte[1024];
            while ((read = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, read);
            }

            bos.flush();

            bos.close();
            fos.close();
            bis.close();
            is.close();

        }
        catch (IOException e) {
            Log.e("ErrorMessage : ", e.getMessage());

        }

    }

    public void closeDB()
    {
        database.close();
    }
    public void SetContext(Context context) {
        this.ctx = context;
    }

    public boolean checkDomain(String domain)
    {
        try{
            String selectQuery = "select count(*) from "+DBHelper.TABLE_DOMAIN +" WHERE domain_address =\""+domain+"\"";
            Cursor mCount = database.rawQuery(selectQuery,null);
            mCount.moveToFirst();
            int count = mCount.getInt(0);
            mCount.close();
            return count > 0;
        }catch(Exception e)
        {
            Log.e("ErrorMessage : ", e.getMessage());
            return false;
        }


    }

    public void insertNewsList(List<NewsModel> items)
    {
        for(NewsModel item : items)
        {
            insertNews(item);
        }
    }
    public void insertNews(NewsModel item)
    {
        try{
            ContentValues values =new ContentValues();
            values.put("guid", item.mGuid);
            values.put("title", item.mTitle);
            values.put("link", item.mLink);
            values.put("description", item.mDescription);
            values.put("comments", item.mComments);
            values.put("imglink", item.mImageLink);
            values.put("icolink", item.mIcoLink);
            values.put("source", item.mSource);
            values.put("nice_source", item.mTag);
            values.put("shares", item.mShares);
            values.put("mread", item.mRead);
            values.put("showall", item.mShowAll);
            values.put("local_imglink", item.mLocalImageLink);
            values.put("YourTopStories", item.mYourTopStories);
            values.put("pubDate", item.mPubDate);
            values.put("category", item.mCategory);
            values.put("found", item.mFound);
            values.put("difference", item.mTimeArray.mDifference);
            values.put("period", item.mTimeArray.mPeriod);
            values.put("extention", item.mTimeArray.mExtension);
            values.put("label", item.mTimeArray.mLabel);
            database.insert(DBHelper.TABLE_NEWS, null, values);
        }catch(Exception e)
        {
            Log.e("ErrorMessage : ", e.getMessage());
        }

    }

    public void removeAllNews()
    {
        try{
            database.delete(DBHelper.TABLE_NEWS, null, null);
        }catch (Exception e)
        {
            Log.e("ErrorMessage : ", e.getMessage());
        }

    }

    public String getLocalUrl(String url)
    {
        try{
            String selectQuery = "SELECT  * FROM " + DBHelper.TABLE_DOWNLOADS +" WHERE url =\"" + url+"\"" ;
            String localUrl = "";
            Cursor cursor = database.rawQuery(selectQuery, null);
            if(cursor.moveToFirst()) {
                localUrl = cursor.getString(cursor.getColumnIndex("local_url"));
            }
            cursor.close();
            return localUrl;
        }catch (Exception e)
        {
            Log.e("ErrorMessage : ", e.getMessage());
            return "";
        }


    }

    public boolean checkDownloadedURL(String url)
    {
        try {
            String selectQuery = "select count(*) from "+DBHelper.TABLE_DOWNLOADS +" WHERE url =\""+url+"\"";
            Cursor mCount = database.rawQuery(selectQuery,null);
            mCount.moveToFirst();
            int count = mCount.getInt(0);
            mCount.close();
            return count > 0;
        }catch (Exception e)
        {
            Log.e("ErrorMessage : ", e.getMessage());
            return false;
        }

    }

    public void insertDownloadedURL(String url, String localUrl) {
        try {
            long time = System.currentTimeMillis();
            ContentValues values = new ContentValues();
            values.put("url", url);
            values.put("local_url", localUrl);
            values.put("time", time);
            database.insert(DBHelper.TABLE_DOWNLOADS, null, values);
        } catch (Exception e) {
            Log.e("ErrorMessage : ", e.getMessage());

        }
    }

    public ArrayList<NewsModel> getNews()
    {
        ArrayList<NewsModel> newsList = new ArrayList<NewsModel>();
        try {

            String selectQuery = "SELECT  * FROM " + DBHelper.TABLE_NEWS;

            Cursor cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    NewsModel model = new NewsModel();
                    model.mId = cursor.getInt(cursor.getColumnIndex("id"));
                    model.mGuid = cursor.getString(cursor.getColumnIndex("guid"));
                    model.mTitle = cursor.getString(cursor.getColumnIndex("title"));
                    model.mLink = cursor.getString(cursor.getColumnIndex("link"));
                    model.mDescription = cursor.getString(cursor.getColumnIndex("description"));
                    model.mComments = cursor.getString(cursor.getColumnIndex("comments"));
                    model.mImageLink = cursor.getString(cursor.getColumnIndex("imglink"));
                    model.mIcoLink = cursor.getString(cursor.getColumnIndex("icolink"));
                    model.mSource = cursor.getString(cursor.getColumnIndex("source"));
                    model.mTag = cursor.getString(cursor.getColumnIndex("nice_source"));
                    model.mShares = cursor.getString(cursor.getColumnIndex("shares"));
                    model.mRead = cursor.getString(cursor.getColumnIndex("mread"));
                    model.mShowAll = cursor.getString(cursor.getColumnIndex("showall"));
                    model.mLocalImageLink = cursor.getString(cursor.getColumnIndex("local_imglink"));
                    model.mYourTopStories = cursor.getString(cursor.getColumnIndex("YourTopStories"));
                    model.mPubDate = cursor.getString(cursor.getColumnIndex("pubDate"));
                    model.mCategory = cursor.getString(cursor.getColumnIndex("category"));
                    model.mFound = cursor.getString(cursor.getColumnIndex("found"));
                    model.mTimeArray.mDifference = cursor.getString(cursor.getColumnIndex("difference"));
                    model.mTimeArray.mPeriod = cursor.getString(cursor.getColumnIndex("period"));
                    model.mTimeArray.mExtension = cursor.getString(cursor.getColumnIndex("extention"));
                    model.mTimeArray.mLabel = cursor.getString(cursor.getColumnIndex("label"));
                    newsList.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception e)
        {
            Log.e("ErrorMessage : ", e.getMessage());
        }

        return newsList;
    }

}
