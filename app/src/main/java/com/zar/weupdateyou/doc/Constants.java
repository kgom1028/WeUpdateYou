package com.zar.weupdateyou.doc;

import android.os.Environment;

/**
 * Created by KJS on 11/15/2016.
 */
public class Constants {
    public static final String APP_PAKAGE_NAME = "com.zar.weupdateyou";
    public static final String OFFINE_DB_NAME = "newsapp_db";
    public static String SITE_URL = "http://weupdateyou.com/";
    public static String BASE_URL = SITE_URL + "api.php?";
    public static String GET_SECRET = BASE_URL;
    public static String CREATE_USER = BASE_URL;
    public static String GET_NEWS = BASE_URL;
    public static String REPORT = "http://weupdateyou.com/report.php";
    public static String SWIPE_ACTION= BASE_URL;
    public static String READ_ACTION = BASE_URL ;
    public static String GET_SETTINGS = BASE_URL;
    public static String GET_POPULAR_FEEDS = BASE_URL ;
    public static String FIND_FEED = BASE_URL;
    public static String ADD_USER_FEED = BASE_URL;
    public static String Edit_USER_FEED_VALUES = BASE_URL ;
    public static String REMOVE_USER_FEED = BASE_URL ;
    public static String TRANSLATE_URL = "https://translate.google.com/translate?hl=en&sl=auto&tl=en&u=";


    public static String ERR_JSON_PARSING = "err_json_parsing";
    public static String PREF_SECRET = "pref_secret";
    public static String PREF_USERNAME = "pref_username";
    public static String PREF_PASSWORD = "pref_password";
    public static String PREF_ALARM_SET = "pref_alarm_set";
    public static String PREF_NOTIFICATION_SOUND = "pref_notification_sound";
    public static String PREF_SOURCE_SETTING_COUNT = "pref_source_setting_count";

    public static String READ = "Read";
    public static String UNREAD = "Unread";
    public static final int refreshdelay = 1000*60*10;

    public static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DOWNLOAD_DIR = ROOT_DIR+"/wuy_download";


    public static int HOT_LIMIT = 2000;
    public static int SOURCE_MIN =3;
    public static int MIN_ROW =2;
    public static int NotificationId = 2983;


}
