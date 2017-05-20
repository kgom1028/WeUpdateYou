	package com.zar.weupdateyou.doc;

import android.content.Context;

import com.zar.weupdateyou.model.NewsModel;
import com.zar.weupdateyou.model.UserModel;

import java.util.ArrayList;

/**
 * Created by KJS on 11/15/2016.
 */
public class UserInfo {
    public static UserInfo _instance = null;
    public UserModel mAccount = null;
    public ArrayList<NewsModel> newsModels = null;
    public String previousActivity = "";
    public long previousTime = 0;
    public boolean shouldRefresh = false;
    public boolean settingChanged = false;
    public UserInfo()
    {

    }

    public static UserInfo getInstance()
    {
        if(_instance == null)
            _instance = new UserInfo();
        return _instance;
    }

    public void setSecret(Context mContext, String secret)
    {
        PrefManager.savePrefString(mContext,Constants.PREF_SECRET, secret);
    }

    public String getSecret(Context mContext)
    {
        return PrefManager.readPrefString(mContext,Constants.PREF_SECRET);
    }

    public void setUserName(Context mContext, String userName)
    {
        PrefManager.savePrefString(mContext,Constants.PREF_USERNAME, userName);
    }

    public String getUserName(Context mContext)
    {
        return PrefManager.readPrefString(mContext,Constants.PREF_USERNAME);
    }
    public void setPassword(Context mContext, String password)
    {
        PrefManager.savePrefString(mContext,Constants.PREF_PASSWORD, password);
    }

    public String getPassword(Context mContext)
    {
        return PrefManager.readPrefString(mContext,Constants.PREF_PASSWORD);
    }

    public void setAlarmSet(Context mContext)
    {
        PrefManager.savePrefBoolean(mContext,Constants.PREF_ALARM_SET, true);
    }

    public boolean getAlarmSet(Context mContext)
    {
        return PrefManager.readPrefBoolean(mContext,Constants.PREF_ALARM_SET);
    }


    public void setSourceSettingCount(Context mContext, int count)
    {
        PrefManager.savePrefInt(mContext,Constants.PREF_SOURCE_SETTING_COUNT, count);
    }

    public int getSourceSettingCount(Context mContext)
    {
        return PrefManager.readPrefInt(mContext,Constants.PREF_SOURCE_SETTING_COUNT);
    }

    public boolean isNotificationSound(Context mContext)
    {
        return PrefManager.readPrefBoolean(mContext,Constants.PREF_NOTIFICATION_SOUND, true);
    }
}
