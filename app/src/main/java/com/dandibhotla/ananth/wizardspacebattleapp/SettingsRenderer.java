package com.dandibhotla.ananth.wizardspacebattleapp;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.DisplayMetrics;

import javax.microedition.khronos.opengles.GL10;


/**
 * Created by Ananth on 7/22/2017.
 */

public class SettingsRenderer implements GLSurfaceView.Renderer {
    private Hat mHat,mHat2;
    private Robe robe,robe2;
    private Square mSquare,mSquare2;
    private float screenWidth, screenHeight;
    private Context context;
    private Circle p1Eye, p2Eye;
    private String displayType;
    private int scaler = 3;
    public SettingsRenderer(Context context, String displayType) {
        this.displayType = displayType;
        this.context = context;
    }
    public void setDisplayType(String displayType){
        this.displayType = displayType;
    }


    @Override
    public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig config) {
        // Set the background frame color
        //  GLES20.glClearColor(0.25f, 0.93f, 0.36f, 1.0f);
        GLES20.glClearColor(Player.colorBG[0], Player.colorBG[1], Player.colorBG[2], 1.0f);
        // initialize a triangle
        mHat = new Hat();
        robe = new Robe();
        // initialize a square
        mSquare = new Square(Player.PLAYER_ONE);
        mHat2 = new Hat();
        robe2 = new Robe();
        // initialize a square
        mSquare2 = new Square(Player.PLAYER_TWO);



        float[] colorWhite = {1f, 1f, 1f, 1.0f};
        p1Eye = new Circle(colorWhite);
        p2Eye = new Circle(colorWhite);

    }

    private float[] mRotationMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 unused) {
        //Checks if bullets are out of bounds
        //Log.v("positions",GameScreen.mPreviousX+";"+GameScreen.mPreviousY);


        //float[] scratch = new float[16];
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(Player.colorBG[0], Player.colorBG[1], Player.colorBG[2], 1.0f);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        Matrix.translateM(mMVPMatrix, 0, getScreenWidth()/2, -getScreenHeight()/2, 0); // apply translation
        Matrix.scaleM(mMVPMatrix,0,scaler,scaler,scaler);
        mSquare.draw(mMVPMatrix,Player.colorP1);
        mHat.draw(mMVPMatrix);

            robe.draw(mMVPMatrix, Robe.LEFT_ROBE);
            p1Eye.draw(mMVPMatrix, Circle.FACE_RIGHT, true, true);

        Matrix.multiplyMM(mMVPMatrix2, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.translateM(mMVPMatrix2, 0, -getScreenWidth()/2, -getScreenHeight()/2, 0);
        Matrix.scaleM(mMVPMatrix2,0,scaler,scaler,scaler);
        mSquare2.draw(mMVPMatrix2,Player.colorP2);
        mHat2.draw(mMVPMatrix2);

        robe2.draw(mMVPMatrix2, Robe.RIGHT_ROBE);
        p2Eye.draw(mMVPMatrix2, Circle.FACE_LEFT, true, true);
    }



    private final float[] mMVPMatrix = new float[16];
    private final float[] mMVPMatrix2 = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        float widthPixels = display.widthPixels;
        float heightPixels = display.heightPixels;
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        screenWidth = ratio;
        screenHeight = ((float) height) / width;
        Matrix.frustumM(mProjectionMatrix, 0, -widthPixels / heightPixels, widthPixels / heightPixels, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public volatile float mAngle;



    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            // Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public float getScreenWidth() {
        return screenWidth;
    }

    public float getScreenHeight() {
        return screenHeight;
    }
}