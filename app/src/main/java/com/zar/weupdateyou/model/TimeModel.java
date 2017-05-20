package com.zar.weupdateyou.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KJS on 11/15/2016.
 */
public class TimeModel {
   public String mDifference;
   public String mPeriod;
   public String mExtension;
   public String mLabel;
   public TimeModel(){
        mDifference = "0";
        mPeriod = "hour";
        mExtension = "";
        mLabel ="";

    }
   public void parse(JSONObject jsonObject) throws JSONException {
       mDifference = jsonObject.has("difference")? jsonObject.getString("difference") : "0";
       mPeriod = jsonObject.has("period")?jsonObject.getString("period") : "hour";
       mExtension = jsonObject.has("extention")?jsonObject.getString("extention") : "";
       mLabel = jsonObject.has("label")? jsonObject.getString("label"): "";
   }
    public String getTimeString()
    {
       return  mDifference + " " + (mPeriod.equals("hour") ? "Hour" : "Day") + mExtension + " Ago";
    }
    public long getTimeDiff()
    {
      return  Integer.parseInt(mDifference) * (mPeriod.equals("hour") ? 1000*60*60: 1000*60*60*24);
    }

}
