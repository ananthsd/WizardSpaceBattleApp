package com.dandibhotla.ananth.wizardspacebattleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import static com.dandibhotla.ananth.wizardspacebattleapp.MyGLRenderer.tutorialShown;

public class MainMenu extends Activity {
    private RelativeLayout menuLayout;
    private ImageView playButton, settingsButton;
    private Button tutorialButton;
    private Animation animation;
    private TextView title;
    private FirebaseAnalytics mFirebaseAnalytics;
    public static boolean firstTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if (sharedPref.getBoolean("musicToggle", true)) {
            Intent svc = new Intent(this, BackgroundSoundService.class);
            startService(svc);
        }
        setContentView(R.layout.main_menu);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firstTime = sharedPref.getBoolean("firstTime",true);
        init();
        initTitle();


// set an exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Fade());
            getWindow().setExitTransition(new Fade());
        }


        int colorP1 = sharedPref.getInt("colorP1", Color.BLUE);
        int colorP2 = sharedPref.getInt("colorP2", Color.RED);
        int colorBG = sharedPref.getInt("colorBG", Color.BLACK);
        Player.colorP1[0] = ((float) Color.red(colorP1)) / 255;
        Player.colorP1[1] = ((float) Color.green(colorP1)) / 255;
        Player.colorP1[2] = ((float) Color.blue(colorP1)) / 255;

        Player.colorP2[0] = ((float) Color.red(colorP2)) / 255;
        Player.colorP2[1] = ((float) Color.green(colorP2)) / 255;
        Player.colorP2[2] = ((float) Color.blue(colorP2)) / 255;

        Player.colorBG[0] = ((float) Color.red(colorBG)) / 255;
        Player.colorBG[1] = ((float) Color.green(colorBG)) / 255;
        Player.colorBG[2] = ((float) Color.blue(colorBG)) / 255;
    }

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
    }

    private void init() {
        playButton = (ImageView) findViewById(R.id.play_image);
        settingsButton = (ImageView) findViewById(R.id.settings_image);
        tutorialButton = (Button) findViewById(R.id.tutorial);
        animation = AnimationUtils.loadAnimation(MainMenu.this, R.anim.play_button_anim);
        animation.setDuration(500);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param., id);
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);*/
                        Intent intent = new Intent(MainMenu.this, GameScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        playButton.startAnimation(animation);


        animation = AnimationUtils.loadAnimation(MainMenu.this, R.anim.play_button_anim);

        animation.setDuration(600);


        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                settingsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param., id);
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);*/
                        Intent intent = new Intent(MainMenu.this, SettingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        settingsButton.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(MainMenu.this, R.anim.play_button_anim);

        animation.setDuration(700);


        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tutorialButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        firstTime=true;
                        tutorialShown = false;
                        Intent intent = new Intent(MainMenu.this, GameScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tutorialButton.startAnimation(animation);
    }

    private void initTitle() {
        title = (TextView) findViewById(R.id.game_title_text);
        animation = AnimationUtils.loadAnimation(MainMenu.this, R.anim.title_anim);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        title.startAnimation(animation);

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
