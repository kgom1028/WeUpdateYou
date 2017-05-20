package com.zar.weupdateyou.model;

import com.zar.weupdateyou.doc.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by KJS on 11/15/2016.
 */
public class NewsModel implements Serializable{
    public int mId;
    public String mGuid;
    public String mTitle;
    public String mLink;
    public String mDescription;
    public String mComments;
    public String mImageLink;
    public String mIcoLink;
    public String mSource;
    public String mTag;
    public String mShortLink;
    public String mShares;
    public String mRead;
    public TimeModel mTimeArray;
    public String mShowAll;
    public String mLocalImageLink;
    public String mYourTopStories;
    public String mFound;
    public String mPubDate;
    public String mCategory;


    public NewsModel()
    {
        mTimeArray = new TimeModel();
    }
    public void parse(JSONObject jsonObject) throws JSONException {
        mGuid = jsonObject.has("guid") ? jsonObject.getString("guid") : "";
        mTitle = jsonObject.has("title") ?  jsonObject.getString("title") :"";
        mLink = jsonObject.has("link") ?  jsonObject.getString("link") :"";
        mDescription = jsonObject.has("description") ? jsonObject.getString("description"):"";
        mComments = jsonObject.has("comments")? jsonObject.getString("comments") : "";
        mImageLink = jsonObject.has("imglink")? jsonObject.getString("imglink") : "";
        mIcoLink = jsonObject.has("icolink") ? jsonObject.getString("icolink") : "";
        mSource = jsonObject.has("source") ? jsonObject.getString("source") : "";
        mTag = jsonObject.has("nice_source")? jsonObject.getString("nice_source") : "";
        mShortLink = jsonObject.has("short_link")? jsonObject.getString("short_link") :"";
        mShares = jsonObject.has("shares")? jsonObject.getString("shares") : "0";
        mRead = jsonObject.has("read")? jsonObject.getString("read") : "0";
        if(jsonObject.has("time_array"))
        {
            mTimeArray.parse(jsonObject.getJSONObject("time_array"));
        }
         mShowAll = jsonObject.has("showall")?jsonObject.getString("showall"):"";
         mLocalImageLink = jsonObject.has("local_imglink")? Constants.SITE_URL + jsonObject.getString("local_imglink"):"";
         mYourTopStories = jsonObject.has("YourTopStories")?jsonObject.getString("YourTopStories"):"";
         mPubDate = jsonObject.has("pubDate")?jsonObject.getString("pubDate"):"";
         mCategory = jsonObject.has("category")?jsonObject.getString("category") : "";
         mFound = jsonObject.has("found")? jsonObject.getString("found") : "";
    }
}
