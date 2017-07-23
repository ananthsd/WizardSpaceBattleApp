package com.dandibhotla.ananth.wizardspacebattleapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameScreen extends Activity {
    private VelocityTracker mVelocityTracker = null;
    private Point point1, point2;
    private MyGLSurfaceView mGLView;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    private float mPreviousX2;
    private float mPreviousY2;
    private Player player1, player2;
    private int widthPixels, heightPixels;
    private boolean p1TouchFirst;
    private TextView score1, score2;
    private RelativeLayout leftLayout,rightLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mGLView = new MyGLSurfaceView(this);
        setContentView(R.layout.content_game_screen);
        FrameLayout frame = (FrameLayout) findViewById(R.id.gameFrame);
       /* score1 = (TextView) findViewById(R.id.p1ScoreText);
        score2 = (TextView) findViewById(R.id.p2ScoreText);

        score1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("touch","left");
                return true;
            }
        });
        score2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("touch","right");
                return true;
            }
        });*/
       leftLayout = (RelativeLayout)findViewById(R.id.leftRelativeLayout);
       rightLayout = (RelativeLayout)findViewById(R.id.rightRelativeLayout);
        leftLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        player1.move(Math.atan2(y - mPreviousY, x - mPreviousX), Math.sqrt((x - mPreviousX) * (x - mPreviousX) + (y - mPreviousY) * (y - mPreviousY)));
                        mGLView.requestRender();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        mPreviousX = x;
                        mPreviousY = y;
                }
                return true;
            }
        });
        rightLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        player2.move(Math.atan2(y - mPreviousY2, x - mPreviousX2), Math.sqrt((x - mPreviousX2) * (x - mPreviousX2) + (y - mPreviousY2) * (y - mPreviousY2)));

                        mGLView.requestRender();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        mPreviousX2 = x;
                        mPreviousY2 = y;
                }
                return true;
            }
        });

        frame.addView(mGLView,0);

        point1 = new Point();
        point2 = new Point();

        // setContentView(mGLView);

        DisplayMetrics display = getResources().getDisplayMetrics();
        widthPixels = display.widthPixels;
        heightPixels = display.heightPixels;
    }



    class MyGLSurfaceView extends GLSurfaceView {

        private final MyGLRenderer mRenderer;

        public float getScreenHeight() {
            return mRenderer.getScreenHeight();
        }

        public float getScreenWidth() {
            return mRenderer.getScreenWidth();
        }

        public MyGLSurfaceView(Context context) {
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            mRenderer = new MyGLRenderer(context);

            // Set the Renderer for drawing on the GLSurfaceView

            setRenderer(mRenderer);
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            player1 = mRenderer.getPlayer1();
            player2 = mRenderer.getPlayer2();
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            /*
            float x = e.getX();
            float y = e.getY();

            if (x < widthPixels/2) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        player1.move(Math.atan2(y - mPreviousY, x - mPreviousX), Math.sqrt((x - mPreviousX) * (x - mPreviousX) + (y - mPreviousY) * (y - mPreviousY)));

                        requestRender();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        mPreviousX = x;
                        mPreviousY = y;
                }
            }
            else{

                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        player2.move(Math.atan2(y - mPreviousY2, x - mPreviousX2), Math.sqrt((x - mPreviousX2) * (x - mPreviousX2) + (y - mPreviousY2) * (y - mPreviousY2)));

                        requestRender();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        mPreviousX2 = x;
                        mPreviousY2 = y;
                }
            }
*/

            if (e.getPointerCount() == 1) {

                float x = e.getX();
                float y = e.getY();

                if (x < widthPixels / 2) {
                    p1TouchFirst = true;
                    switch (e.getAction()) {

                        case MotionEvent.ACTION_MOVE:
                            player1.move(Math.atan2(y - mPreviousY, x - mPreviousX), Math.sqrt((x - mPreviousX) * (x - mPreviousX) + (y - mPreviousY) * (y - mPreviousY)));

                            requestRender();
                            break;
                        case MotionEvent.ACTION_DOWN:
                            mPreviousX = x;
                            mPreviousY = y;
                    }
                } else {
                    p1TouchFirst = false;
                    switch (e.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            player2.move(Math.atan2(y - mPreviousY2, x - mPreviousX2), Math.sqrt((x - mPreviousX2) * (x - mPreviousX2) + (y - mPreviousY2) * (y - mPreviousY2)));

                            requestRender();
                            break;
                        case MotionEvent.ACTION_DOWN:
                            mPreviousX2 = x;
                            mPreviousY2 = y;
                    }
                }
            } else {
                if (p1TouchFirst) {

                }
            }
            return false;
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
