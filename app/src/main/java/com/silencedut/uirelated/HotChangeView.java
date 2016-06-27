package com.silencedut.uirelated;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


/**
 * Created by SilenceDut on 16/6/24.
 */

public class HotChangeView extends FrameLayout implements View.OnClickListener{
    private View mPanelBackground;
    private View mDownView;
    private View mUpView;
    private View mDownTextView;
    private View mUpTextView;

    private ObjectAnimator mPanelAnimator;
    private ObjectAnimator mUpViewAnimator;
    private ObjectAnimator mDownViewAnimator;
    private ObjectAnimator mDownTextViewAnimator;
    private ObjectAnimator mUpTextViewAnimator;
    private ValueAnimator mExchangePanelAnimator;
    private AnimatorSet mAllAnimatorSet;
    private float mOriginalPanelX;
    private float mOriginalUpViewX;
    private float mOriginalDownViewX;
    private float mOriginalUpViewY;
    private float mOriginanDownViewY;

    public HotChangeView(Activity activity) {
        super(activity);
        init(activity);
    }

    private void init(Activity activity) {
        inflate(getContext(),R.layout.hot_change_view,this);
        this.setBackgroundColor(Color.parseColor("#00000000"));
        mPanelBackground = findViewById(R.id.panelBackground);
        mUpView = findViewById(R.id.upPanel);
        mDownView = findViewById(R.id.downPanel);
        mDownTextView = findViewById(R.id.downText);
        mUpTextView = findViewById(R.id.upText);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER;
        activity.addContentView(this, layoutParams);
        setOnClickListener(this);
        post(new Runnable() {
            @Override
            public void run() {
                mOriginalPanelX = mPanelBackground.getX();
                mOriginalUpViewX = mUpView.getX();
                mOriginalDownViewX = mDownView.getX();
                mOriginalUpViewY = mUpView.getY();
                mOriginanDownViewY = mDownView.getY();
                mPanelBackground.setX(getRight());
                mUpView.setX(-mUpView.getRight());
                mDownView.setX(-mDownView.getRight());
                playAll();
            }
        });

    }

    private void playAll() {
        mAllAnimatorSet = new AnimatorSet();
        mAllAnimatorSet.playSequentially(animatorIn(),animatorExchangePanel(),animatorText());
        mAllAnimatorSet.start();
    }

    private ValueAnimator animatorIn() {
        mPanelAnimator = getInAndOutAnimator(mPanelBackground,mOriginalPanelX);
        mUpViewAnimator = getInAndOutAnimator(mUpView,mOriginalUpViewX);
        mDownViewAnimator = getInAndOutAnimator(mDownView,mOriginalDownViewX);
        mDownViewAnimator.setStartDelay(50);
        mPanelAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mUpViewAnimator.start();
                mDownViewAnimator.start();

            }
        });

        return mPanelAnimator;
    }

    private ValueAnimator animatorExchangePanel() {
        mExchangePanelAnimator = ValueAnimator.ofFloat(0,(float) (Math.PI));
        mExchangePanelAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float angle;
            float downViewX ;
            float downViewY ;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angle = (float) animation.getAnimatedValue();
                downViewX = (float) (Math.sin(angle)*mDownView.getHeight()/2);
                downViewY = mDownView.getHeight()/2-(float) (Math.cos(angle)*mDownView.getHeight()/2);
                mDownView.setX( mOriginalDownViewX+downViewX);
                mDownView.setY(mOriginanDownViewY-downViewY);
                mUpView.setX( mOriginalUpViewX - downViewX);
                mUpView.setY(mOriginalUpViewY + downViewY);
            }
        });
        mExchangePanelAnimator.setDuration(300);
        return mExchangePanelAnimator;
    }

    private ValueAnimator animatorText() {

        PropertyValuesHolder valuesHolder1 = PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f);
        PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f);
        mDownTextViewAnimator = ObjectAnimator.ofPropertyValuesHolder(mDownTextView,valuesHolder1, valuesHolder2);
        mUpTextViewAnimator = ObjectAnimator.ofPropertyValuesHolder(mUpTextView,valuesHolder1, valuesHolder2);
        mDownTextViewAnimator.setDuration(400);
        mUpTextViewAnimator.setDuration(400);
        mDownTextViewAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mUpTextViewAnimator.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorOut();
            }
        });
        return mDownTextViewAnimator;
    }

    private ValueAnimator animatorOut() {
        mPanelAnimator = getInAndOutAnimator(mPanelBackground,-mPanelBackground.getRight());
        mUpViewAnimator = getInAndOutAnimator(mUpView,getRight());
        mDownViewAnimator = getInAndOutAnimator(mDownView,getRight());
        mUpViewAnimator.setStartDelay(50);
        mPanelAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mUpViewAnimator.start();
                mDownViewAnimator.start();
            }
        });
        mPanelAnimator.start();
        return mPanelAnimator;
    }

    private ObjectAnimator getInAndOutAnimator(View targetView,float targetX) {
        return ObjectAnimator.ofFloat(targetView,"x",targetX)
                .setDuration(500);
    }

    @Override
    public void onClick(View v) {
        animatorExchangePanel();
        playAll();
    }
}