package com.zar.weupdateyou.receiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.kgom.wuy.R;
import com.zar.weupdateyou.activity.SplashActivity;
import com.zar.weupdateyou.doc.ENUM;
import com.zar.weupdateyou.doc.UserInfo;
import com.zar.weupdateyou.model.NewsModel;
import com.zar.weupdateyou.model.Time;
import com.zar.weupdateyou.model.UserModel;
import com.zar.weupdateyou.service.HttpServiceManager;
import com.zar.weupdateyou.service.NotificationService;
import com.zar.weupdateyou.service.ServiceListener;
import com.zar.weupdateyou.service.StaticWakeLock;


public class AlarmManagerBroadcastReceiver extends WakefulBroadcastReceiver {
    private NotificationManager mNotificationManager;
    private  static int NOTIFICATION_REMINDER_NIGHT = 232;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
          mContext = context;
     //     StaticWakeLock.lockOn(context);
     //     AlarmAlertWakeLock.acquireCpuWakeLock(context);
         Intent serviceIntent = NotificationService.createIntentStartNotificationService(context);


        if (serviceIntent != null) {
            startWakefulService(context,serviceIntent);

        }
    }
    public static void setupAlarm(Context context)
    {
            cancelAlarm(context);
        //    setAlarm(context,1000);
        //    TestAlarm(context);
            String[] timeArray =  context.getResources().getStringArray(R.array.alarm_time_array);
            List<Time> times = new ArrayList<Time>();
            for(String timeStr : timeArray)
            {
                String[] tempTime = timeStr.split(":");
                if(tempTime.length != 3)
                    continue;;
                Time time = new Time();
                time.hour = Integer.parseInt(tempTime[0]);
                time.minute = Integer.parseInt(tempTime[1]);
                time.second = Integer.parseInt(tempTime[2]);
                times.add(time);
            }
            setRepeatedAlarm(context, times);

    }
    public static void setAlarm(Context context, long triggerTimeMill)
    {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        boolean isWorking = (PendingIntent.getBroadcast(context, 1001,
                  intent, PendingIntent.FLAG_NO_CREATE) != null);
        if (isWorking) {
            // first cancel previous alarm
             PendingIntent pI = PendingIntent.getBroadcast(context, 1001,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);// the same as up
            alarmManager.cancel(pI);// important
            pI.cancel();// important
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,  intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+triggerTimeMill ,10000, pendingIntent);

    }

    public static void setRepeatedAlarm(Context context, List<Time> times)
    {
        int index =NOTIFICATION_REMINDER_NIGHT;
        for(Time time : times)
        {
             setRepeatedAlarm(context, time, index);
            index++;
        }
    }
    public static void setRepeatedAlarm(Context context, Time time, int reqCode)
    {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, time.hour);
        calendar.set(Calendar.MINUTE, time.minute);
        calendar.set(Calendar.SECOND, time.second);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("requestCode", reqCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (context, reqCode, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);
    }

    public static void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
    protected void sendRequest() {
        if(mContext == null)
            return;
        UserInfo userInfo = UserInfo.getInstance();
        String mUsername =userInfo.getUserName(mContext);
        String mPassword = userInfo.getPassword(mContext);
        String mSecret = userInfo.getSecret(mContext);
        if(!mUsername.equals("") && !mPassword.equals("") && !mSecret.equals(""))
        {
            UserModel userModel = new UserModel();
            userModel.mUserName = mUsername;
            userModel.mPassword = mPassword;
            userModel.mSecret = mSecret;
            //HttpServiceManager.getInstance().serviceGetNews(userModel,this);
            String message = mContext.getString(R.string.notification_message, String.valueOf(10));
            showNotification(mContext,message);
        }
    }

    private void showNotification(Context context, String message)
    {
        //You can do the processing here update the widget/remote views.
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, SplashActivity.class), 0);
        mBuilder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.app_title))
                .setTicker(context.getString(R.string.app_title))
                .setAutoCancel(true)
                .setContentText(message).setContentIntent(contentIntent);

        if (UserInfo.getInstance().isNotificationSound(context)) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
        } else mBuilder.setSound(null);
        Random r = new Random();
        int i1 = r.nextInt(1000) + 1000;
        mNotificationManager.notify(i1, mBuilder.build());
    }


}