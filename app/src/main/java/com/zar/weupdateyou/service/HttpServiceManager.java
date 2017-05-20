package com.zar.weupdateyou.service;


import android.text.TextUtils;

import com.zar.weupdateyou.doc.Constants;
import com.zar.weupdateyou.doc.ENUM;
import com.zar.weupdateyou.doc.UserInfo;
import com.zar.weupdateyou.model.NewsModel;
import com.zar.weupdateyou.model.PopularSourceItem;
import com.zar.weupdateyou.model.SettingModel;
import com.zar.weupdateyou.model.UserModel;
import com.zar.weupdateyou.util.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by KJS on 11/15/2016.
 */
public class HttpServiceManager {
    static HttpServiceManager _instance;

    UserInfo userInfo;
    public static HttpServiceManager getInstance() {
        if (_instance == null)
            _instance = new HttpServiceManager();
        return _instance;
    }

    public HttpServiceManager()
    {
        userInfo = UserInfo.getInstance();
    }

    public void serviceGetSecret(final String username, final String password, final ServiceListener listener)
    {
        RequestParams params = new RequestParams();
        params.put("username",username);
        params.put("password",password);
        params.put("app","user");
        params.put("action","getsecret");
        HttpUtil.post(Constants.GET_SECRET, params, new AsyncHttpResponseHandler(){
            public void onFailure(Throwable paramThrowable) {
                if(listener != null)
                listener.OnFaild(ENUM.SERVICE_TYPE.GET_SECRET);
            }
            public void onFinish() {
                if(listener != null)
                listener.OnFinished(ENUM.SERVICE_TYPE.GET_SECRET);
            }
            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject jsonObject = new JSONObject(paramString);
                    String secret = jsonObject.getString("secret");
                    UserModel userModel = new UserModel();
                    userModel.mUserName = username;
                    userModel.mPassword = password;
                    userModel.mSecret = secret;
                    if(listener != null)
                    listener.OnSuccess(userModel, ENUM.SERVICE_TYPE.GET_SECRET);
                } catch (Exception e) {
                    e.printStackTrace();
                    if(listener != null)
                    listener.OnError(Constants.ERR_JSON_PARSING, ENUM.SERVICE_TYPE.GET_SECRET);
                   // listener.OnError(paramString, ENUM.SERVICE_TYPE.GET_SECRET);
                }
            }

        });
    }

    public void serviceCreateUser(final UserModel userModel, final ServiceListener listener)
    {
        RequestParams params = new RequestParams();
        params.put("username",userModel.mUserName);
        params.put("password",userModel.mPassword);
        params.put("app","user");
        params.put("action","create");
        HttpUtil.post(Constants.CREATE_USER, params, new AsyncHttpResponseHandler(){
            public void onFailure(Throwable paramThrowable) {
                if(listener != null)
                listener.OnFaild(ENUM.SERVICE_TYPE.CREATE_USER);
            }
            public void onFinish() {
                if(listener != null)
                listener.OnFinished(ENUM.SERVICE_TYPE.CREATE_USER);
            }
            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject jsonObject = new JSONObject(paramString);
                    String user = jsonObject.getString("user");
                    String secret = jsonObject.getString("secret");
                    UserModel model= new UserModel();
                    model.mUserName = userModel.mUserName;
                    model.mPassword = userModel.mPassword;
                    model.mSecret = secret;
                    if(listener != null)
                    listener.OnSuccess(model, ENUM.SERVICE_TYPE.CREATE_USER);
                } catch (Exception e) {
                    e.printStackTrace();
                    if(listener != null)
                    listener.OnError(Constants.ERR_JSON_PARSING, ENUM.SERVICE_TYPE.CREATE_USER);
                }
            }

        });
    }

    public void serviceGetNews(final UserModel userModel, final ServiceListener listener)
    {
        if(userModel != null && userModel.mSecret != null && userModel.mPassword != null && userModel.mUserName != null) {
            RequestParams params = new RequestParams();
            params.put("username", userModel.mUserName);
            params.put("secret", userModel.mSecret);
            params.put("app", "newsfeed");
            params.put("action", "getnews");
            HttpUtil.post(Constants.GET_NEWS, params, new AsyncHttpResponseHandler() {
                public void onFailure(Throwable paramThrowable) {
                    if (listener != null)
                        listener.OnFaild(ENUM.SERVICE_TYPE.GET_NEWS);
                }

                public void onFinish() {
                    if (listener != null)
                        listener.OnFinished(ENUM.SERVICE_TYPE.GET_NEWS);
                }

                public void onSuccess(String paramString) {  //that is return when success..
                    try {
                        JSONObject jsonData = new JSONObject(paramString);

                    /*JSONArray newsArray = jsonObject.getJSONArray("mynewsfeed");
                    List<NewsModel> newsList = new ArrayList<NewsModel>();
                    for(int i=0; i<newsArray.length(); i++)
                    {
                        JSONObject newsObject = newsArray.getJSONObject(i);
                        NewsModel mModel = new NewsModel();
                        mModel.parse(newsObject);
                        newsList.add(mModel);
                    }*/
                        JSONObject jsonObject = jsonData.getJSONObject("mynewsfeed");
                        Iterator<?> keys = jsonObject.keys();
                        List<NewsModel> newsList = new ArrayList<NewsModel>();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            if (jsonObject.get(key) instanceof JSONObject) {
                                JSONObject newsObject = (JSONObject) jsonObject.get(key);
                                NewsModel mModel = new NewsModel();
                                mModel.parse(newsObject);
                                newsList.add(mModel);
                            }
                        }
                        if (listener != null)
                            listener.OnSuccess(newsList, ENUM.SERVICE_TYPE.GET_NEWS);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (listener != null)
                            listener.OnError(Constants.ERR_JSON_PARSING, ENUM.SERVICE_TYPE.GET_NEWS);
                    }
                }

            });
        }else
            {
                if (listener != null)
                listener.OnFaild(ENUM.SERVICE_TYPE.GET_NEWS);
            }
    }

    public void serviceSwipeNews(UserModel userModel, final boolean isRead, final String link, final ServiceListener listener)
    {
        //+"app=newsfeed&action=swipe"
        if(userModel != null && userModel.mSecret != null && userModel.mPassword != null && userModel.mUserName != null) {
            RequestParams params = new RequestParams();
            params.put("username", userModel.mUserName);
            params.put("secret", userModel.mSecret);
            params.put("data", link);
            params.put("app", "newsfeed");
            params.put("action", "swipe" + (isRead ? "read" : "unread"));
            HttpUtil.post(Constants.SWIPE_ACTION, params, new AsyncHttpResponseHandler() {
                public void onFailure(Throwable paramThrowable) {
                    if (listener != null)
                        listener.OnFaild(isRead ? ENUM.SERVICE_TYPE.READ : ENUM.SERVICE_TYPE.UNREAD);
                }

                public void onFinish() {
                    if (listener != null)
                        listener.OnFinished(isRead ? ENUM.SERVICE_TYPE.READ : ENUM.SERVICE_TYPE.UNREAD);
                }

                public void onSuccess(String paramString) {  //that is return when success..
                    if (listener != null)
                        listener.OnSuccess(link, isRead ? ENUM.SERVICE_TYPE.READ : ENUM.SERVICE_TYPE.UNREAD);

                }

            });
        }else
        {
            if (listener != null)
                listener.OnFaild(isRead ? ENUM.SERVICE_TYPE.READ : ENUM.SERVICE_TYPE.UNREAD);
        }

    }

    public void serviceRead(UserModel userModel, final String link, final ServiceListener listener)
    {
        if(userModel != null && userModel.mSecret != null && userModel.mPassword != null && userModel.mUserName != null) {
            RequestParams params = new RequestParams();
            params.put("username", userModel.mUserName);
            params.put("secret", userModel.mSecret);
            params.put("data", link);
            params.put("app", "newsfeed");
            params.put("action", "read");
            HttpUtil.post(Constants.READ_ACTION, params, new AsyncHttpResponseHandler() {
                public void onFailure(Throwable paramThrowable) {
                    if (listener != null)
                        listener.OnFaild(ENUM.SERVICE_TYPE.READ_ACTION);
                }

                public void onFinish() {
                    if (listener != null)
                        listener.OnFinished(ENUM.SERVICE_TYPE.READ_ACTION);
                }

                public void onSuccess(String paramString) {  //that is return when success..
                    if (listener != null)
                        listener.OnSuccess(link, ENUM.SERVICE_TYPE.READ_ACTION);
                }

            });
        }else
        {
            if (listener != null)
                listener.OnFaild(ENUM.SERVICE_TYPE.READ_ACTION);
        }
    }

    public void serviceGetSettings(UserModel userModel, final ServiceListener listener)
    {
        if(userModel != null && userModel.mSecret != null && userModel.mPassword != null && userModel.mUserName != null) {
                RequestParams params = new RequestParams();
                params.put("username", userModel.mUserName);
                params.put("secret", userModel.mSecret);
                params.put("app", "newsfeed");
                params.put("action", "getsettings");
                HttpUtil.post(Constants.GET_SETTINGS, params, new AsyncHttpResponseHandler() {
                    public void onFailure(Throwable paramThrowable) {
                        if (listener != null)
                            listener.OnFaild(ENUM.SERVICE_TYPE.GET_SETTINGS);
                    }

                    public void onFinish() {
                        if (listener != null)
                            listener.OnFinished(ENUM.SERVICE_TYPE.GET_SETTINGS);
                    }

                    public void onSuccess(String paramString) {  //that is return when success..
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(paramString);
                            JSONArray settingArray = jsonObject.getJSONArray("mynewsfeed");
                            List<SettingModel> settingModels = new ArrayList<SettingModel>();

                            for (int i = 0; i < settingArray.length(); i++) {
                                JSONObject settingObject = settingArray.getJSONObject(i);
                                SettingModel mModel = new SettingModel();
                                mModel.parse(settingObject);
                                settingModels.add(mModel);
                            }

                            if (listener != null)
                                listener.OnSuccess(settingModels, ENUM.SERVICE_TYPE.GET_SETTINGS);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.OnSuccess(new ArrayList<SettingModel>(), ENUM.SERVICE_TYPE.GET_SETTINGS);
                        }
                    }
                });

        }else
        {
            if (listener != null)
                listener.OnFaild(ENUM.SERVICE_TYPE.GET_SETTINGS);
        }
    }

    public void serviceGetPopularFeeds(UserModel userModel, final ServiceListener listener)
    {
        if(userModel != null && userModel.mSecret != null && userModel.mPassword != null && userModel.mUserName != null)
            {
                RequestParams params = new RequestParams();
                params.put("username", userModel.mUserName);
                params.put("secret", userModel.mSecret);
                params.put("app", "newsfeed");
                params.put("action", "getpopularfeeds");
                HttpUtil.post(Constants.GET_POPULAR_FEEDS, params, new AsyncHttpResponseHandler() {
                    public void onFailure(Throwable paramThrowable) {
                        if (listener != null)
                            listener.OnFaild(ENUM.SERVICE_TYPE.GET_POPULAR_FEEDS);
                    }

                    public void onFinish() {
                        if (listener != null)
                            listener.OnFinished(ENUM.SERVICE_TYPE.GET_POPULAR_FEEDS);
                    }

                    public void onSuccess(String paramString) {  //that is return when success..
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(paramString);
                            JSONArray objArray = jsonObject.getJSONArray("popularfeeds");
                            List<PopularSourceItem> models = new ArrayList<PopularSourceItem>();
                            for (int i = 0; i < objArray.length(); i++) {
                                JSONObject obj = objArray.getJSONObject(i);
                                PopularSourceItem mModel = new PopularSourceItem();
                                mModel.parse(obj);
                                models.add(mModel);
                            }

                            if (listener != null)
                                listener.OnSuccess(models, ENUM.SERVICE_TYPE.GET_POPULAR_FEEDS);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                if (listener != null)
                    listener.OnFaild(ENUM.SERVICE_TYPE.GET_POPULAR_FEEDS);
            }

    }


    public void serviceFindFeed(UserModel userModel, String key,  final ServiceListener listener)
    {
        if(userModel != null && userModel.mSecret != null && userModel.mPassword != null && userModel.mUserName != null) {
            RequestParams params = new RequestParams();
            params.put("username", userModel.mUserName);
            params.put("secret", userModel.mSecret);
            params.put("data", key);
            params.put("app", "newsfeed");
            params.put("action", "findfeed");
            HttpUtil.post(Constants.FIND_FEED, params, new AsyncHttpResponseHandler() {
                public void onFailure(Throwable paramThrowable) {
                    if (listener != null)
                        listener.OnFaild(ENUM.SERVICE_TYPE.FIND_FEED);
                }

                public void onFinish() {
                    if (listener != null)
                        listener.OnFinished(ENUM.SERVICE_TYPE.FIND_FEED);
                }

                public void onSuccess(String paramString) {  //that is return when success..
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(paramString);
                        JSONArray objArray = jsonObject.getJSONArray("foundfeeds");
                        List<PopularSourceItem> models = new ArrayList<PopularSourceItem>();
                        for (int i = 0; i < objArray.length(); i++) {
                            JSONObject obj = objArray.getJSONObject(i);
                            PopularSourceItem mModel = new PopularSourceItem();
                            mModel.parse(obj);
                            models.add(mModel);
                        }

                        if (listener != null)
                            listener.OnSuccess(models, ENUM.SERVICE_TYPE.FIND_FEED);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else
        {
            if (listener != null)
                listener.OnFaild(ENUM.SERVICE_TYPE.FIND_FEED);
        }
    }

    public void serviceAddUserFeed(UserModel userModel, String source, List<String> words, String top,  final ServiceListener listener)
    {
        if(userModel != null && userModel.mSecret != null && userModel.mPassword != null && userModel.mUserName != null) {
            if (source == null || words == null || top == null)
                return;
            if (source.equals("")) {
                listener.OnFaild(ENUM.SERVICE_TYPE.ADD_USER_FEED);
                return;
            }
            String wordsParam = TextUtils.join("||", words);

            String data = String.format("%s;%s;%s", source, wordsParam, top);
            RequestParams params = new RequestParams();
            params.put("username", userModel.mUserName);
            params.put("secret", userModel.mSecret);
            params.put("data", data);
            params.put("app", "newsfeed");
            params.put("action", "adduserfeed");

            HttpUtil.post(Constants.ADD_USER_FEED, params, new AsyncHttpResponseHandler() {
                public void onFailure(Throwable paramThrowable) {
                    if (listener != null)
                        listener.OnFaild(ENUM.SERVICE_TYPE.ADD_USER_FEED);
                }

                public void onFinish() {
                    if (listener != null)
                        listener.OnFinished(ENUM.SERVICE_TYPE.ADD_USER_FEED);
                }

                public void onSuccess(String paramString) {  //that is return when success..
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(paramString);
                        String result = jsonObject.has("addfeed") ? jsonObject.getString("addfeed") : "";
                        if (result.equals("faild")) {
                            if (listener != null)
                                listener.OnError(result, ENUM.SERVICE_TYPE.ADD_USER_FEED);
                        } else if (listener != null)
                            listener.OnSuccess(result, ENUM.SERVICE_TYPE.ADD_USER_FEED);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else
        {
            if (listener != null)
                listener.OnFaild(ENUM.SERVICE_TYPE.ADD_USER_FEED);
        }
    }

    public void serviceEditUserFeedValues(UserModel userModel, String feedid, List<String> words, String top,  final ServiceListener listener)
    {
        if(userModel != null && userModel.mSecret != null && userModel.mPassword != null && userModel.mUserName != null) {

            if (feedid == null || words == null || top == null)
                return;

            String wordsParam = TextUtils.join("||", words);
            String data = String.format("%s;%s;%s", feedid, wordsParam, top);
            RequestParams params = new RequestParams();
            params.put("username", userModel.mUserName);
            params.put("secret", userModel.mSecret);
            params.put("data", data);
            params.put("app", "newsfeed");
            params.put("action", "edituserfeedvalues");

            HttpUtil.post(Constants.Edit_USER_FEED_VALUES, params, new AsyncHttpResponseHandler() {
                public void onFailure(Throwable paramThrowable) {
                    if (listener != null)
                        listener.OnFaild(ENUM.SERVICE_TYPE.Edit_USER_FEED_VALUES);
                }

                public void onFinish() {
                    if (listener != null)
                        listener.OnFinished(ENUM.SERVICE_TYPE.Edit_USER_FEED_VALUES);
                }

                public void onSuccess(String paramString) {  //that is return when success..
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(paramString);
                        String result = jsonObject.has("editfeed") ? jsonObject.getString("editfeed") : "";
                        if (result.equals("failed")) {
                            if (listener != null)
                                listener.OnError(result, ENUM.SERVICE_TYPE.Edit_USER_FEED_VALUES);
                        } else if (listener != null)
                            listener.OnSuccess(result, ENUM.SERVICE_TYPE.Edit_USER_FEED_VALUES);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else
        {
            if (listener != null)
                listener.OnFaild(ENUM.SERVICE_TYPE.Edit_USER_FEED_VALUES);
        }
    }

    public void serviceRemoveUserFeed(UserModel userModel, String feedId,  final ServiceListener listener) {
        if (userModel != null && userModel.mSecret != null && userModel.mPassword != null && userModel.mUserName != null) {
            RequestParams params = new RequestParams();
            params.put("username", userModel.mUserName);
            params.put("secret", userModel.mSecret);
            params.put("data", feedId);
            params.put("app", "newsfeed");
            params.put("action", "removeuserfeed");

            HttpUtil.post(Constants.REMOVE_USER_FEED, params, new AsyncHttpResponseHandler() {
                public void onFailure(Throwable paramThrowable) {
                    if (listener != null)
                        listener.OnFaild(ENUM.SERVICE_TYPE.REMOVE_USER_FEED);
                }

                public void onFinish() {
                    if (listener != null)
                        listener.OnFinished(ENUM.SERVICE_TYPE.REMOVE_USER_FEED);
                }

                public void onSuccess(String paramString) {  //that is return when success..
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(paramString);
                        String result = jsonObject.has("removefeed") ? jsonObject.getString("removefeed") : "";
                        if (result.equals("failed")) {
                            if (listener != null)
                                listener.OnError(result, ENUM.SERVICE_TYPE.REMOVE_USER_FEED);
                        } else if (listener != null)
                            listener.OnSuccess(result, ENUM.SERVICE_TYPE.REMOVE_USER_FEED);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            if (listener != null)
                listener.OnFaild(ENUM.SERVICE_TYPE.REMOVE_USER_FEED);
        }
    }
}
