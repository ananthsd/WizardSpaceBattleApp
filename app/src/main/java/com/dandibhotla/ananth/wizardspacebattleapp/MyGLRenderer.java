package com.dandibhotla.ananth.wizardspacebattleapp;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by Ananth on 7/22/2017.
 */

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Hat mHat, mHat2;
    private Robe robe, robe2;
    private Square mSquare, mSquare2;
    private float screenWidth, screenHeight;
    private Player player1, player2;
    private int pixelWidth, pixelheight;
    private Context context;
    private Circle p1Eye,p2Eye;
    public MyGLRenderer(Context context) {
        player1 = new Player(context, Player.PLAYER_ONE_START);
        player2 = new Player(context, Player.PLAYER_TWO_START);

    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig config) {
        // Set the background frame color
        //  GLES20.glClearColor(0.25f, 0.93f, 0.36f, 1.0f);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // initialize a triangle
        mHat = new Hat();
        robe = new Robe();
        // initialize a square
        mSquare = new Square(Square.COLOR_BLUE);

        mHat2 = new Hat();
        robe2 = new Robe();
        // initialize a square
        mSquare2 = new Square(Square.COLOR_RED);
        p1Eye = new Circle(-0.07f,0.02f);
        p2Eye = new Circle(0.07f,0.02f);
    }

    private float[] mRotationMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        // Create a rotation transformation for the triangle
        float xTranslate = screenWidth;
        //  Matrix.translateM(mMVPMatrix, 0, xTranslate, 0, 0); // apply translation
        //  Matrix.translateM(mMVPMatrix, 0, -0.2f, 0, 0); // apply translation
      //  Log.v("movementGuy", "X: " + (float) (player1.getxLoc() / ((double) pixelWidth)) + "; Y:" + (float) (player1.getyLoc() / ((double) pixelheight)));
        //   Matrix.translateM(mMVPMatrix, 0, (float) (player1.getxLoc()/((double)pixelWidth)), (float) (player1.getyLoc()/((double)pixelheight)), 0); // apply translation
        Matrix.translateM(mMVPMatrix, 0, player1.getxLoc(), player1.getyLoc(), 0); // apply translation
        // Matrix.scaleM(mMVPMatrix, 0, 0.75f, 0.75f, 1);
        mSquare.draw(mMVPMatrix);
        mHat.draw(mMVPMatrix);
        if (player1.getxLoc() < 0) {
            robe.draw(mMVPMatrix, Robe.RIGHT_ROBE);
        } else {
            robe.draw(mMVPMatrix, Robe.LEFT_ROBE);
        }
        p1Eye.draw(mMVPMatrix);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix2, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.translateM(mMVPMatrix2, 0, player2.getxLoc(), player2.getyLoc(), 0);
        mSquare2.draw(mMVPMatrix2);
        mHat2.draw(mMVPMatrix2);
        if (player2.getxLoc() < 0) {
            robe2.draw(mMVPMatrix2, Robe.RIGHT_ROBE);
        } else {
            robe2.draw(mMVPMatrix2, Robe.LEFT_ROBE);
        }
        p2Eye.draw(mMVPMatrix2);
        // Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        // Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        // Matrix.translateM(scratch,0, -1.0f, 0, 0);
        // Draw shape
        // mHat.draw(scratch);
    }

    private final float[] mMVPMatrix = new float[16];
    private final float[] mMVPMatrix2 = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        screenWidth = ratio;
        screenHeight = 1 / ratio;
        pixelheight = height;
        pixelWidth = width;
        Log.v("screenstuff", ratio + "");
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
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

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
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