package com.zar.weupdateyou.doc;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by KJS on 11/15/2016.
 */
public class Globals {

    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo thisiveNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return thisiveNetworkInfo != null && thisiveNetworkInfo.isConnected();
    }



    public static void expand(View summary) {
        //set Visible
        summary.setVisibility(View.VISIBLE);
        if(summary.getTag() == null)
        {
            summary.setTag(summary.getHeight());
            ValueAnimator mAnimator = slideAnimator(0, summary.getHeight(), summary);
            mAnimator.start();
        }
        else
        {
            int height = (int)summary.getTag();
            ValueAnimator mAnimator = slideAnimator(0, height, summary);
            mAnimator.start();
        }
//        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        summary.measure(widthSpec, summary.getMeasuredHeight());

    }




    public static void collapse(final View summary) {
        int finalHeight = summary.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, summary);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                summary.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }


    public static ValueAnimator slideAnimator(int start, int end, final View summary) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = summary.getLayoutParams();
                layoutParams.height = value;
                summary.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }


}
