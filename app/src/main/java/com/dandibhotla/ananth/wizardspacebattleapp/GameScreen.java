package com.dandibhotla.ananth.wizardspacebattleapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.Window;
import android.view.WindowManager;

public class GameScreen extends Activity {
    private VelocityTracker mVelocityTracker = null;
    private Point point1;
    private GLSurfaceView mGLView;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
        point1 = new Point();

    }
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

      switch (action){
          case MotionEvent.ACTION_DOWN:
              point1.set((int)event.getX(),(int)event.getY());
              break;
          case MotionEvent.ACTION_MOVE:
              Log.v("movement","Distance: "+  Math.sqrt(Math.pow(event.getX() - point1.x, 2) + Math.pow(event.getY() - point1.y, 2)));
              break;
      }


        return true;
    }

    class MyGLSurfaceView extends GLSurfaceView {

        private final MyGLRenderer mRenderer;

        public MyGLSurfaceView(Context context){
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            mRenderer = new MyGLRenderer();

            // Set the Renderer for drawing on the GLSurfaceView

            setRenderer(mRenderer);
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;

                    // reverse direction of rotation above the mid-line
                    if (y > getHeight() / 2) {
                        dx = dx * -1 ;
                    }

                    // reverse direction of rotation to left of the mid-line
                    if (x < getWidth() / 2) {
                        dy = dy * -1 ;
                    }

                    mRenderer.setAngle(
                            mRenderer.getAngle() -
                                    ((dx + dy) * TOUCH_SCALE_FACTOR));
                    requestRender();
            }

            mPreviousX = x;
            mPreviousY = y;
            return true;
        }
    }
}
