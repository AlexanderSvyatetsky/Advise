package ua.dreambim.advise.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class VerticalScrollView extends ScrollView {

    private int fullScrollViewWidht;
    private int fullScrollViewHeight;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.i("onSizeChanged", "w=" + w + ", h=" + h + ", oldw=" + oldw + ", oldh=" + oldh);
        if (oldw == 0 && oldh == 0) {
            fullScrollViewWidht = w;
            fullScrollViewHeight = h;
        } else if (w == fullScrollViewWidht && h == fullScrollViewHeight) {
            Log.i("onSizeChanged", "resized, hide scrollView");
            setScrollbarFadingEnabled(true);
        } else {
            Log.i("onSizeChanged", "resized, show scrollView");
            setVerticalScrollBarEnabled(true);
            setScrollbarFadingEnabled(false);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public VerticalScrollView(Context context) {
        super(context);
    }

    public VerticalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i("VerticalScrollview", "onInterceptTouchEvent: DOWN super false");
                super.onTouchEvent(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                return false; // redirect MotionEvents to ourself

            case MotionEvent.ACTION_CANCEL:
                Log.i("VerticalScrollview", "onInterceptTouchEvent: CANCEL super false");
                super.onTouchEvent(ev);
                break;

            case MotionEvent.ACTION_UP:
                Log.i("VerticalScrollview", "onInterceptTouchEvent: UP super false");
                return false;

            default:
                Log.i("VerticalScrollview", "onInterceptTouchEvent: " + action);
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int y;
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            Log.i("VerticalScrollview", "Action DOWN: ");
            y=(int)ev.getY();
            smoothScrollTo(0,y);

        }else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
             y= (int) ev.getY();

            Log.i("VerticalScrollview", "y=" + y  + ", getScrollY=" + getScrollY() + ", height=" + getBottom());
            if (y >= 0) {
                smoothScrollTo(0, y);
            }


        } else if (ev.getAction() == MotionEvent.ACTION_SCROLL) {
            Log.i("VerticalScrollview", "ActionScroll: ");
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            Log.i("VerticalScrollview", "Action UP: ");
        }


        Log.i("VerticalScrollview", "onTouchEvent. action: " + ev.getAction());
        return true;
    }
}