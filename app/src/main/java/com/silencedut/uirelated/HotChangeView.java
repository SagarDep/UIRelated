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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by SilenceDut on 16/6/24.
 */

public class HotChangeView extends FrameLayout{
    private View mPanelBackground;
    private View mDownView;
    private View mUpView;
    private TextView mDownTextView;
    private TextView mUpTextView;

    private ObjectAnimator mPanelAnimator;
    private ObjectAnimator mUpViewAnimator;
    private ObjectAnimator mDownViewAnimator;
    private ObjectAnimator mDownTextViewAnimator;
    private ObjectAnimator mUpTextViewAnimator;
    private ValueAnimator mExchangePanelAnimator;
    private ObjectAnimator mDownViewColorSizeAnimator;
    private AnimatorSet mAllAnimatorSet;
    private final Set<Animator> mAnimators = new HashSet<>();
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
        mDownTextView = (TextView) findViewById(R.id.downText);
        mUpTextView = (TextView) findViewById(R.id.upText);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER;
        activity.addContentView(this, layoutParams);
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
            }
        });

    }

    public void playAll() {
        mAllAnimatorSet = new AnimatorSet();
        mAllAnimatorSet.playSequentially(animatorIn(),animatorExchangePanel(),animatorText());
        mAllAnimatorSet.start();
        mAnimators.add(mAllAnimatorSet);
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
        mExchangePanelAnimator.setDuration(1000);
        mAnimators.add(mExchangePanelAnimator);
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
                animatorPanel();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mDownTextView.setText("1");
                mUpTextView.setText("2");
                mUpTextView.setBackgroundDrawable(getResources().getDrawable(R.mipmap.rank_down));
                mDownTextView.setBackgroundDrawable(getResources().getDrawable(R.mipmap.rank_top));
            }
        });
        mAnimators.add(mDownTextViewAnimator);
        mAnimators.add(mUpTextViewAnimator);
        return mDownTextViewAnimator;
    }

    private void animatorPanel() {
        PropertyValuesHolder valuesHolder1 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.2f,1.0f);
        PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.2f,1.0f);
        mDownViewColorSizeAnimator = ObjectAnimator.ofPropertyValuesHolder(mDownView,valuesHolder1,valuesHolder2);
        mDownViewColorSizeAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
               // mDownView.setBackground();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // mDownView.setBackground();
                animatorOut();
            }
        });
        mDownViewColorSizeAnimator.setDuration(400);
        mDownViewColorSizeAnimator.start();
        mAnimators.add(mDownViewColorSizeAnimator);
    }

    private void animatorOut() {
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

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeFromWindow();
            }
        });
        mPanelAnimator.setStartDelay(500);
        mPanelAnimator.start();
    }

    private ObjectAnimator getInAndOutAnimator(View targetView,float targetX) {
        ObjectAnimator animator =  ObjectAnimator.ofFloat(targetView,"x",targetX)
                .setDuration(600);
        mAnimators.add(animator);
        return animator;
    }


    private void removeFromWindow(){
        if(((ViewGroup)getParent())!=null) {
            ((ViewGroup) getParent()).removeView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for(Animator animator:mAnimators) {
            if(animator!=null&&animator.isRunning()){
                animator.cancel();
            }
        }
    }
}
