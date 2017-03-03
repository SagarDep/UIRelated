package com.silencedut.uirelated;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by SilenceDut on 2017/2/27 .
 */

public class DraggableLayout extends FrameLayout {
    private float mLastX;
    private float mLastY;
    private float mDownX;
    private float mDownY;
    private int mParentWidth;
    private int mParentHeight;
    private int mLayoutWidth;
    private int mLayoutHeight;
    private int mHorizontalMargin ;
    private int mVerticalMargin ;
    private boolean mNarrowed;
    private boolean mInited;
    private int mLargeX;
    private int mLargeY;
    private long mLongPressTimeOut;
    private long mLastDownTime;
    private int mTopMargin;

    private SizeChangedListener mSizeChangedListener;
    private int mMoveSlop;

    public DraggableLayout(Context context) {
        super(context);
        init();
    }

    public DraggableLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    private void init() {
        mLongPressTimeOut = ViewConfiguration.getLongPressTimeout();
        mMoveSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setSizeChangeListener(SizeChangedListener sizeChangeListener) {
        mSizeChangedListener = sizeChangeListener;
    }

    public void setParentDimensions(int screenWidth,int screenHeight) {
        mParentWidth = screenWidth;
        mParentHeight = screenHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,widthMeasureSpec*3/4);
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if(!mInited) {
            mLayoutWidth = getMeasuredWidth();
            mLayoutHeight = getMeasuredHeight();
            mInited = true;
            mLargeX = left;
            mLargeY = top;
            mParentWidth = ((ViewGroup)getParent()).getMeasuredWidth();
            mParentHeight = ((ViewGroup)getParent()).getMeasuredHeight();
            mTopMargin = ((LayoutParams)getLayoutParams()).topMargin;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(!mNarrowed) {
            return true;
        }

        float x = event.getRawX();
        float y = event.getRawY();

        float deltaX;
        float deltaY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mDownX = event.getRawX();
                mDownY = event.getRawY();
                mLastDownTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:

                deltaX = x-mLastX;
                deltaY = y- mLastY;

                if(Math.abs(deltaX)<mMoveSlop&&Math.abs(deltaY)<mMoveSlop) {
                    return true;
                }

                float targetTranslationX = getTranslationX() + deltaX;
                float targetTranslationY = getTranslationY() + deltaY;

                if (getRight() + targetTranslationX + mHorizontalMargin < mParentWidth && getLeft() + targetTranslationX - mHorizontalMargin >= 0) {
                    setTranslationX(targetTranslationX);
                }

                if (getBottom() + targetTranslationY + mVerticalMargin < mParentHeight && getTop() + targetTranslationY - mVerticalMargin >= 0) {
                    setTranslationY(targetTranslationY);
                }
                break;
            case MotionEvent.ACTION_UP:
                deltaX = x-mDownX;
                deltaY = y- mDownY;
                long intervalTime = System.currentTimeMillis() - mLastDownTime;
                if(deltaX==0&&deltaY==0&&mNarrowed&&intervalTime<mLongPressTimeOut) {
                    enlargeLayout();
                }
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }


//    public void operateSize() {
//        if(mNarrowed) {
//            enlargeLayout();
//        }else {
//            narrowLayout();
//        }
//    }

    public void enlargeLayout() {
        getLayoutParams().width = mLayoutWidth;
        getLayoutParams().height = mLayoutHeight;
        FrameLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        params.gravity= Gravity.TOP;
        params.topMargin = mTopMargin;
        params.leftMargin=0;
        params.rightMargin =0;
        setX(mLargeX);
        setY(mLargeY);
        setTranslationX(0);
        setTranslationY(0);
        mHorizontalMargin = 0;
        mVerticalMargin = 0;
        requestLayout();
        mNarrowed = false;
        if(mSizeChangedListener!=null) {
            mSizeChangedListener.onSizeChanged(false);
        }
    }

    public void narrowLayout() {
        getLayoutParams().width = mLayoutWidth/3;
        getLayoutParams().height = mLayoutWidth/3;
        FrameLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        params.gravity= Gravity.BOTTOM|Gravity.RIGHT;
        params.bottomMargin = 200;
        params.rightMargin = 20;
        mHorizontalMargin = 20;
        mVerticalMargin = 20;
        requestLayout();
        mNarrowed = true;
        if(mSizeChangedListener!=null) {
            mSizeChangedListener.onSizeChanged(true);
        }
    }


    public interface SizeChangedListener {
        void onSizeChanged(boolean narrowed);
    }

}