package edu.uw.animdemo;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";

    private DrawingSurfaceView view;

    private AnimatorSet radiusAnim;

    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (DrawingSurfaceView)findViewById(R.id.drawingView);

        radiusAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.animations);

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.v(TAG, event.toString());

        boolean gesture = mDetector.onTouchEvent(event); //ask the detector to handle instead
        //if(gesture) return true; //if we don't also want to handle

        float x = event.getX();
        float y = event.getY() - getSupportActionBar().getHeight(); //closer to center...

        int action = MotionEventCompat.getActionMasked(event);

        int pointerIndex = MotionEventCompat.getActionIndex(event);
        int pointerID = MotionEventCompat.getPointerId(event, pointerIndex);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) : //put finger down
                //Log.v(TAG, "finger down");
                pointerIndex = MotionEventCompat.getActionIndex(event);
                Log.v(TAG, "down index: " + pointerIndex);
                view.addTouch(event, pointerIndex);

//                ObjectAnimator xAnim = ObjectAnimator.ofFloat(view.ball, "x", x);
//                xAnim.setDuration(1000);
//                ObjectAnimator yAnim = ObjectAnimator.ofFloat(view.ball, "y", y);
//                yAnim.setDuration(1500); //y moves 1.5x slower
//
//                AnimatorSet set = new AnimatorSet();
//                set.playTogether(yAnim, xAnim);
//                set.start();

//                view.ball.cx = x;
//                view.ball.cy = y;
//                view.ball.dx = (x - view.ball.cx)/Math.abs(x - view.ball.cx)*30;
//                view.ball.dy = (y - view.ball.cy)/Math.abs(y - view.ball.cy)*30;
                return true;
            case (MotionEvent.ACTION_MOVE) : //move finger
                int count = event.getPointerCount();
                int newX = 0;
                int newY = 0;
                for (int i = 0; i < count; i++) {
                    MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
                    event.getPointerCoords(i, pointerCoords);
                    newX = (int)pointerCoords.getAxisValue(0);
                    newY = (int)pointerCoords.getAxisValue(1);
                    if (newX == event.getX(i) && newY == event.getY(i)) {
                        pointerID = MotionEventCompat.getPointerId(event, pointerIndex);
                        view.moveTouch(pointerID, newX, newY);
                    }
                }
                view.moveTouch(pointerID, newX, newY);
                //Log.v(TAG, "finger move");
//                view.ball.cx = x;
//                view.ball.cy = y;
                return true;
            case (MotionEvent.ACTION_UP) : //lift finger up
                pointerIndex = MotionEventCompat.getActionIndex(event);
                //Log.v(TAG, "touch point: " );
                Log.v(TAG, "up index: " + pointerIndex);
                view.removeTouch(event, pointerIndex);
                return true;
            case (MotionEvent.ACTION_CANCEL) : //aborted gesture
            case (MotionEvent.ACTION_OUTSIDE) : //outside bounds
            case (MotionEvent.ACTION_POINTER_DOWN) :
                pointerIndex = MotionEventCompat.getActionIndex(event);
                Log.v(TAG, "pointer down index: " + pointerIndex);
                view.addTouch(event, pointerIndex);
                //float radius = view.ball.getRadius();
//                if (radius < 300) {
//                    view.ball.setRadius(radius + 50);
//                }
                return true;
            case (MotionEvent.ACTION_POINTER_UP) :
                pointerIndex = MotionEventCompat.getActionIndex(event);
                Log.v(TAG, "pointer up index: " + pointerIndex);
                view.removeTouch(event, pointerIndex);
                //view.ball.setRadius(rad - 50);
//                if (rad < 100) {
//                    view.ball.setRadius(100);
//                }
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }


    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true; //recommended practice
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float scaleFactor = .03f;

            //fling!
            Log.v(TAG, "Fling! "+ velocityX + ", " + velocityY);
            view.ball.dx = -1*velocityX*scaleFactor;
            view.ball.dy = -1*velocityY*scaleFactor;

            return true; //we got this
        }
    }


    /** Menus **/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.menu_pulse:
                //make the ball change size!
                if(!radiusAnim.isRunning()) {
                    radiusAnim.setTarget(view.ball);
                    radiusAnim.start();
                } else {
                    radiusAnim.end();
                }
                return true;
            case R.id.menu_button:
                startActivity(new Intent(MainActivity.this, ButtonActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
