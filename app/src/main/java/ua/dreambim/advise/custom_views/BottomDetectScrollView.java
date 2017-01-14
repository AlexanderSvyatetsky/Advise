package ua.dreambim.advise.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

/**
 * Created by MykhailoIvanov on 12/5/2016.
 */

/*
USAGE:
    0. use instead of default ScrollView in .xml file.
    1. You have to use LinearLayout as child of this class object.
    2. somewhere in your code take this class object from .xml;
       create realization of bottomDetectedCallback interface class
       and define object of created class.
       Redefined function BottomDetectedCallback.callback will be called if
       bottom of ScrollView was reached.

    EXTRA:
        if you do not want this class to call your function
        BottomDetectedCallback.callback sometimes use flag
        BottomDetectScrollView.canBeCalled

    Mykhailo Ivanov (c)
 */
public class BottomDetectScrollView extends ScrollView {

    // FIELDS AND METHODS YOU HAVE TO WORK WITH

    /* if the bottom was reached,
       it will be set false*/
    public boolean canBeCalled = true;

    public interface BottomDetectedCallback{
        void callback();
    }

    public BottomDetectedCallback bottomDetectedCallback;

    // use before any changed is child view!
    public void removeProgressBar()
    {
        LinearLayout childLayout = (LinearLayout) getChildAt(0);
        childLayout.removeViewAt(childLayout.getChildCount() - 1);
    }

    //



    public BottomDetectScrollView(Context context) {
        super(context);
    }

    public BottomDetectScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomDetectScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt){
        super.onScrollChanged(l, t, oldl, oldt);

        View view = this.getChildAt(this.getChildCount() - 1);

        int difference = (view.getBottom() - (this.getHeight() + this.getScrollY()));

        if ((difference == 0) && (canBeCalled)) {

            LinearLayout childLayout = (LinearLayout) getChildAt(0);
            childLayout.addView(new ProgressBar(getContext()));

            canBeCalled = false;

            bottomDetectedCallback.callback();
        }
    }
}
