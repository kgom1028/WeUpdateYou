package com.zar.weupdateyou.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by KJS on 11/21/2016.
 */
public class SettingModel {
    public String mFeedId;
    public String mSource;
    public String mTitle;
    public String mTop;
    public ArrayList<String> mWordsArray;

    public SettingModel()
    {
        mWordsArray = new ArrayList<String>();
        mFeedId ="";
        mSource = "";
        mTitle = "";
        mTop = "-1";

    }
    public void parse(JSONObject jsonObject) throws JSONException {
        mFeedId = jsonObject.has("feedid")?jsonObject.getString("feedid") : "-1";
        mSource = jsonObject.has("source") ? jsonObject.getString("source") : "";
        mTitle = jsonObject.has("title") ?  jsonObject.getString("title") :"";
        mTop = jsonObject.has("top") ?  jsonObject.getString("top") :"-1";
        if(jsonObject.has("words_array"))
        {
            mWordsArray.clear();
            JSONArray jsonArray = jsonObject.getJSONArray("words_array");
            for(int i=0; i<jsonArray.length(); i++)
            {
               String word = jsonArray.getString(i);

                mWordsArray.add(word);
            }
        }



    }
}
