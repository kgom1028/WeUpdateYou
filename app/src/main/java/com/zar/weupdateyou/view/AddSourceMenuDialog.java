package com.zar.weupdateyou.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kgom.wuy.R;

import java.util.ArrayList;

/**
 * Created by KJS on 11/22/2016.
 */
public class AddSourceMenuDialog extends Dialog {
    private Context mContext;
    AddSourceMenuListener _listener;
    ListView mListView;
    ArrayList<String> mMenuItems;



    public interface AddSourceMenuListener{
        public void onMenuItemClick(String menuItem);

    }
    public void setListener(AddSourceMenuListener listener)
    {
        _listener = listener;
    }
    public AddSourceMenuDialog(Context mContext)
    {
        super(mContext);
        this.mContext = mContext;
        mMenuItems = new ArrayList<String>();


        String[] strList = mContext.getResources().getStringArray(R.array.add_source_menu);
        for(int i=0; i<strList.length; i++) {
            mMenuItems.add(strList[i]);
        }

    }

    public void showDialog(){

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setContentView(R.layout.dialog_add_source);
        mListView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1);
        adapter.addAll(mMenuItems);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _listener.onMenuItemClick(mMenuItems.get(position));
                dismiss();
            }
        });
        show();

    }
}
