package com.zar.weupdateyou.activity;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kgom.wuy.R;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.ViewBinder;
import com.zar.weupdateyou.doc.Constants;
import com.zar.weupdateyou.doc.Globals;
import com.zar.weupdateyou.doc.UserInfo;
import com.zar.weupdateyou.model.NewsModel;
import com.zar.weupdateyou.view.FullscreenableChromeClient;
import com.zar.weupdateyou.view.ObservableWebView;

import java.io.ByteArrayInputStream;

/**
 * Created by KJS on 11/16/2016.
 */
public class WebViewActivity extends BaseActivity{
    NewsModel model = null;
    boolean mAdBlockEnabled = true;
    ObservableWebView webView;
    ProgressBar progressBar;
    FullscreenableChromeClient webChromeClient;
    Toolbar searchBar;
    TextView searchEdit;
    String searchQuery;
    MenuItem searchItem;
    ViewGroup moPubContainer;
    boolean adOpen = false;
    String mCurrentLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mCurrentLink = "";
        if(UserInfo.getInstance().newsModels != null)
        {
            int index =getIntent().getIntExtra("NewsModelIndex",0);
            model = UserInfo.getInstance().newsModels.get(index);
            mCurrentLink = model.mLink;
        }

        setToolBar();
        attachHandle();
        initialize();

    }
    protected void onDestroy()
    {
        super.onDestroy();

    }
    private void attachHandle()
    {
        webView =(ObservableWebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) this.findViewById(R.id.pro);
        moPubContainer = (ViewGroup) findViewById(R.id.adContainer);
    }

    private void initialize() {
        attachKeyboardListeners();
        initAds();

        webChromeClient = new FullscreenableChromeClient(this){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        };
        WebSettings set = webView.getSettings();
        set.setJavaScriptEnabled(true);
        set.setBuiltInZoomControls(false);

        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebChromeClient(webChromeClient);
        webView.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int l, int t) {
                if(adOpen == false && t >100) {
                    moPubContainer.setVisibility(View.VISIBLE);
                    Globals.expand(moPubContainer);
                    adOpen = true;
                }
                if(adOpen == true && t <30)
                {
                    moPubContainer.setVisibility(View.INVISIBLE);
                    Globals.collapse(moPubContainer);
                    adOpen = false;
                }

            }
        });


        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url)
            {
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                mCurrentLink = url;
            }


            @Override
            public WebResourceResponse shouldInterceptRequest (final WebView view,  WebResourceRequest request) {
                if (!mAdBlockEnabled) {
                    return super.shouldInterceptRequest(view, request);
                }
                Uri uri = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    uri = request.getUrl();
                }else
                    return  super.shouldInterceptRequest(view, request);
                String topdomains = uri.getHost();
               /* if(topdomains.length <2)
                    return super.shouldInterceptRequest(view, request);
                String topdomain = topdomains[topdomains.length - 2] + "." + topdomains[topdomains.length - 1];*/
                if(application.GetDB().checkDomain(topdomains))
                    return createEmptyResource();
                return  super.shouldInterceptRequest(view, request);

            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("mailto:")){
                    MailTo mt = MailTo.parse(url);
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{mt.getTo()});
                    i.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
                    i.putExtra(Intent.EXTRA_CC, mt.getCc());
                    i.putExtra(Intent.EXTRA_TEXT, mt.getBody());
                    WebViewActivity.this.startActivity(i);
                    view.reload();
                    return true;
                }
                if( URLUtil.isNetworkUrl(url) ) {
                    return false;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity( intent );
                return true;
            }});



        webView.loadUrl(model.mLink);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static WebResourceResponse createEmptyResource() {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
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
        popup.getMenuInflater().inflate(R.menu.webview_popup_menu, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_copy_clipboard:
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(WebViewActivity.this.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("URL", model.mLink);
                        showToast(WebViewActivity.this,getString(R.string.copy_url_toast));
                        clipboard.setPrimaryClip(clip);
                        return true;
                    case R.id.menu_open_browser: {
                        try {
                            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.mLink));
                            startActivity(myIntent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(WebViewActivity.this, "No application can handle this request."
                                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        return true;
                    }
                    case R.id.menu_refresh_page: {
                        webView.loadUrl(model.mLink);

                    }
                    return true;
                    case R.id.find_page: {
                        if(!model.mFound.equals(""))
                        {
                            searchEdit.setText(model.mFound);
                        }
                            searchBar.setVisibility(View.VISIBLE);

                    }
                        return true;

                    case R.id.google_translate:
                        webView.loadUrl(getTranslateUrl(model.mLink));
                        break;
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
        share.putExtra(Intent.EXTRA_TEXT,getString(R.string.web_share_text, mCurrentLink));

        startActivity(Intent.createChooser(share, "Share"));
    }

    protected void onPause()
    {
        super.onPause();
        UserInfo.getInstance().previousActivity = this.getClass().getSimpleName();
    }

    private void setToolBar() {
        Toolbar tb = (Toolbar) findViewById(R.id.Toolbar);
        TextView mTitleFirst = (TextView) tb.findViewById(R.id.toolbar_title_first);
        Typeface Holla = Typeface.createFromAsset(getAssets(), "TypographerRotunda.ttf");

        mTitleFirst.setTypeface(Holla);

        TextView mTitleLeft = (TextView) tb.findViewById(R.id.toolbar_title_left);
        Typeface Robert = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mTitleLeft.setTypeface(Robert);
        tb.setTitle(R.string.app_title);
        tb.setNavigationIcon(R.drawable.back_ic);
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
 //               onBackPressed();
                webView.loadUrl("about:blank");
                finish();
            }
        });

        searchBar = (Toolbar) findViewById(R.id.search_toolbar);
        searchBar.setNavigationIcon(R.drawable.back_ic);
        searchBar.inflateMenu(R.menu.searchbar_menu);
        searchBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        searchBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setVisibility(View.GONE);
            }
        });

        searchEdit = (EditText)searchBar.findViewById(R.id.myEditText);
        searchEdit.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable edit) {

                // you can call or do what you want with your EditText here
                searchQuery =  searchEdit.getText().toString();
                webView.findAllAsync(searchQuery);

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        searchBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.action_prev) {
                    webView.findNext(false);
                    return true;
                }
                if(item.getItemId() == R.id.action_next)
                {
                    webView.findNext(true);
                   return true;
                }
                return false;
            }
        });



    }

    String getTranslateUrl(String original)
    {
        String translateUrl = Constants.TRANSLATE_URL+original;
        return translateUrl;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        if(searchBar.getVisibility() == View.VISIBLE)
            searchBar.setVisibility(View.GONE);
        else
        {
            if (!webChromeClient.onBackPressed()) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    // Close app (presumably)
                    super.onBackPressed();
                    webView.loadUrl("about:blank");
                    finish();

                }
            }

        }

    }

    private void initAds()
    {
        MoPubNative.MoPubNativeNetworkListener moPubNativeListener = new MoPubNative.MoPubNativeNetworkListener() {
            @Override
            public void onNativeLoad(NativeAd nativeAd) {
                LinearLayout adContainter = (LinearLayout) findViewById(R.id.ad_container);
                View adViewRender = nativeAd.createAdView(WebViewActivity.this,adContainter);
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

    @Override
    protected void onShowKeyboard(int keyboardHeight) {
        // do things when keyboard is shown
        FrameLayout adContainter = (FrameLayout) findViewById(R.id.adContainer);
       adContainter.setVisibility(View.GONE);
    }

    @Override
    protected void onHideKeyboard() {
        // do things when keyboard is hidden
       // bottomContainer.setVisibility(View.VISIBLE);
    }



}
