package com.dandibhotla.ananth.wizardspacebattleapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;

import static com.dandibhotla.ananth.wizardspacebattleapp.Player.colorBG;
import static com.dandibhotla.ananth.wizardspacebattleapp.Player.colorP1;
import static com.dandibhotla.ananth.wizardspacebattleapp.Player.colorP2;

public class SettingsActivity extends Activity {
    private RecyclerView settingsRecyclerView;
    private static MyGLSurfaceView mGLView;
    private static double widthPixels, heightPixels;

    private static RelativeLayout leftLayout, rightLayout, infoLayout;
    private List<String> settingsList;
    private LinearLayout glLinearLayout;
    private Button p1Color, p2Color, bgColor, volumeButton;
    private ColorPicker p1ColorPicker, p2ColorPicker, bgColorPicker;
    private DiscreteSeekBar volumeBar;
    private TextView text_seekbar;
    private CheckBox toggleMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mGLView = new MyGLSurfaceView(this);
        setContentView(R.layout.settings_screen);

        FrameLayout frame = (FrameLayout) findViewById(R.id.settingsFrame);

        leftLayout = (RelativeLayout) findViewById(R.id.leftRelativeLayout);
        rightLayout = (RelativeLayout) findViewById(R.id.rightRelativeLayout);
        glLinearLayout = (LinearLayout) findViewById(R.id.glFrameLayout);

        glLinearLayout.addView(mGLView, 0);


