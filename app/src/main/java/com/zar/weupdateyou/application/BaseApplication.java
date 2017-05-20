package com.zar.weupdateyou.application;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import  com.kgom.wuy.R;
import com.zar.weupdateyou.provider.MyDB;
import io.fabric.sdk.android.Fabric;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
/**
 * Created by KJS on 11/14/2016.
 */

public class BaseApplication extends MultiDexApplication {
    public static final String TAG = BaseApplication.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this);
        Fabric.with(this, new Crashlytics());
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
        Log.e(TAG, "onCreate");
    }

    MyDB db = null;
    public MyDB GetDB()
    {
        if(db == null)
        {
            db = new MyDB(this);
        }
        return db;
    }


}