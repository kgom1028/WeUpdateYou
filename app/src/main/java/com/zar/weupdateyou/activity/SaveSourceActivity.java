package com.zar.weupdateyou.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.kgom.wuy.R;
import com.zar.weupdateyou.adapter.TopListSpinnerAdapter;
import com.zar.weupdateyou.doc.ENUM;
import com.zar.weupdateyou.doc.UserInfo;
import com.zar.weupdateyou.model.SettingModel;
import com.zar.weupdateyou.service.HttpServiceManager;
import com.zar.weupdateyou.service.ServiceListener;
import com.zar.weupdateyou.view.TopListDialog;

import java.util.ArrayList;

/**
 * Created by KJS on 11/21/2016.
 */
public class SaveSourceActivity extends BaseActivity implements ServiceListener{
    HttpServiceManager serviceManager;
    TextView wordsLabel;
    TextView topLabel;
    TextView titleText;
    TextView topText;
    TextView saveText;
    ViewGroup saveButton;
    EditText wordsEdit;
    ProgressDialog progressDialog;
    ArrayList<String> mTopString;
    ArrayList<Integer> mTopValues;
    SettingModel model;
    TopListDialog listDialog;
    Spinner topListSpinner;
    String mType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_source);

        model = new SettingModel();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            model.mTitle = extras.containsKey("title")? extras.getString("title"):"";
            model.mFeedId = extras.containsKey("feedid")?extras.getString("feedid"):"";
            model.mTop   = extras.containsKey("top")?extras.getString("top"):"-1";
            model.mSource = extras.containsKey("source")?extras.getString("source"):"";
            model.mWordsArray = extras.containsKey("words_array")?extras.getStringArrayList("words_array"): new ArrayList<String>();
            mType =extras.containsKey("type")?extras.getString("type"):"";
        }

        setToolBar();
        attachHandle();
        initialize();



    }
    private void attachHandle()
    {
        Typeface RobertRegular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        Typeface RobertMedium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");

        titleText = (TextView) findViewById(R.id.source_title);
        titleText.setTypeface(RobertRegular);
        topText = (TextView) findViewById(R.id.top_text);
        topText.setTypeface(RobertRegular);
        wordsEdit = (EditText) findViewById(R.id.words_edit);
        wordsEdit.setTypeface(RobertRegular);
        topListSpinner = (Spinner)findViewById(R.id.top_text_spinner);
        saveButton = (ViewGroup) findViewById(R.id.saveBtn);
        saveText = (TextView) findViewById(R.id.saveText);
        saveText.setTypeface(RobertMedium);

        topLabel = (TextView) findViewById(R.id.top_label);
        topLabel.setTypeface(RobertRegular);
        wordsLabel = (TextView) findViewById(R.id.words_label);
        wordsLabel.setTypeface(RobertRegular);



    }

    private void initialize() {
        titleText.setText(model.mTitle);
        serviceManager = HttpServiceManager.getInstance();
        topText.setText(getTopStringFromValue(Integer.parseInt(model.mTop) ));
        if(!(mType.equals("web")|| mType.equals("youtube")))
        {
            topText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTopListDialog();
                }
            });
        }else {
            wordsEdit.requestFocus();

        }
        //if(mType.equals("edit")){
            saveButton.setVisibility(View.VISIBLE);
        //}else
        //   saveButton.setVisibility(View.GONE);

        wordsEdit.setText(TextUtils.join(",", model.mWordsArray));

        mTopString = new ArrayList<String>();
        mTopValues = new ArrayList<Integer>();

        mTopString.clear();
        mTopValues.clear();
        String[] strList = getResources().getStringArray(R.array.top_array);
        for(int i=0; i<strList.length; i++) {
            mTopString.add(strList[i]);
            mTopValues.add(i-1);
        }
        //topListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.color_white), PorterDuff.Mode.SRC_ATOP);
        TopListSpinnerAdapter adapter = new TopListSpinnerAdapter(this, R.layout.spinner_item, mTopString);
        //adapter.addAll(mTopString);
        topListSpinner.setAdapter(adapter);
        topListSpinner.setSelection(Integer.parseInt(model.mTop)+1);

    }
    private void openTopListDialog()
    {
        final TopListDialog dialogView = new TopListDialog(this);
        dialogView.showDialog();
        dialogView.setListener(new TopListDialog.TopListDialogListener() {
            @Override
            public void onTopListItemClick(String topString, String topValue) {
                model.mTop = topValue;
                topText.setText(topString);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mType.equals("edit")) {
            getMenuInflater().inflate(R.menu.save_source_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case R.id.action_remove:
                doRemove();
                break;
        }
        return true;
    }



    void updateModel()
    {

        model.mTitle = titleText.getText().toString();
        model.mTop   = String.valueOf(mTopValues.get(topListSpinner.getSelectedItemPosition()));
        String wordText =wordsEdit.getText().toString();
        String[] words =  wordText.split(",");
        if(words.length > 0)
        {
            model.mWordsArray.clear();
            for (String word : words)
                model.mWordsArray.add(word);
        }


    }

    protected void onPause()
    {
        super.onPause();
        UserInfo.getInstance().previousActivity = this.getClass().getSimpleName();
    }

    String getTopStringFromValue(int top)
    {
        String[]  strs = getResources().getStringArray(R.array.top_array);
        if(top+2 > strs.length)
            return "";
        return strs[top+1];
    }
    private void saveSource()
    {
        updateModel();
        if(mType.equals("web") || mType.equals("youtube"))
        {
            if(model.mWordsArray.size() != 1)
            {
                showToast(this,getString(R.string.input_one_word));
            }
        }
        if(model.mFeedId.equals(""))
            serviceManager.serviceAddUserFeed(UserInfo.getInstance().mAccount, model.mSource, model.mWordsArray, model.mTop, this);
        else
            serviceManager.serviceEditUserFeedValues(UserInfo.getInstance().mAccount, model.mFeedId, model.mWordsArray, model.mTop, this);
        progressDialog = ProgressDialog.show(this,"",getString(R.string.loading));
        UserInfo.getInstance().settingChanged = true;
    }
    private void setToolBar() {
        Toolbar tb = (Toolbar) findViewById(R.id.Toolbar);
        TextView mTitle = (TextView) tb.findViewById(R.id.toolbar_title);
        Typeface Holla = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mTitle.setTypeface(Holla);
       tb.setTitle("");
        if(mType.equals("edit"))
            mTitle.setText(getString(R.string.edit_source));
        else if(mType.equals("remove"))
            mTitle.setText(getString(R.string.remove_source));
        else
            mTitle.setText(getString(R.string.add_source));
        tb.setNavigationIcon(R.drawable.back_ic);
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void onClick(View v){

        switch (v.getId()){
            case R.id.saveBtn:
                saveSource();
                break;
        }
    }
    void doRemove()
    {
        if(model == null)
            return;
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.remove_confirm, model.mTitle))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        serviceManager.serviceRemoveUserFeed(UserInfo.getInstance().mAccount,  model.mFeedId, SaveSourceActivity.this );
                        UserInfo.getInstance().settingChanged = true;
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
    @Override
    public void OnSuccess(Object response, ENUM.SERVICE_TYPE type) {
        if(type == ENUM.SERVICE_TYPE.REMOVE_USER_FEED)
        {
            showToast(this, getString(R.string.toast_source_removed));
            finish();
        }else {
            showToast(this, getString(R.string.toast_source_edit));
            if (!mType.equals("edit") && !mType.equals("remove")) {
                UserInfo.getInstance().setSourceSettingCount(this, UserInfo.getInstance().getSourceSettingCount(this) + 1);
            }
            finish();
        }

    }

    @Override
    public void OnError(String ErrorMsg, ENUM.SERVICE_TYPE type) {
        showToast(this, "Invalid param");
    }

    @Override
    public void OnFaild(ENUM.SERVICE_TYPE type) {
        showToast(this, "Connection Error.");
    }

    @Override
    public void OnFinished(ENUM.SERVICE_TYPE type) {
       // if(progressDialog != null && progressDialog.isShowing())
       //     progressDialog.dismiss();
    }

}

