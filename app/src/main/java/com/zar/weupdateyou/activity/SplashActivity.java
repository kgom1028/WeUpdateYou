package com.zar.weupdateyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.kgom.wuy.R;
import com.zar.weupdateyou.doc.ENUM;
import com.zar.weupdateyou.doc.Globals;
import com.zar.weupdateyou.doc.UserInfo;
import com.zar.weupdateyou.model.NewsModel;
import com.zar.weupdateyou.model.SettingModel;
import com.zar.weupdateyou.model.UserModel;
import com.zar.weupdateyou.service.HttpServiceManager;
import com.zar.weupdateyou.service.ServiceListener;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by KJS on 11/15/2016.
 */
public class SplashActivity extends  BaseActivity implements ServiceListener {
    private HttpServiceManager serviceManager;
    private UserInfo userInfo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }

        setContentView(R.layout.activity_splash);

        userInfo = UserInfo.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                initReg();
            }
        }, 0);


    }
    private void init() {
        serviceManager = HttpServiceManager.getInstance();
        String mUsername =userInfo.getUserName(this);
        String mPassword = userInfo.getPassword(this);
        String mSecret = userInfo.getSecret(this);
        if(!mUsername.equals("") && !mPassword.equals("") && !mSecret.equals(""))
        {
            UserModel userModel = new UserModel();
            userModel.mUserName =mUsername;
            userModel.mPassword = mPassword;
            userModel.mSecret = mSecret;

            //serviceManager.serviceGetSecret(mUsername,mPassword,this);
            serviceManager.serviceGetSettings(userModel,this);
            //serviceManager.serviceGetNews(userModel,this);

        }else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void initReg()
    {
        if(Globals.isNetworkAvailable(this.getApplicationContext()))
        {
            init();
        }else {
           /* AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setCancelable(true);
            alert.setTitle("Oops!!!");
            alert.setNeutralButton("Okay!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    SplashActivity.this.finish();
                }
            });
            alert.setMessage("Check internet connection.");
            alert.show();*/

            UserModel userModel = new UserModel();
            userModel.mUserName =userInfo.getUserName(this);
            userModel.mPassword = userInfo.getPassword(this);
            userModel.mSecret = userInfo.getSecret(this);
            if(userModel.mSecret.equals(""))
            {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            else
            {
                userInfo.mAccount = userModel;
                userInfo.newsModels = application.GetDB().getNews();
                startActivity(new Intent(this, HomeActivity.class));
               // overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }

    }

    protected void onPause()
    {
        super.onPause();
        UserInfo.getInstance().previousActivity = this.getClass().getSimpleName();
    }

    @Override
    public void OnSuccess(Object response, ENUM.SERVICE_TYPE type) {
        if(type == ENUM.SERVICE_TYPE.GET_SECRET)
        {
            userInfo.mAccount = (UserModel) response;
            userInfo.setPassword(this,userInfo.mAccount.mPassword);
            userInfo.setUserName(this,userInfo.mAccount.mUserName);
            userInfo.setSecret(this,userInfo.mAccount.mSecret);

        }
        if(type == ENUM.SERVICE_TYPE.GET_NEWS)
        {
            userInfo.newsModels = (ArrayList<NewsModel>) response;
            UserModel userModel = new UserModel();
            userModel.mUserName =userInfo.getUserName(this);
            userModel.mPassword = userInfo.getPassword(this);
            userModel.mSecret = userInfo.getSecret(this);
            userInfo.mAccount = userModel;



        }
        if(type == ENUM.SERVICE_TYPE.GET_SETTINGS)
        {
            List<SettingModel> items = (List<SettingModel>) response;
            UserInfo.getInstance().setSourceSettingCount(this,items.size());
            UserModel userModel = new UserModel();
            userModel.mUserName =userInfo.getUserName(this);
            userModel.mPassword = userInfo.getPassword(this);
            userModel.mSecret = userInfo.getSecret(this);
            userInfo.mAccount = userModel;

            startActivity(new Intent(this, HomeActivity.class));
            finish();

        }


    }

    @Override
    public void OnError(String ErrorMsg, ENUM.SERVICE_TYPE type) {
        finish();
        startActivity(new Intent(this, LoginActivity.class));

    }

    @Override
    public void OnFaild(ENUM.SERVICE_TYPE type) {
        finish();
        startActivity(new Intent(this, LoginActivity.class));

    }

    @Override
    public void OnFinished(ENUM.SERVICE_TYPE type) {

    }
}
