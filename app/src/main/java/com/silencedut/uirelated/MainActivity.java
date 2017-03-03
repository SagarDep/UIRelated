package com.silencedut.uirelated;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private HotChangeView mHotChangeView;
    private DraggableLayout mDraggableLayout;
    TextView text;
    View mOperate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clip_attribute);
        mDraggableLayout = (DraggableLayout)findViewById(R.id.draggableLayout);
        mOperate = findViewById(R.id.operate);
        text = (TextView) findViewById(R.id.textView);
        mHotChangeView =new HotChangeView(this);
        final View parentView = findViewById(R.id.parent);
        assert parentView != null;
        mDraggableLayout.setSizeChangeListener(new DraggableLayout.SizeChangedListener() {
            @Override
            public void onSizeChanged(boolean narrowed) {
                mOperate.setVisibility(narrowed?View.GONE:View.VISIBLE);
            }
        });

//        parentView.post(new Runnable() {
//            @Override
//            public void run() {
//                mDraggableLayout.setParentDimensions(parentView.getWidth(),parentView.getHeight());
//            }
//        });

        text.setOnClickListener(this);
        mOperate.setOnClickListener(this);

        ValueAnimator valueAnimator  = ValueAnimator.ofInt(0,1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float fraction;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fraction =  animation.getAnimatedFraction();
                if(fraction>0&&fraction<=0.33) {
                    text.setText("正在加载.");
                }else if(fraction>0.33&&fraction<0.66) {
                    text.setText("正在加载..");
                }else {
                    text.setText("正在加载...");
                }
            }
        });
        valueAnimator.setDuration(3000);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.start();

    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Log.i(TAG,""+drawable.getIntrinsicWidth()+"\n"+drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView :
                mHotChangeView.playAll();
                Toast.makeText(this,"TextClicked",Toast.LENGTH_LONG).show();
                break;
            case R.id.operate :
                mDraggableLayout.narrowLayout();
                break;
        }
    }
}
