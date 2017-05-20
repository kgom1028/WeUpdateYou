package com.zar.weupdateyou.adapter;

import android.content.Context;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kgom.wuy.R;
import com.zar.weupdateyou.model.NewsItem;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by KJS on 11/18/2016.
 */
public class NewsRecycleAdapter extends RecyclerView.Adapter<NewsRecycleAdapter.CustomViewHolder> {
    private List<NewsItem> mNews;
    private Map<String, Integer> mSections;
    private Context mContext;
    private NewsItemListener mListener;
    public NewsRecycleAdapter(Context context, ArrayList<NewsItem> items, Map<String, Integer> sections)
    {
        mContext = context;
        mNews  = items;
        mSections = sections;
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_news_item, parent, false);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            viewHolder.newsImage = (ImageView) view.findViewById(R.id.news_image);
            // viewHolder.newsImage = (RoundedImageView) view.findViewById(R.id.news_image);
            viewHolder.hot = (ImageView) view.findViewById(R.id.hot_ic);
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.news_text);
            viewHolder.txtDescribe = (TextView) view.findViewById(R.id.news_describe);
            viewHolder.txtShares = (TextView) view.findViewById(R.id.share_txt);
            viewHolder.txtTime = (TextView) view.findViewById(R.id.time_txt);
            viewHolder.txtTag = (TextView) view.findViewById(R.id.tag_txt);
            viewHolder.txtSource = (TextView) view.findViewById(R.id.source_txt);
            viewHolder.imageContainer = (LinearLayout) view.findViewById(R.id.imageContainer);

            viewHolder.type = viewType;
            viewHolder.imageLink = "";
            viewHolder.isDismissed = false;
            Typeface robotoRegular = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf");
            viewHolder.txtTitle.setTypeface(robotoRegular);
            viewHolder.txtDescribe.setTypeface(robotoRegular);
            viewHolder.txtShares.setTypeface(robotoRegular);
            viewHolder.txtSource.setTypeface(robotoRegular);
            viewHolder.txtTime.setTypeface(robotoRegular);
            viewHolder.txtTag.setTypeface(robotoRegular);
            view.setTag(viewHolder);
            viewHolder.newsImage.setImageBitmap(null);
            return viewHolder;
        }else if(viewType == 1)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_section_item, parent, false);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.section_txt);
            viewHolder.type = viewType;
            viewHolder.isDismissed = false;
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Medium.ttf");
            viewHolder.txtTitle.setTypeface(font);
            view.setTag(viewHolder);
            return viewHolder;
        }else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_add_source_item, parent, false);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            viewHolder.type = viewType;
            viewHolder.isDismissed = false;
            view.setTag(viewHolder);
            return viewHolder;
        }

    }
    @Override
    public int getItemViewType(int position) {
       /* if(mSections.values().contains(position))
            return 1;
        else if
            return 0;*/
        if(mNews != null)
            return mNews.get(position).type;
        else
            return 0;

    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public void onBindViewHolder(final CustomViewHolder viewHolder, final int position) {
        NewsItem model = mNews.get(position);
        int type = model.type;
        if(type == 0) {
            if (model.tag.isEmpty())
                viewHolder.txtTag.setVisibility(View.INVISIBLE);
            else {
                viewHolder.txtTag.setVisibility(View.VISIBLE);
                viewHolder.txtTag.setText(model.tag);
                viewHolder.txtTag.setTag(model.tag);
            }
            if (model.source.isEmpty())
                viewHolder.txtSource.setVisibility(View.INVISIBLE);
            else {
                viewHolder.txtSource.setVisibility(View.VISIBLE);
                viewHolder.txtSource.setText(model.source);
                viewHolder.txtSource.setTag(model.source);
            }

            if (model.read.equals("1")){
                viewHolder.txtTitle.setTextColor(mContext.getResources().getColor(R.color.unread_news_describe_color));
                viewHolder.hot.setAlpha(0.38f);
                viewHolder.newsImage.setAlpha(0.38f);
                viewHolder.txtTag.setAlpha(0.38f);
                viewHolder.txtSource.setAlpha(0.38f);
                viewHolder.txtShares.setTextColor(mContext.getResources().getColor(R.color.unread_share_color));
                viewHolder.txtDescribe.setTextColor(mContext.getResources().getColor(R.color.unread_news_describe_color));
                viewHolder.txtTime.setTextColor(mContext.getResources().getColor(R.color.unread_share_color));
                //viewHolder.unreadCover.setBackgroundResource(R.color.color_unread_cover);
            }
            else{
                viewHolder.txtTitle.setTextColor(mContext.getResources().getColor(R.color.news_title_color));
               // viewHolder.unreadCover.setBackgroundResource(R.color.transparent);
                viewHolder.hot.setAlpha(1.0f);
                viewHolder.newsImage.setAlpha(1.0f);
                viewHolder.txtTag.setAlpha(1.0f);
                viewHolder.txtSource.setAlpha(1.0f);
                viewHolder.txtShares.setTextColor(mContext.getResources().getColor(R.color.color_share_gray));
                viewHolder.txtDescribe.setTextColor(mContext.getResources().getColor(R.color.news_describe_color));
                viewHolder.txtTime.setTextColor(mContext.getResources().getColor(R.color.color_share_gray));
            }

            viewHolder.txtTitle.setText(model.title);
            viewHolder.txtDescribe.setText(model.description);
            viewHolder.txtShares.setText(model.shares + " Shares");

            viewHolder.txtTime.setText(model.timeText);
            viewHolder.hot.setVisibility(model.isHot ? View.VISIBLE : View.GONE);



            viewHolder.txtTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null)
                    mListener.onTagPressed(v.getTag().toString());
                }
            });
            viewHolder.txtSource.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null)
                        mListener.onSourcePressed(v.getTag().toString());
                }
            });
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null)
                        mListener.onItemClick(v);
                }
            });

           /* Glide.with(mContext)
                    .load(model.imgLink)
                    .fitCenter()
                    .crossFade()
                    .into(viewHolder.newsImage);
                    */
            if(model.imgLink.equals("")){
                viewHolder.imageContainer.setVisibility(View.GONE);
            }else
                viewHolder.imageContainer.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                      .load(model.imgLink)
                      .error(R.drawable.ic_launcher)
                      .diskCacheStrategy(DiskCacheStrategy.ALL)
                      /*.bitmapTransform(new RoundedCornersTransformation(mContext, 7, 0,
                              RoundedCornersTransformation.CornerType.ALL))*/

                      .into(viewHolder.newsImage);
              /*  Glide.with(mContext)
                        .load(model.imgLink)
                        .dontAnimate()
                        .fitCenter()
                        .into(viewHolder.newsImage);*/
            viewHolder.imageLink = model.imgLink;
        }else if(type == 1)
        {
            viewHolder.txtTitle.setText(model.title);
        }else if(type == 2)
        {

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null)
                        mListener.onItemClick(v);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (mNews != null) {
            return mNews.size();
        }
        return 0;
    }
    public void setListener(NewsItemListener listener)
    {
        mListener = listener;
    }

    public interface NewsItemListener{
        public void onTagPressed(String tagname);
        public void onSourcePressed(String sourcename);
        public void onItemClick(View v);
    }

    public void setItems(ArrayList<NewsItem> items, Map<String, Integer> sections)
    {
        mNews = items;
        mSections = sections;
        //notifyDataSetChanged();

    }

    public boolean moveItem(int pos, String sectionName)
    {
        if(mSections.containsKey(sectionName))
        {
            NewsItem item = mNews.get(pos);

            for (String key : mSections.keySet())
            {
                int sectionPos = mSections.get(key).intValue();
                if(sectionPos> pos )
                {
                    mSections.put(key,new Integer(sectionPos-1));
                }
            }
            mNews.remove(pos);
            notifyItemRemoved(pos);
            int targetPos = mSections.get(sectionName).intValue();
            mNews.add(targetPos+1,item);
            //notifyDataSetChanged();
            for (String key : mSections.keySet())
            {
                int sectionPos = mSections.get(key).intValue();
                if(sectionPos> targetPos )
                {
                    mSections.put(key,new Integer(sectionPos+1));
                }
            }

            notifyItemInserted(targetPos+1);

            return true;
        }
        return false;
    }

    public String getSection(int pos)
    {
        int distance = mNews.size();
        String result = "";
        for (String key : mSections.keySet())
        {
            int sectionPos = mSections.get(key).intValue();
            int diff = pos - sectionPos;
            if(diff > 0 && distance > diff)
            {
                distance = diff;
                result = key;
            }
        }
        return result;
    }
    public NewsItem getItem(int position)
    {
        if(mNews != null)
            return mNews.get(position);
        return null;
    }
    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected int type;
        protected String imageLink;
        protected ImageView newsImage;
        //RoundedImageView newsImage;
        protected TextView txtTitle;
        protected TextView txtDescribe;
        protected TextView txtTime;
        protected ImageView hot;
        protected TextView txtShares;
        protected TextView txtTag;
        protected TextView txtSource;
        protected LinearLayout imageContainer;
        protected View unreadCover;
        protected boolean isDismissed;

        public CustomViewHolder(View view) {
            super(view);
            newsImage = (ImageView) view.findViewById(R.id.news_image);
            // viewHolder.newsImage = (RoundedImageView) view.findViewById(R.id.news_image);
            hot = (ImageView) view.findViewById(R.id.hot_ic);
            txtTitle = (TextView) view.findViewById(R.id.news_text);
            txtDescribe = (TextView) view.findViewById(R.id.news_describe);
            txtShares = (TextView) view.findViewById(R.id.share_txt);
            txtTime = (TextView) view.findViewById(R.id.time_txt);
            txtTag = (TextView) view.findViewById(R.id.tag_txt);
            txtSource = (TextView) view.findViewById(R.id.source_txt);
            txtDescribe = (TextView) view.findViewById(R.id.news_describe);
            imageContainer = (LinearLayout) view.findViewById(R.id.imageContainer);

            imageLink = "";
            isDismissed = false;
        }
    }
}

