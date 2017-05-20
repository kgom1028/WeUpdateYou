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
public class TopListDialog extends Dialog {
    private Context mContext;
    TopListDialogListener _listener;
    ListView mListView;
    ArrayList<String> mTopString;
    ArrayList<Integer> mTopValues;


    public interface TopListDialogListener{
        public void onTopListItemClick(String topString, String topValue);

    }
    public void setListener(TopListDialogListener listener)
    {
        _listener = listener;
    }
    public TopListDialog(Context mContext)
    {
        super(mContext);
        this.mContext = mContext;
        mTopString = new ArrayList<String>();
        mTopValues = new ArrayList<Integer>();

        String[] strList = mContext.getResources().getStringArray(R.array.top_array);
        for(int i=0; i<strList.length; i++) {
            mTopString.add(strList[i]);
            mTopValues.add(i-1);
        }

    }

    public void showDialog(){

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setContentView(R.layout.dialog_top_list);
        mListView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1);
        adapter.addAll(mTopString);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _listener.onTopListItemClick(mTopString.get(position), mTopValues.get(position).toString());
                dismiss();
            }
        });
        show();

    }
}
