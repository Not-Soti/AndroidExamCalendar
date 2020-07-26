package com.example.examcalendar.MonthActivity;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.examcalendar.MonthActivity.MonthActivityController;

/**
 * Class made to override the interceptTouchEvent on the MonthActivity
 * so it swipes left and right but gives other events to it's child views
 */
public class SwipeRelativeLayoutMonthActivity extends RelativeLayout {
    private int mTouchSlop;
    private Context context;
    private float firstX, firstY; //needed to determine the first touch when swapping


    public SwipeRelativeLayoutMonthActivity(Context context) {
        super(context);
        this.context = context;
        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
    }

    public SwipeRelativeLayoutMonthActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
    }

    public SwipeRelativeLayoutMonthActivity(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
    }

    public SwipeRelativeLayoutMonthActivity(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        boolean mIsScrolling = true;

        //final int action = MotionEventCompat.getActionMasked(ev);
        final int action = ev.getAction();


        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the scroll.
            mIsScrolling = false;
            return false; // Do not intercept touch event, let the child handle it
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                firstX = ev.getX();
                firstY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE: {
                final float xDiff = Math.abs(ev.getX() - firstX);
                final float yDiff = Math.abs(ev.getY() - firstY);

                if (mIsScrolling) {

                    if(yDiff > xDiff){ //It was a vertical swipe, not intercepting it
                        return false;
                    }

                    // We're currently scrolling, so yes, intercept the
                    // touch event!
                    return true;
                }

                // If the user has dragged her finger horizontally more than
                // the touch slop, start the scroll

                // Touch slop should be calculated using ViewConfiguration
                // constants.
                if (xDiff > mTouchSlop) {
                    // Start scrolling!
                    mIsScrolling = true;
                    return true;
                }
                break;
            }
        }

        // In general, we don't want to intercept touch events. They should be
        // handled by the child view.
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Here we actually handle the touch event (e.g. if the action is ACTION_MOVE,
        // scroll this container).
        // This method will only be called if the touch event was intercepted in
        // onInterceptTouchEvent

        //Same as before
        int action = ev.getAction();
        boolean swipeDone = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN: //Drag started
                firstX = ev.getRawX();
                break;
            case MotionEvent.ACTION_UP:
                float newX = ev.getX(), newY = ev.getY();
                float xDiff = newX - firstX, yDiff = newY - firstY;
                //Toast.makeText(this.context, "Swipe pa un lao firstX = " + firstX + " lastX = " + ev.getX() + " xDiff = "+xDiff, Toast.LENGTH_SHORT).show();

                //If xDiff > 0; the swipe was from right to left
                //otherwise it was from left to right
                if (Math.abs(xDiff) > Math.abs(yDiff)) { //the swipe was horizontal and not vertical
                    if ((xDiff > 0) & (Math.abs(xDiff) > mTouchSlop)) {
                        MonthActivityController act = (MonthActivityController) context;
                        swipeDone = true;
                        act.previousMonthPressed();
                    }
                    if ((xDiff < 0) & (Math.abs(xDiff) > mTouchSlop)) {
                        MonthActivityController act = (MonthActivityController) context;
                        swipeDone = true;
                        act.nextMonthPressed();
                    }
                }
                break;
        }
        return swipeDone;
    }
}
