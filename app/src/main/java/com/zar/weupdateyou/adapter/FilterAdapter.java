package com.zar.weupdateyou.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kgom.wuy.R;
import com.zar.weupdateyou.model.FilterItem;

import java.util.List;

/**
 * Created by KJS on 11/24/2016.
 */
public class FilterAdapter extends BaseAdapter{
    private Context mContext;
    private List<FilterItem> mItems;
    public FilterAdapter(Context context, List<FilterItem> items){
        mContext = context;
        mItems = items;

    }

    public void setItems(List<FilterItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }
    public List<FilterItem> getItems()
    {
        return mItems;
    }
    @Override
    public int getCount() {
        if (mItems != null) {
            return mItems.size();
        }
        return 0;
    }
    @Override
    public Object getItem(int position) {
        if (mItems != null) {
            return mItems.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final FilterItem model = (FilterItem) getItem(position);
        int type = model.type;

        boolean shouldCreateView = false;
        if(view == null )
            shouldCreateView = true;
        else{
            ViewHolder vwHolder = (ViewHolder) view.getTag();

            if(vwHolder.type == type)
            {
                shouldCreateView =false;
            }else
                shouldCreateView = true;
        }

        if(model.type == 0)
        {
            if (shouldCreateView) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawmenu_item_layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.txtFilterName = (TextView) view.findViewById(R.id.filter_name);
                Typeface robotoLight = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
                viewHolder.txtFilterName.setTypeface(robotoLight);
                viewHolder.unreadCount = (TextView) view.findViewById(R.id.filter_unread_count);
                Typeface robotoMedium = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Medium.ttf");
                viewHolder.unreadCount.setTypeface(robotoMedium);
                viewHolder.unreadView = (ViewGroup) view.findViewById(R.id.unread_frame);
                viewHolder.type = type;
                view.setTag(viewHolder);
            }
            ViewHolder vwHolder = (ViewHolder)view.getTag();
            if(model.filterType == 1)
                vwHolder.txtFilterName.setText("@"+model.title);
            else
                vwHolder.txtFilterName.setText("#"+model.title);
            vwHolder.unreadCount.setText(String.valueOf(model.unreadCount) );
            if(model.unreadCount > 0)
                vwHolder.unreadView.setVisibility(View.VISIBLE);
            else
                vwHolder.unreadView.setVisibility(View.GONE);
        }else
        {
            if (shouldCreateView) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawmenu_section_layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.txtFilterName = (TextView) view.findViewById(R.id.section_name);
                Typeface robotoMedium = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Medium.ttf");
                viewHolder.txtFilterName.setTypeface(robotoMedium);
                viewHolder.type = type;
                view.setTag(viewHolder);
            }

            ViewHolder vwHolder = (ViewHolder)view.getTag();
            vwHolder.txtFilterName.setText(model.title);
        }
        return view;
    }

    class ViewHolder{
        TextView txtFilterName;
        TextView unreadCount;
        ViewGroup unreadView;
        int type;
    }
}
