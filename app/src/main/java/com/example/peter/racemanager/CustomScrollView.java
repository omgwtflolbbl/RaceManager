package com.example.peter.racemanager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Peter on 6/4/2016.
 */
public class CustomScrollView extends ScrollView {

    float touchX = 0;
    float touchY = 0;

    ViewPager parentPager;

    public void setParentPager(ViewPager parentPager) {
        this.parentPager = parentPager;
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {



        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                touchX = ev.getX();
                touchY = ev.getY();
                return super.onTouchEvent(ev);
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(touchX-ev.getX())<40){
                    Log.i("HEY", "IS IT SCROLLING");
                    return super.onTouchEvent(ev);
                }else{
                    if (parentPager==null) {
                        return false;
                    } else {
                        return parentPager.onTouchEvent(ev);
                    }
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                touchX=0;
                touchY=0;
                break;
        }
        return super.onTouchEvent(ev);
    }
}