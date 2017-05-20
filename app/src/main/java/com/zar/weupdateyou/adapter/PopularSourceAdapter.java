package com.zar.weupdateyou.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kgom.wuy.R;
import com.zar.weupdateyou.model.PopularSourceItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KJS on 11/21/2016.
 */
public class PopularSourceAdapter extends RecyclerView.Adapter<PopularSourceAdapter.CustomViewHolder> {
    private List<PopularSourceItem> mItems;
    private Context mContext;
    private ItemListener mListener;

    public interface ItemListener {
        public void onItemClick(View v, int position);
    }

    public PopularSourceAdapter(Context context, ArrayList<PopularSourceItem> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_source_item, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        viewHolder.txtTitle = (TextView) view.findViewById(R.id.source_title);
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf");
        viewHolder.txtTitle.setTypeface(font);
        return viewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder viewHolder, final int position) {
        PopularSourceItem model = mItems.get(position);
        viewHolder.txtTitle.setText(model.mTitle);
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


    public void setItems(ArrayList<PopularSourceItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }



    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtTitle;

        public CustomViewHolder(View view) {
            super(view);
            txtTitle = (TextView) view.findViewById(R.id.source_title);
        }
    }
}