package com.dandibhotla.ananth.wizardspacebattleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.dandibhotla.ananth.wizardspacebattleapp.Player.colorBG;

public class GameScreen extends Activity {

    private Point point1, point2;
    private static MyGLSurfaceView mGLView;

    public static volatile double mPreviousX, mPreviousY, mPreviousX2, mPreviousY2;
    public static volatile double mCurrentX, mCurrentY, mCurrentX2, mCurrentY2;
    private static Player player1, player2;
    private static double widthPixels, heightPixels;
    public static boolean p1Touch, p2Touch;
    public static ImageButton pauseButton;
    private RelativeLayout parentMenu, subMenu;
    private Button resumeButton,menuButton;
    public static boolean isPaused;
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if (BackgroundSoundService.player != null && sharedPref.getBoolean("musicToggle", true)) {
            BackgroundSoundService.player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if (BackgroundSoundService.player != null && sharedPref.getBoolean("musicToggle", true)) {
            BackgroundSoundService.player.start();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        }
    }

    public static void updateScore1() {
        score1.setText("P1 Score: " + player1.getScore());
    }

    public static void updateScore2() {
        score2.setText("P2 Score: " + player2.getScore());
    }

    public static void updateHealth() {
        if (health1 != null && health2 != null) {
            health1.setText("P1 Health: " + player1.getHealth());
            health2.setText("P2 Health: " + player2.getHealth());
        }
    }


