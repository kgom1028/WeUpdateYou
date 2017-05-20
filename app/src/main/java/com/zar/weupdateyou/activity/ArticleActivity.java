package com.zar.weupdateyou.activity;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kgom.wuy.R;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.ViewBinder;
import com.zar.weupdateyou.doc.UserInfo;
import com.zar.weupdateyou.model.NewsModel;
import com.zar.weupdateyou.service.HttpServiceManager;
import com.mopub.mobileads.MoPubView;

/**
 * Created by KJS on 11/16/2016.
 */
public class ArticleActivity extends BaseActivity{
    NewsModel model = null;
    int modelIndex =0;
    HttpServiceManager serviceManager;

    ImageView imgView;
    TextView txtTitle;
    TextView txtContent;
    TextView txtShares;
    ImageView imgHot;
    TextView txtTime;
    TextView txtTag;
    TextView txtSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       if(UserInfo.getInstance().newsModels != null)
       {
            modelIndex =getIntent().getIntExtra("NewsModelIndex",0);
            model = UserInfo.getInstance().newsModels.get(modelIndex);
       }

        if(model != null && !model.mImageLink.equals("") && !model.mIcoLink.equals("")){
            setContentView(R.layout.activity_article);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }else
        {
            setContentView(R.layout.activity_article_no_image);
        }

        setToolBar();
        attachHandle();
        initialize();


    }
    protected void onPause()
    {
        super.onPause();
        UserInfo.getInstance().previousActivity = this.getClass().getSimpleName();
    }
    protected void onDestroy()
    {
        super.onDestroy();

    }
    private void attachHandle()
    {
        imgView = (ImageView) findViewById(R.id.news_image);
        txtTitle = (TextView)findViewById(R.id.news_title);
        txtContent = (TextView) findViewById(R.id.news_content);
        imgHot = (ImageView) findViewById(R.id.hot_ic);
        txtShares = (TextView) findViewById(R.id.share_txt);
        txtTime = (TextView)findViewById(R.id.time_txt);
        txtTag = (TextView) findViewById(R.id.tag_txt);
        txtSource = (TextView) findViewById(R.id.source_txt);

        Typeface font = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        txtTitle.setTypeface(font);
        txtContent.setTypeface(font);
        txtTime.setTypeface(font);
        txtShares.setTypeface(font);
        txtTag.setTypeface(font);
        txtSource.setTypeface(font);
    }

    private void initialize() {
        serviceManager  = HttpServiceManager.getInstance();
        // if(model != null)
        if(model== null)
            return;
        Glide.with(this)
                .load(model.mImageLink)
                .error(R.drawable.placeholder)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgView);


        txtTitle.setText(model.mTitle);
        txtContent.setText(model.mDescription);
        txtShares.setText(model.mShares + " Shares");


        txtTime.setText(model.mTimeArray.getTimeString());
        imgHot.setVisibility(model.mYourTopStories.equals("true") ? View.VISIBLE : View.GONE);
        txtTag.setText(model.mTag);
        txtSource.setText(model.mFound);

        if (model.mTag.isEmpty())
            txtTag.setVisibility(View.INVISIBLE);
        else
            txtTag.setVisibility(View.VISIBLE);

        if(model.mFound.isEmpty())
            txtSource.setVisibility(View.INVISIBLE);
        else
            txtSource.setVisibility(View.VISIBLE);

        initAds();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case R.id.action_more:
                View menuItemView = findViewById(R.id.action_more);
                onMenuClick(menuItemView);
                break;
            case R.id.action_share:
                shareTextUrl();
                break;
        }
        return true;
    }

    public void onMenuClick(final View v)
    {
        // We need to post a Runnable to show the popup to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        v.post(new Runnable() {
            @Override
            public void run() {
                showPopupMenu(v);
            }
        });
    }
    // BEGIN_INCLUDE(show_popup)
    private void showPopupMenu(View view) {
        // Retrieve the clicked item from view's tag

        // Create a PopupMenu, giving it the clicked view for an anchor
        PopupMenu popup = new PopupMenu(this, view);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.article_popup_menu, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_copy_clipboard:
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(ArticleActivity.this.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("URL", model.mLink);
                        showToast(ArticleActivity.this,getString(R.string.copy_url_toast));
                        clipboard.setPrimaryClip(clip);
                        return true;
                    case R.id.menu_open_browser: {
                        try {
                            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.mLink));
                            startActivity(myIntent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(ArticleActivity.this, "No application can handle this request."
                                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        return true;
                    }
                    default:
                        break;

                }
                return false;
            }
        });

        // Finally show the PopupMenu
        popup.show();
    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Share WeUpdateYou");
        share.putExtra(Intent.EXTRA_TEXT,getString(R.string.article_share_text,model.mTitle, model.mLink));

        startActivity(Intent.createChooser(share, "Share"));
    }

    private void setToolBar() {

        Toolbar tb = (Toolbar) findViewById(R.id.Toolbar);
        TextView mTitle = (TextView) tb.findViewById(R.id.toolbar_title);
        Typeface Holla = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mTitle.setTypeface(Holla);
        tb.setTitle("");
        tb.setNavigationIcon(R.drawable.back_ic);
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(model != null && !model.mImageLink.equals("") && !model.mIcoLink.equals("")) {
            mTitle.setText("");
            tb.getBackground().setAlpha(0);
        }else {

            mTitle.setText(getString(R.string.article));
        }

    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.read_more_btn:
            {
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("NewsModelIndex",modelIndex);
                startActivity(intent);
                finish();

            }
                break;
        }
    }

    private void initAds()
    {
        MoPubNative.MoPubNativeNetworkListener moPubNativeListener = new MoPubNative.MoPubNativeNetworkListener() {
            @Override
            public void onNativeLoad(NativeAd nativeAd) {
                LinearLayout adContainter = (LinearLayout) findViewById(R.id.ad_container);
                View adViewRender = nativeAd.createAdView(ArticleActivity.this,adContainter);
                nativeAd.renderAdView(adViewRender);
                nativeAd.prepare(adViewRender);
                adContainter.addView(adViewRender);
            }

            @Override
            public void onNativeFail(NativeErrorCode errorCode) {
                Log.d("LockUpMopub",errorCode+ " errorcode");
            }
        };

        MoPubNative mMoPubNative = new MoPubNative(this
                ,getResources().getString(R.string.unit_id),moPubNativeListener);

        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.facebook_ad_listview_item)
                .iconImageId(R.id.icon_desc)
                .titleId(R.id.nativeAdTitle)
                .textId(R.id.nativeAdBody)
                .callToActionId(R.id.nativeAdCallToAction)
                .build();

        MoPubStaticNativeAdRenderer adRenderer = new MoPubStaticNativeAdRenderer(viewBinder);

        mMoPubNative.registerAdRenderer(adRenderer);
        mMoPubNative.makeRequest();
    }



}
