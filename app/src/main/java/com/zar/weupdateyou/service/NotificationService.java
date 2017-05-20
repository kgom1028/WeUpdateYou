package com.zar.weupdateyou.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.kgom.wuy.R;
import com.zar.weupdateyou.activity.SplashActivity;
import com.zar.weupdateyou.doc.Constants;
import com.zar.weupdateyou.doc.ENUM;
import com.zar.weupdateyou.doc.UserInfo;
import com.zar.weupdateyou.model.NewsModel;
import com.zar.weupdateyou.model.UserModel;
import com.zar.weupdateyou.receiver.AlarmAlertWakeLock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by KJS on 11/25/2016.
 */
public class NotificationService extends IntentService {
    private NotificationManager mNotificationManager;
    private ServiceBinder mBinder = new ServiceBinder();
    private Intent mIntent;

    public NotificationService() {
        super(NotificationService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mIntent = intent;
        sendRequest();
        //   stopService(mIntent);
    }

    protected void sendRequest() {
        UserInfo userInfo = UserInfo.getInstance();
        String mUsername = userInfo.getUserName(this);
        String mPassword = userInfo.getPassword(this);
        String mSecret = userInfo.getSecret(this);
        if (!mUsername.equals("") && !mPassword.equals("") && !mSecret.equals("")) {
            UserModel userModel = new UserModel();
            userModel.mUserName = mUsername;
            userModel.mPassword = mPassword;
            userModel.mSecret = mSecret;
           // HttpServiceManager.getInstance().serviceGetNews(userModel, this);
            //String message = getString(R.string.notification_message, String.valueOf(10));
            //showNotification(this,message);

            List<NewsModel> models = getNews(userModel);
            if (models != null) {
                int unreadCount = 0;
                for (NewsModel model : models) {
                    if (model.mRead.equals("0"))
                        unreadCount++;
                }
                if(unreadCount > 0) {
                    String message = getString(R.string.notification_message, String.valueOf(unreadCount));
                    showNotification(this, message);
                }
            }
        }
        //String message = getString(R.string.notification_message,"1");
        //showNotification(this, message);
        stopSelf();
    }

    private List<NewsModel> getNews(UserModel userModel) {
        String requestUrl = Constants.GET_NEWS;
        String paramString = "username=" + userModel.mUserName + "&" +
                "secret=" + userModel.mSecret + "&" +
                "app=newsfeed&action=getnews";
        byte[] postData = new byte[0];
        try {
            postData = paramString.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int postDataLength = postData.length;
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
                /* forming th java.net.URL object */
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            urlConnection.setUseCaches(false);

            OutputStream os = urlConnection.getOutputStream();
            os.write(postData);
            os.flush();
            os.close();
            int statusCode = urlConnection.getResponseCode();
        /* 200 represents HTTP OK */
            if (statusCode == 200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                try {
                    JSONObject jsonData = new JSONObject(response);
                    JSONObject jsonObject = jsonData.getJSONObject("mynewsfeed");
                    List<NewsModel> newsList = new ArrayList<NewsModel>();
                    Iterator<?> keys = jsonObject.keys();
                    while( keys.hasNext() ) {
                        String key = (String)keys.next();
                        if ( jsonObject.get(key) instanceof JSONObject ) {
                            JSONObject newsObject = (JSONObject) jsonObject.get(key);
                            NewsModel mModel = new NewsModel();
                            mModel.parse(newsObject);
                            newsList.add(mModel);
                        }
                    }
                    return newsList;

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            } else {
                return null;
            }


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

            /* Close Stream */
        if (null != inputStream) {
            inputStream.close();
        }

        return result;
    }

    private void showNotification(Context context, String message) {
        //You can do the processing here update the widget/remote views.
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, SplashActivity.class), 0);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.ic_notification_lollipop);
        } else {
            mBuilder.setSmallIcon(R.drawable.ic_notification);
        }
        mBuilder.setContentTitle(context.getString(R.string.app_title))
                .setTicker(context.getString(R.string.app_title))
                .setAutoCancel(true)
                .setContentText(message).setContentIntent(contentIntent);



        if (UserInfo.getInstance().isNotificationSound(context)) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
        } else mBuilder.setSound(null);
        Random r = new Random();
        int i1 = Constants.NotificationId;
        mNotificationManager.notify(i1, mBuilder.build());
    }

    /*
        @Override
        public void OnSuccess(Object response, ENUM.SERVICE_TYPE type) {
            List<NewsModel> models = (List<NewsModel>) response;
            int unreadCount = 0;
            for (NewsModel model : models) {
                if (model.mRead.equals("0"))
                    unreadCount++;
            }
            String message = getString(R.string.notification_message, String.valueOf(unreadCount));
            showNotification(this, message);
        }

        @Override
        public void OnError(String ErrorMsg, ENUM.SERVICE_TYPE type) {
            Log.d("Err", ErrorMsg);
        }

        @Override
        public void OnFaild(ENUM.SERVICE_TYPE type) {
            Log.d("Err", "Faild");
        }

        @Override
        public void OnFinished(ENUM.SERVICE_TYPE type) {
            stopService(mIntent);
        }
*/
    public class ServiceBinder extends Binder {
        /**
         * Return the service.
         */
        public NotificationService getService() {
            return NotificationService.this;
        }
    }
}