        DisplayMetrics display = getResources().getDisplayMetrics();
        widthPixels = display.widthPixels / 2;
        heightPixels = display.heightPixels / 2;
        //mGLView.getHolder().setFixedSize((int) widthPixels, (int) heightPixels);
        settingsList = new ArrayList<>();
        settingsList.add("Colors");
        settingsList.add("Music");
       // settingsList.add("Colors");
       // settingsList.add("Colors");
        settingsRecyclerView = (RecyclerView) findViewById(R.id.settings_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        settingsRecyclerView.setLayoutManager(layoutManager);
        settingsRecyclerView.setAdapter(new SettingsAdapter(this, settingsList));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(settingsRecyclerView.getContext(),
                layoutManager.getOrientation());
        settingsRecyclerView.addItemDecoration(dividerItemDecoration);

        infoLayout = (RelativeLayout) findViewById(R.id.settingColorInfoLayout);
        p1Color = (Button) infoLayout.findViewById(R.id.p1ColorButton);
        p2Color = (Button) infoLayout.findViewById(R.id.p2ColorButton);
        bgColor = (Button) infoLayout.findViewById(R.id.bgColorButton);
        SharedPreferences sharedPref = getSharedPreferences("Settings",Context.MODE_PRIVATE);

        p1Color.setBackgroundColor(sharedPref.getInt("colorP1",Color.BLUE));
        p2Color.setBackgroundColor(sharedPref.getInt("colorP2",Color.RED));
        bgColor.setBackgroundColor(sharedPref.getInt("colorBG",Color.BLACK));

        p1ColorPicker = new ColorPicker(SettingsActivity.this, (int) (colorP1[0] * 255), (int) (colorP1[1] * 255), (int) (colorP1[2] * 255));
        p2ColorPicker = new ColorPicker(SettingsActivity.this, (int) (colorP2[0] * 255), (int) (colorP2[1] * 255), (int) (colorP2[2] * 255));
        bgColorPicker = new ColorPicker(SettingsActivity.this, (int) (colorBG[0] * 255), (int) (colorBG[1] * 255), (int) (colorBG[2] * 255));
        p1Color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p1ColorPicker.show();
            }
        });
        p2Color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p2ColorPicker.show();
            }
        });
        bgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgColorPicker.show();
            }
        });
        if(0.2126 * colorP1[0] + 0.7152 * colorP1[1] + 0.0722 * colorP1[2]>0.179){
            p1Color.setTextColor(Color.BLACK);
        }
        else{
            p1Color.setTextColor(Color.WHITE);
        }
        if(0.2126 * colorP2[0] + 0.7152 * colorP2[1] + 0.0722 * colorP2[2]>0.179){
            p2Color.setTextColor(Color.BLACK);
        }
        else{
            p2Color.setTextColor(Color.WHITE);
        }
        if(0.2126 * colorBG[0] + 0.7152 * colorBG[1] + 0.0722 * colorBG[2]>0.179){
            bgColor.setTextColor(Color.BLACK);
        }
        else{
            bgColor.setTextColor(Color.WHITE);
        }
        p1ColorPicker.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                colorP1[0] = ((float) Color.red(color)) / 255;
                colorP1[1] = ((float) Color.green(color)) / 255;
                colorP1[2] = ((float) Color.blue(color)) / 255;
                mGLView.requestRender();
                SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("colorP1", color);
                editor.commit();
                p1Color.setBackgroundColor(sharedPref.getInt("colorP1",Color.BLUE));
                if(0.2126 * colorP1[0] + 0.7152 * colorP1[1] + 0.0722 * colorP1[2]>0.179){
                    p1Color.setTextColor(Color.BLACK);
                }
                else{
                    p1Color.setTextColor(Color.WHITE);
                }
                p1ColorPicker.hide();
            }
        });
        p2ColorPicker.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                colorP2[0] = ((float) Color.red(color)) / 255;
                colorP2[1] = ((float) Color.green(color)) / 255;
                colorP2[2] = ((float) Color.blue(color)) / 255;
                mGLView.requestRender();
                SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("colorP2", color);
                editor.commit();
                p2Color.setBackgroundColor(sharedPref.getInt("colorP2",Color.RED));
                if(0.2126 * colorP2[0] + 0.7152 * colorP2[1] + 0.0722 * colorP2[2]>0.179){
                    p2Color.setTextColor(Color.BLACK);
                }
                else{
                    p2Color.setTextColor(Color.WHITE);
                }
                p2ColorPicker.hide();
            }
        });
        bgColorPicker.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                colorBG[0] = ((float) Color.red(color)) / 255;
                colorBG[1] = ((float) Color.green(color)) / 255;
                colorBG[2] = ((float) Color.blue(color)) / 255;

                //mGLView.setBackgroundColor(color);
                mGLView.refreshDrawableState();
                mGLView.requestRender();
                SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("colorBG", color);
                editor.commit();
                bgColor.setBackgroundColor(sharedPref.getInt("colorBG",Color.BLACK));
                if(0.2126 * colorBG[0] + 0.7152 * colorBG[1] + 0.0722 * colorBG[2]>0.179){
                    bgColor.setTextColor(Color.BLACK);
                }
                else{
                    bgColor.setTextColor(Color.WHITE);
                }
                bgColorPicker.hide();
            }
        });

        toggleMusic = (CheckBox) findViewById(R.id.checkMusicBox);

        toggleMusic.setChecked(sharedPref.getBoolean("musicToggle",true));
        toggleMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("musicToggle", isChecked);
                editor.commit();
                if(isChecked) {
                    BackgroundSoundService.player.start();
                }
                else{
                    BackgroundSoundService.player.pause();
                }
            }
        });
        volumeBar = (DiscreteSeekBar) findViewById(R.id.volumeBar);
        volumeBar.setTrackColor(Color.BLUE);
        volumeBar.setScrubberColor(Color.BLUE);
        volumeBar.setThumbColor(Color.BLUE, Color.BLUE);
        volumeBar.setProgress(sharedPref.getInt("musicVolume",80));
        volumeButton = (Button)findViewById(R.id.setVolumeButton);
        volumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("musicVolume", volumeBar.getProgress());
                editor.commit();
                BackgroundSoundService.setVolume(volumeBar.getProgress());
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (BackgroundSoundService.player != null) {
            BackgroundSoundService.player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BackgroundSoundService.player != null) {
            BackgroundSoundService.player.start();
        }
    }

    class MyGLSurfaceView extends GLSurfaceView {

        public final SettingsRenderer mRenderer;

        public float getScreenHeight() {
            return mRenderer.getScreenHeight();
        }

        public float getScreenWidth() {
            return mRenderer.getScreenWidth();
        }

        public SettingsRenderer getRenderer() {
            return mRenderer;
        }

        public MyGLSurfaceView(Context context) {
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            mRenderer = new SettingsRenderer(context, "");

            // Set the Renderer for drawing on the GLSurfaceView

            setRenderer(mRenderer);
            //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);


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
