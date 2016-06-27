package com.silencedut.uirelated;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clip_attribute);
        TextView text = (TextView) findViewById(R.id.textView);
        new HotChangeView(this);


//        SpannableString spannableString = new SpannableString("这是SpannableString的测试");
//        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.BLUE);
//
//        spannableString.setSpan(foregroundColorSpan,2,17, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        text.setText(spannableString);
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
}
