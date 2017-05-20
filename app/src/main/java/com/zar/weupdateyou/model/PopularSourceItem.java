package com.zar.weupdateyou.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KJS on 11/21/2016.
 */
public class PopularSourceItem {
    public String mTitle;  //title
    public String mSource; //source

    public void parse(JSONObject jsonObject) throws JSONException {
        mSource = jsonObject.has("source") ? jsonObject.getString("source") : "";
        mTitle = jsonObject.has("title") ?  jsonObject.getString("title") :"";
    }
}
