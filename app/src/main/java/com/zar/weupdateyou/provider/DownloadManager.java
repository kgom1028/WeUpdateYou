package com.zar.weupdateyou.provider;

import android.content.Context;
import android.os.AsyncTask;

import com.zar.weupdateyou.activity.BaseActivity;
import com.zar.weupdateyou.model.NewsModel;

import java.util.List;

/**
 * Created by KJS on 11/19/2016.
 */
public class DownloadManager {
    public static DownloadManager _instance = null;
    public static DownloadManager getInstance()
    {
        if(_instance == null)
            _instance = new DownloadManager();
        return _instance;
    }

    boolean IsDownloading = false;
    MyDB db;
    public boolean DataSync(Context context, List<NewsModel> newsList)
    {
        db = ((BaseActivity)context).application.GetDB();
        if(!IsDownloading)
        {
            NewsDownloadTask task = new NewsDownloadTask(context, newsList);
            task.execute();
            IsDownloading = true;
        }
        return false;
    }


    class NewsDownloadTask extends AsyncTask<Void, Void, Void> {

        private boolean mIsFailed = false;
        private List<NewsModel> newsList;
        private Context mContext;

        public NewsDownloadTask(Context context, List<NewsModel> newsList)
        {

            super();
            mContext= context;
            this.newsList = newsList;

        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //      mLoading = ProgressDialog.show(SearchSongsActivity.this, "", getString(R.string.download_label));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            db.removeAllNews();
            for(NewsModel model : newsList)
            {

                String thumbImageURL="", bigImageURL = "";
                boolean isFaild = false;
                if(!db.checkDownloadedURL(model.mLocalImageLink))
                {
                    thumbImageURL = Utils.getThumbImageFilePath(model.mLocalImageLink);
                    if(Utils.downloadFile_with_Get(model.mLocalImageLink, thumbImageURL))
                    {
                        db.insertDownloadedURL(model.mLocalImageLink, thumbImageURL);
                    }else
                        thumbImageURL = "";
                }else
                    thumbImageURL = db.getLocalUrl(model.mLocalImageLink);
                if(!db.checkDownloadedURL(model.mImageLink))
                {
                    bigImageURL = Utils.getBigImageFilePath(model.mLocalImageLink);
                    if(Utils.downloadFile_with_Get(model.mImageLink, bigImageURL))
                    {

                        db.insertDownloadedURL(model.mImageLink, bigImageURL);

                    }else
                       bigImageURL = "";
                }else
                    bigImageURL = db.getLocalUrl(model.mImageLink);

                if(!isFaild)
                {
                    model.mLocalImageLink = thumbImageURL;
                    model.mImageLink = bigImageURL;
                    db.insertNews(model);
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            //   if(mLoading != null && mLoading.isShowing())
            //       mLoading.dismiss();
            IsDownloading = false;

        }
    }

}
