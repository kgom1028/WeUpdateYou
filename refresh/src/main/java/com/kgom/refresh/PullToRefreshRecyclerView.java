package com.kgom.refresh;

/**
 * Created by KJS on 11/18/2016.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
/**
 *
 * Support RecyclerView
 *
 * @author Dean.Ding
 *
 */
public class PullToRefreshRecyclerView extends PullToRefreshBase<RecyclerView> {


    public PullToRefreshRecyclerView(Context context) {
        super(context);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerView recyclerView;
        recyclerView = new RecyclerView(context, attrs);
        recyclerView.setId(R.id.recyclerview);
        return recyclerView;
    }

    @Override
    protected boolean isReadyForPullStart() {
        if (mRefreshableView.getChildCount() <= 0)
            return true;
        int firstVisiblePosition = mRefreshableView.getChildPosition(mRefreshableView.getChildAt(0));
        if (firstVisiblePosition == 0)
            return mRefreshableView.getChildAt(0).getTop() == mRefreshableView.getPaddingTop();
        else
            return false;

    }

    @Override
    protected boolean isReadyForPullEnd() {
        return false;
    }

    @Override
    protected int getMaxRefreshHeight(int remainItemCount) {
        if(mRefreshableView.getChildCount() <remainItemCount)
        {
            return 0;
        }else
        {
            int refreshHeight = 0;
            for (int i=0; i< remainItemCount; i++)
            {
                refreshHeight += mRefreshableView.getChildAt(i).getHeight();
            }
           return  mRefreshableView.getHeight() - refreshHeight;

        }

    }


}

