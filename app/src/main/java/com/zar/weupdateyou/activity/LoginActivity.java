package com.zar.weupdateyou.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kgom.wuy.R;
import com.zar.weupdateyou.doc.Constants;
import com.zar.weupdateyou.doc.ENUM;
import com.zar.weupdateyou.doc.UserInfo;
import com.zar.weupdateyou.model.SettingModel;
import com.zar.weupdateyou.model.UserModel;
import com.zar.weupdateyou.service.HttpServiceManager;
import com.zar.weupdateyou.service.ServiceListener;

import java.util.List;

/**
 * Created by KJS on 11/14/2016.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, ServiceListener {
    HttpServiceManager serviceManager;
    TextView usernameEdit;
    TextView passwordEdit;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        attachHandle();
        initialize();

    }

    private void attachHandle() {
        usernameEdit = (EditText) findViewById(R.id.username_edit);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        TextView mTitleFirst = (TextView) findViewById(R.id.toolbar_title_first);
        Typeface Holla = Typeface.createFromAsset(getAssets(), "TypographerRotunda.ttf");

        mTitleFirst.setTypeface(Holla);

        TextView mTitleLeft = (TextView) findViewById(R.id.toolbar_title_left);
        Typeface Robert = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mTitleLeft.setTypeface(Robert);
    }

    private void initialize() {
        serviceManager = HttpServiceManager.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_in_btn:
                login();
                break;
            case R.id.create_account_btn:
                createUser();
                break;
        }
    }

    protected void onPause() {
        super.onPause();
        UserInfo.getInstance().previousActivity = this.getClass().getSimpleName();
    }

    private void createUser() {

        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if (!username.isEmpty() && !password.isEmpty()) {
            UserModel userModel = new UserModel();
            userModel.mUserName = username;
            userModel.mPassword = password;

            serviceManager.serviceCreateUser(userModel, this);
            progressDialog = ProgressDialog.show(this, "", getString(R.string.loading));

        } else
            showToast(this, getString(R.string.input_all_fields));
    }

    private void login() {
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if (!username.isEmpty() && !password.isEmpty()) {
            serviceManager.serviceGetSecret(username, password, this);
            progressDialog = ProgressDialog.show(this, "", getString(R.string.loading));
        } else
            showToast(this, getString(R.string.input_all_fields));
    }

    @Override
    public void OnSuccess(Object response, ENUM.SERVICE_TYPE type) {
        if (type == ENUM.SERVICE_TYPE.CREATE_USER || type == ENUM.SERVICE_TYPE.GET_SECRET) {
            UserInfo info = UserInfo.getInstance();
            info.mAccount = (UserModel) response;
            UserInfo.getInstance().setSecret(this, info.mAccount.mSecret);
            UserInfo.getInstance().setUserName(this, info.mAccount.mUserName);
            UserInfo.getInstance().setPassword(this, info.mAccount.mPassword);
            serviceManager.serviceGetSettings(info.mAccount, this);

        }

        if (type == ENUM.SERVICE_TYPE.GET_SETTINGS) {
            List<SettingModel> items = (List<SettingModel>) response;
            UserInfo.getInstance().setSourceSettingCount(this, items.size());
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();

        }
    }

    @Override
    public void OnError(String ErrorMsg, ENUM.SERVICE_TYPE type) {
        if (type == ENUM.SERVICE_TYPE.GET_SECRET) {
            if (ErrorMsg.equals(Constants.ERR_JSON_PARSING)) {
                showToast(this, getString(R.string.invalid_user));
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                return;
            }
        }
        if (type == ENUM.SERVICE_TYPE.CREATE_USER) {
            if (ErrorMsg.equals(Constants.ERR_JSON_PARSING)) {
                showToast(this, getString(R.string.exist_user));
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                return;
            }
        }
    }

    @Override
    public void OnFaild(ENUM.SERVICE_TYPE type) {
        showToast(this, "Connection Fail");
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void OnFinished(ENUM.SERVICE_TYPE type) {

    }
}