    public static TextView score1, score2, health1, health2;
    private static RelativeLayout leftLayout, rightLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mGLView = new MyGLSurfaceView(this);
        setContentView(R.layout.content_game_screen);
        score1 = (TextView) findViewById(R.id.player1Score);
        score2 = (TextView) findViewById(R.id.player2Score);
        health1 = (TextView) findViewById(R.id.player1Health);
        health2 = (TextView) findViewById(R.id.player2Health);
        FrameLayout frame = (FrameLayout) findViewById(R.id.gameFrame);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Fade());
            getWindow().setExitTransition(new Fade());
        }
        leftLayout = (RelativeLayout) findViewById(R.id.leftRelativeLayout);
        rightLayout = (RelativeLayout) findViewById(R.id.rightRelativeLayout);
        leftLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isPaused){
                    return true;
                }
                float x = event.getX();
                float y = event.getY();
                // Log.v("action",MotionEvent.actionToString(event.getAction()));
                switch (event.getAction()) {

                    case MotionEvent.ACTION_MOVE:
                        // Log.v("action","move");
                        player1.setMoveValues(Math.atan2(y - mPreviousY, x - mPreviousX), Math.sqrt((x - mPreviousX) * (x - mPreviousX) + (y - mPreviousY) * (y - mPreviousY)));
                        //player1.move();
                        mCurrentX = x;
                        mCurrentY = y;
                        mGLView.requestRender();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        // Log.v("action","down");
                        mPreviousX = x;
                        mPreviousY = y;
                        p1Touch = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        //  Log.v("action","up");
                        p1Touch = false;
                        break;

                }
                return true;
            }
        });
        rightLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isPaused){
                    return true;
                }
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        player2.setMoveValues(Math.atan2(y - mPreviousY2, x - mPreviousX2), Math.sqrt((x - mPreviousX2) * (x - mPreviousX2) + (y - mPreviousY2) * (y - mPreviousY2)));
                        // player2.move();
                        mCurrentX2 = x;
                        mCurrentY2 = y;
                        mGLView.requestRender();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        mPreviousX2 = x;
                        mPreviousY2 = y;
                        p2Touch = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        p2Touch = false;
                        break;
                }
                return true;
            }
        });

        frame.addView(mGLView, 0);
        pauseButton = (ImageButton) findViewById(R.id.pauseButton);

        point1 = new Point();
        point2 = new Point();

        // setContentView(mGLView);

        DisplayMetrics display = getResources().getDisplayMetrics();
        widthPixels = display.widthPixels;
        heightPixels = display.heightPixels;
        // Log.v("sizeD", widthPixels + ";" + heightPixels);
        mGLView.getHolder().setFixedSize((int) widthPixels, (int) heightPixels);

       /* score1.setTextColor(Color.rgb((int) (Player.colorP1[0] * 255), (int) (Player.colorP1[1] * 255), (int) (Player.colorP1[2] * 255)));
        score2.setTextColor(Color.rgb((int) (Player.colorP2[0] * 255), (int) (Player.colorP2[1] * 255), (int) (Player.colorP2[2] * 255)));

        health1.setTextColor(Color.rgb((int) (Player.colorP1[0] * 255), (int) (Player.colorP1[1] * 255), (int) (Player.colorP1[2] * 255)));
        health2.setTextColor(Color.rgb((int) (Player.colorP2[0] * 255), (int) (Player.colorP2[1] * 255), (int) (Player.colorP2[2] * 255)));*/

        if (0.2126 * colorBG[0] + 0.7152 * colorBG[1] + 0.0722 * colorBG[2] > 0.179) {
            score1.setTextColor(Color.BLACK);
            health1.setTextColor(Color.BLACK);
            score2.setTextColor(Color.BLACK);
            health2.setTextColor(Color.BLACK);
            pauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
        } else {
            score1.setTextColor(Color.WHITE);
            health1.setTextColor(Color.WHITE);
            score2.setTextColor(Color.WHITE);
            health2.setTextColor(Color.WHITE);
            pauseButton.setImageResource(R.drawable.ic_action_pause);
        }
        isPaused = false;
        parentMenu = (RelativeLayout)findViewById(R.id.menu_layout);
        subMenu = (RelativeLayout)findViewById(R.id.subMenu);
        resumeButton = (Button)subMenu.findViewById(R.id.resumeButton);
        menuButton = (Button)subMenu.findViewById(R.id.backMenuButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.v("pause", "clicked");
                subMenu.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);

                parentMenu.setBackgroundColor(Color.parseColor("#80000000"));
                isPaused = true;
                mGLView.onPause();
            }
        });
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subMenu.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                parentMenu.setBackgroundColor(Color.parseColor("#00000000"));
                isPaused = false;
                mGLView.onResume();
            }
        });
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameScreen.this,MainMenu.class));
            }
        });
       /* score1.setTextColor(Color.WHITE);
        score2.setTextColor(Color.WHITE);

        health1.setTextColor(Color.WHITE);
        health2.setTextColor(Color.WHITE);*/

        health1.setText("P1 Health: " + player1.getHealth());
        health2.setText("P2 Health: " + player2.getHealth());

    }


    class MyGLSurfaceView extends GLSurfaceView {

        public final MyGLRenderer mRenderer;

        public float getScreenHeight() {
            return mRenderer.getScreenHeight();
        }

        public float getScreenWidth() {
            return mRenderer.getScreenWidth();
        }

        public MyGLRenderer getRenderer() {
            return mRenderer;
        }

        public MyGLSurfaceView(Context context) {
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            mRenderer = new MyGLRenderer(context, score1, score2, health1, health2);

            // Set the Renderer for drawing on the GLSurfaceView

            setRenderer(mRenderer);
            //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            player1 = mRenderer.getPlayer1();
            player2 = mRenderer.getPlayer2();

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

    public static float getmPreviousXFloat() {
        double distance = mPreviousX / widthPixels * widthPixels / heightPixels * 2;
        //Log.v("joystick", mPreviousX + "");
        return (float) (-distance + mGLView.getScreenWidth());
    }

    public static float getmPreviousYFloat() {
        if (mPreviousY > heightPixels / 2) {
            double distance = mPreviousY - heightPixels / 2;
            return -(float) (distance / (heightPixels / 2));
        } else {
            double distance = heightPixels / 2 - mPreviousY;
            return (float) (distance / (heightPixels / 2));
        }
    }

    public static float getmPreviousX2Float() {

        double distance = (mPreviousX2) / (widthPixels / 2) * -widthPixels / heightPixels;
        double x2 = -mPreviousX2 / widthPixels * widthPixels / heightPixels * 2;
        double distance2 = mPreviousX2 / rightLayout.getWidth() * widthPixels / heightPixels;
        //Log.v("joystick", mPreviousX2 + ";" + widthPixels / heightPixels);
        // Log.v("joystick",mPreviousX2/widthPixels*mGLView.getScreenWidth()*2+";");
        return (float) -distance2;
    }

    public static float getmPreviousY2Float() {
        if (mPreviousY2 > heightPixels / 2) {
            double distance = mPreviousY2 - heightPixels / 2;
            return -(float) (distance / (heightPixels / 2));
        } else {
            double distance = heightPixels / 2 - mPreviousY2;
            return (float) (distance / (heightPixels / 2));
        }
    }


    public static float getmCurrentXFloat() {
        double distance = mCurrentX / widthPixels * widthPixels / heightPixels * 2;
        //Log.v("joystick", mPreviousX + "");
        return (float) (-distance + mGLView.getScreenWidth());
    }

    public static float getmCurrentYFloat() {
        if (mCurrentY > heightPixels / 2) {
            double distance = mCurrentY - heightPixels / 2;
            return -(float) (distance / (heightPixels / 2));
        } else {
            double distance = heightPixels / 2 - mCurrentY;
            return (float) (distance / (heightPixels / 2));
        }
    }

    public static float getmCurrentX2Float() {


        double distance2 = mCurrentX2 / rightLayout.getWidth() * widthPixels / heightPixels;
        //Log.v("joystick", mPreviousX2 + ";" + widthPixels / heightPixels);
        // Log.v("joystick",mPreviousX2/widthPixels*mGLView.getScreenWidth()*2+";");
        return (float) -distance2;
    }

    public static float getmCurrentY2Float() {
        if (mCurrentY2 > heightPixels / 2) {
            double distance = mCurrentY2 - heightPixels / 2;
            return -(float) (distance / (heightPixels / 2));
        } else {
            double distance = heightPixels / 2 - mCurrentY2;
            return (float) (distance / (heightPixels / 2));
        }
    }

}
