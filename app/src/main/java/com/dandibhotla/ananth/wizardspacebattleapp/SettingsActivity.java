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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends Activity {
    private RecyclerView settingsRecyclerView;
    private static MyGLSurfaceView mGLView;
    private static double widthPixels, heightPixels;

    private static RelativeLayout leftLayout, rightLayout, infoLayout;
    private List<String> settingsList;
    private LinearLayout glLinearLayout;
    private Button p1Color,p2Color,bgColor;
    private ColorPicker p1ColorPicker,p2ColorPicker,bgColorPicker;
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
        widthPixels = display.widthPixels/2;
        heightPixels = display.heightPixels/2;
        //mGLView.getHolder().setFixedSize((int) widthPixels, (int) heightPixels);
        settingsList = new ArrayList<>();
        settingsList.add("Colors");
        settingsList.add("Colors");
        settingsList.add("Colors");
        settingsList.add("Colors");
        settingsRecyclerView = (RecyclerView)findViewById(R.id.settings_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        settingsRecyclerView.setLayoutManager(layoutManager);
        settingsRecyclerView.setAdapter(new SettingsAdapter(this, settingsList));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(settingsRecyclerView.getContext(),
                layoutManager.getOrientation());
        settingsRecyclerView.addItemDecoration(dividerItemDecoration);

        infoLayout = (RelativeLayout) findViewById(R.id.settingInfoLayout);
        p1Color = (Button) infoLayout.findViewById(R.id.p1ColorButton);
        p2Color = (Button) infoLayout.findViewById(R.id.p2ColorButton);
        bgColor = (Button) infoLayout.findViewById(R.id.bgColorButton);
        p1ColorPicker = new ColorPicker(SettingsActivity.this, (int)(Player.colorP1[0]*255), (int)(Player.colorP1[1]*255), (int)(Player.colorP1[2]*255));
        p2ColorPicker = new ColorPicker(SettingsActivity.this, (int)(Player.colorP2[0]*255), (int)(Player.colorP2[1]*255), (int)(Player.colorP2[2]*255));
        bgColorPicker = new ColorPicker(SettingsActivity.this, (int)(Player.colorBG[0]*255), (int)(Player.colorBG[1]*255), (int)(Player.colorBG[2]*255));
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
        p1ColorPicker.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                Player.colorP1[0]= ((float) Color.red(color))/255;
                Player.colorP1[1]= ((float) Color.green(color))/255;
                Player.colorP1[2]= ((float) Color.blue(color))/255;
                mGLView.requestRender();
                SharedPreferences sharedPref = getSharedPreferences("Settings",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("colorP1", color);
                editor.commit();
                p1ColorPicker.hide();
            }
        });
        p2ColorPicker.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                Player.colorP2[0]= ((float) Color.red(color))/255;
                Player.colorP2[1]= ((float) Color.green(color))/255;
                Player.colorP2[2]= ((float) Color.blue(color))/255;
                mGLView.requestRender();
                SharedPreferences sharedPref = getSharedPreferences("Settings",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("colorP2", color);
                editor.commit();
                p2ColorPicker.hide();
            }
        });
        bgColorPicker.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                Player.colorBG[0]= ((float) Color.red(color))/255;
                Player.colorBG[1]= ((float) Color.green(color))/255;
                Player.colorBG[2]= ((float) Color.blue(color))/255;

                //mGLView.setBackgroundColor(color);
                mGLView.refreshDrawableState();
                mGLView.requestRender();
                SharedPreferences sharedPref = getSharedPreferences("Settings",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("colorBG", color);
                editor.commit();
                bgColorPicker.hide();
            }
        });
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

            mRenderer = new SettingsRenderer(context,"");

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
