package com.zar.weupdateyou.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kgom.wuy.R;
import com.zar.weupdateyou.adapter.SettingAdapter;
import com.zar.weupdateyou.doc.ENUM;
import com.zar.weupdateyou.doc.UserInfo;
import com.zar.weupdateyou.model.NewsItem;
import com.zar.weupdateyou.model.SettingModel;
import com.zar.weupdateyou.service.HttpServiceManager;
import com.zar.weupdateyou.service.ServiceListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by KJS on 11/22/2016.
 */
public class SettingListActivity extends BaseActivity implements ServiceListener, SettingAdapter.ItemListener{
    HttpServiceManager serviceManager;
    RecyclerView settingList;
    SettingAdapter settingAdapter;
    ProgressDialog progressDialog;
    private ArrayList<SettingModel> settingItems;
    String mType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_list);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mType =extras.containsKey("type")?extras.getString("type"):"";
        }
        setToolBar();
        attachHandle();
        initialize();

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        loadData();
    }
    private void attachHandle()
    {
        settingList = (RecyclerView) findViewById(R.id.popularSourceList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        settingList.setLayoutManager(linearLayoutManager);
    }

    private void initialize() {
        settingItems = new ArrayList<SettingModel>();
        serviceManager  = HttpServiceManager.getInstance();
        settingAdapter = new SettingAdapter(this, settingItems);
        settingList.setAdapter(settingAdapter);
        settingAdapter.setListener(this);

    }

    public void search(String key)
    {
        serviceManager.serviceFindFeed(UserInfo.getInstance().mAccount,key, this);
        progressDialog = ProgressDialog.show(this,"",getString(R.string.loading));
    }
    private void loadData()
    {
        serviceManager.serviceGetSettings(UserInfo.getInstance().mAccount, this);
       // progressDialog = ProgressDialog.show(this,"",getString(R.string.loading));
    }
    private void setToolBar() {
        Toolbar tb = (Toolbar) findViewById(R.id.Toolbar);
        TextView mTitle = (TextView) tb.findViewById(R.id.toolbar_title);
        Typeface Holla = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mTitle.setTypeface(Holla);
        if(mType.equals("edit"))
            mTitle.setText(getString(R.string.edit_source));
        else if(mType.equals("delete"))
            mTitle.setText(getString(R.string.remove_source));
        else
            mTitle.setText(getString(R.string.manage_source));
        tb.setTitle("");
        tb.setNavigationIcon(R.drawable.back_ic);
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_add_source:
                View menuItemView = findViewById(R.id.action_add_source);
                onMenuClick(menuItemView);
                break;
        }
        return true;
    }

    public void onMenuClick(final View v) {
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
        popup.getMenuInflater().inflate(R.menu.add_source_menu, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.menu_add_news:
                        openPopularSource();
                        break;
                    case R.id.menu_add_web:
                        openWebAdd();
                        break;
                    case R.id.menu_add_youtube:
                        openYoutubeAdd();
                        break;
                }
                return false;
            }
        });

        // Finally show the PopupMenu
        popup.show();
    }


    private void openWebAdd() {
        Intent intent = new Intent(this, SaveSourceActivity.class);
        intent.putExtra("type", "web");
        intent.putExtra("source", "web");
        intent.putExtra("title", "Web");
        intent.putExtra("top", "0");
        startActivity(intent);
    }

    private void openYoutubeAdd() {
        Intent intent = new Intent(this, SaveSourceActivity.class);
        intent.putExtra("type", "youtube");
        intent.putExtra("source", "youtube");
        intent.putExtra("title", "Youtube");
        intent.putExtra("top", "0");
        startActivity(intent);
    }

    private void openPopularSource() {
        Intent intent = new Intent(this, PopularSourceActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_list_menu, menu);
        return true;
    }
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.read_more_btn:
            {
            }
            break;
        }
    }

    @Override
    public void OnSuccess(Object response, ENUM.SERVICE_TYPE type) {
        if(type == ENUM.SERVICE_TYPE.GET_SETTINGS)
        {
            settingItems.clear();
            List<SettingModel> items = (List<SettingModel>) response;
            for (SettingModel item : items)
            {
                settingItems.add(item);
            }

            Comparator<SettingModel> comparator = new Comparator<SettingModel>() {
                @Override
                public int compare(SettingModel lhs, SettingModel rhs) {

                        String l = lhs.mTitle;
                        String r= rhs.mTitle;
                       return  l.compareTo(r)< 0 ? -1 : (l.compareTo(r) == 0 ? 0 : 1);
                        //return l < r ? -1 : (l == r ? 0 : 1);
                    //return 0;
                }
            };
            Collections.sort(settingItems, comparator);
            UserInfo.getInstance().setSourceSettingCount(this,items.size());
            settingAdapter.setItems(settingItems);
        }
        if(type == ENUM.SERVICE_TYPE.REMOVE_USER_FEED)
        {
           // loadData();
           // showToast(this, getString(R.string.toast_source_removed));
            //finish();
        }
    }

    @Override
    public void OnError(String ErrorMsg, ENUM.SERVICE_TYPE type) {

    }

    @Override
    public void OnFaild(ENUM.SERVICE_TYPE type) {

    }

    @Override
    public void OnFinished(ENUM.SERVICE_TYPE type) {
//        if(progressDialog.isShowing())
//            progressDialog.dismiss();
    }

    @Override
    public void onItemClick(View v, int position) {
        SettingModel item =  settingItems.get(position);
        if(mType.equals("edit") || mType.equals("manage"))
            doEdit(position);
        else
            doRemove(position);
    }
    void doEdit(int position)
    {
        SettingModel  model =  settingItems.get(position);
        Intent intent= new Intent(this, SaveSourceActivity.class);
        intent.putExtra("feedid", model.mFeedId);
        intent.putExtra("title", model.mTitle);
        intent.putExtra("source", model.mSource);
        intent.putStringArrayListExtra("words_array", model.mWordsArray);
        intent.putExtra("top", model.mTop);
        intent.putExtra("type","edit");
        startActivity(intent);
    }

    protected void onPause()
    {
        super.onPause();
        UserInfo.getInstance().previousActivity = this.getClass().getSimpleName();
    }

    void doRemove(int position)
    {
        final SettingModel  model =  settingItems.get(position);
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.remove_confirm, model.mTitle))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        serviceManager.serviceRemoveUserFeed(UserInfo.getInstance().mAccount,  model.mFeedId, SettingListActivity.this );
                        UserInfo.getInstance().settingChanged = true;
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

}
