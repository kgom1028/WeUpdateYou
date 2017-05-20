package com.zar.weupdateyou.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kgom.wuy.R;
import com.zar.weupdateyou.model.SettingModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KJS on 11/22/2016.
 */
public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.CustomViewHolder> {
    private List<SettingModel> mItems;
    private Context mContext;
    private ItemListener mListener;

    public interface ItemListener {
        public void onItemClick(View v, int position);
    }

    public SettingAdapter(Context context, ArrayList<SettingModel> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_setting_item, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        viewHolder.txtTitle = (TextView) view.findViewById(R.id.source_title);
        viewHolder.txtWords = (TextView) view.findViewById(R.id.words);
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf");
        viewHolder.txtTitle.setTypeface(font);
        viewHolder.txtWords.setTypeface(font);

        return viewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder viewHolder, final int position) {
        SettingModel model = mItems.get(position);
        viewHolder.txtTitle.setText(model.mTitle);
        if(model.mWordsArray.size() == 0) {
            viewHolder.txtWords.setVisibility(View.VISIBLE);
            viewHolder.txtWords.setText("");
        }
        else
        {
            viewHolder.txtWords.setVisibility(View.VISIBLE);
            String wordsLabel = TextUtils.join(",", model.mWordsArray);
            viewHolder.txtWords.setText(wordsLabel);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null)
                    mListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mItems != null) {
            return mItems.size();
        }
        return 0;
    }

    public void setListener(ItemListener listener) {
        mListener = listener;
    }


    public void setItems(ArrayList<SettingModel> items) {
        mItems = items;
        notifyDataSetChanged();
    }



    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtTitle;
        protected TextView txtWords;

        public CustomViewHolder(View view) {
            super(view);
            txtTitle = (TextView) view.findViewById(R.id.source_title);
            txtWords = (TextView) view.findViewById(R.id.words);
        }
    }
}