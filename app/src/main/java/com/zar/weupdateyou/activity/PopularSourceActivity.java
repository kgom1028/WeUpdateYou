package com.zar.weupdateyou.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kgom.wuy.R;
import com.zar.weupdateyou.adapter.PopularSourceAdapter;
import com.zar.weupdateyou.doc.ENUM;
import com.zar.weupdateyou.doc.UserInfo;
import com.zar.weupdateyou.model.PopularSourceItem;
import com.zar.weupdateyou.service.HttpServiceManager;
import com.zar.weupdateyou.service.ServiceListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KJS on 11/21/2016.
 */
public class PopularSourceActivity extends BaseActivity implements ServiceListener, PopularSourceAdapter.ItemListener{
    HttpServiceManager serviceManager;
    RecyclerView popularSourceList;
    PopularSourceAdapter popularSourceAdapter;
    ProgressDialog progressDialog;
    TextView titleTxt;
    private ArrayList<PopularSourceItem> sourceItems;
    EditText searchBox;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_popular_source);
        setToolBar();
        attachHandle();
        initialize();

    }
    private void attachHandle()
    {
        popularSourceList = (RecyclerView) findViewById(R.id.popularSourceList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        popularSourceList.setLayoutManager(linearLayoutManager);
        searchBox = (EditText) findViewById(R.id.search_edit);
        searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        titleTxt = (TextView) findViewById(R.id.title_txt);
    }

    private void initialize() {
        sourceItems = new ArrayList<PopularSourceItem>();
        serviceManager  = HttpServiceManager.getInstance();
        popularSourceAdapter = new PopularSourceAdapter(this, sourceItems);
        popularSourceList.setAdapter(popularSourceAdapter);
        popularSourceAdapter.setListener(this);

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        search(searchBox.getText().toString());
                        InputMethodManager in = (InputMethodManager) getSystemService(PopularSourceActivity.INPUT_METHOD_SERVICE);

                        // NOTE: In the author's example, he uses an identifier
                        // called searchBar. If setting this code on your EditText
                        // then use v.getWindowToken() as a reference to your
                        // EditText is passed into this callback as a TextView

                        in.hideSoftInputFromWindow(searchBox
                                        .getApplicationWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        // Must return true here to consume event
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        loadData();
    }

    protected void onPause()
    {
        super.onPause();
        UserInfo.getInstance().previousActivity = this.getClass().getSimpleName();
    }

    public void search(String key)
    {
        serviceManager.serviceFindFeed(UserInfo.getInstance().mAccount,key, this);
        progressDialog = ProgressDialog.show(this,"",getString(R.string.loading));
    }
    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.popular_source_menu, menu);

        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                search(query);
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case R.id.action_more:
                View menuItemView = findViewById(R.id.action_more);
                break;
        }
        return true;
    }

    private void loadData()
    {
        serviceManager.serviceGetPopularFeeds(UserInfo.getInstance().mAccount, this);
        progressDialog = ProgressDialog.show(this,"",getString(R.string.loading));
    }
    private void setToolBar() {
        Toolbar tb = (Toolbar) findViewById(R.id.Toolbar);
        TextView mTitle = (TextView) tb.findViewById(R.id.toolbar_title);
        Typeface Holla = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mTitle.setText(getString(R.string.add_source));
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
        if(type == ENUM.SERVICE_TYPE.GET_POPULAR_FEEDS || type == ENUM.SERVICE_TYPE.FIND_FEED)
        {
            sourceItems.clear();
            List<PopularSourceItem> items = (List<PopularSourceItem>) response;
            for (PopularSourceItem item : items)
            {
                sourceItems.add(item);
            }
            popularSourceAdapter.setItems(sourceItems);
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
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onItemClick(View v, int position) {
        PopularSourceItem item =  sourceItems.get(position);

        openSaveActivity(item.mSource,item.mTitle);

    }

    private void openSaveActivity(String source, String title)
    {
        Intent intent = new Intent(this, SaveSourceActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("source", source);
        startActivity(intent);
    }
}
