package com.example.exam2020_certificateapp.swipe;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector; //Instance gesture detector

    /**
     * attaches a gesture detector to the given context
     * @param context
     */
    public OnSwipeListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    /**
     * Used as a callback method to those that implements this class
     */
    public void onSwipeLeft() {
    }

    /**
     * Used as a callback method to those that implements this class
     */
    public void onSwipeRight() {
    }

    /**
     * Returns true if screen event matches the gesturedetectors onTouch event
     * View v is not used at the moment
     * @param v
     * @param event
     * @return
     */
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        /**
         * returns true no matter what e is
         * @param e
         * @return
         */
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        /**
         * Calculates whether or not a gesture is classified as a swipe by using e1 and e2 to calculate
         * distance, using that together with velocityX to see if the motion is fast enough.
         * VelocityY is not used since this method is only used to detect left/right swipe
         * @param e1
         * @param e2
         * @param velocityX
         * @param velocityY
         * @return true if swipe is detected, else false
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return false;
        }
    }
}
