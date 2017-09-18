package com.dandibhotla.ananth.wizardspacebattleapp;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by Ananth on 7/22/2017.
 */

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Hat mHat, mHat2;
    private Robe robe, robe2;
    private Square mSquare, mSquare2, test;
    private float screenWidth, screenHeight;
    private Player player1, player2;
    private int pixelWidth, pixelheight;
    private Context context;
    private Circle p1Eye, p2Eye, p1JoyStick, p2JoyStick;
    private BulletObject bulletObject;
    private int counter,alternateCounter=0;
    private ArrayList<Collision> collisions;
    private CollisionEffect collisionEffect;
    //private volatile Queue<Bullet> queuedBullets;
    private final int queueMin= 20;
    FPSCounter fpsCounter ;
    public MyGLRenderer(Context context, TextView score1, TextView score2, TextView health1, TextView health2) {
        player1 = new Player(context, Player.PLAYER_ONE, score1, health1);
        player2 = new Player(context, Player.PLAYER_TWO, score2, health2);
        collisions = new ArrayList<>();
        this.context = context;
        //queuedBullets = new ConcurrentLinkedQueue<>();
        fpsCounter = new FPSCounter();
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        //  GLES20.glClearColor(0.25f, 0.93f, 0.36f, 1.0f);
        //Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
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
        //test = new Square(Player.COLOR_RED);
        float[] colorWhite = {1f, 1f, 1f, 1.0f};
        p1Eye = new Circle(colorWhite);
        p2Eye = new Circle(colorWhite);


        p1JoyStick = new Circle(Arrays.copyOf(Player.colorP1, 4));
        p2JoyStick = new Circle(Arrays.copyOf(Player.colorP2, 4));

        bulletObject = new BulletObject();

        collisionEffect = new CollisionEffect();

    }

    private float[] mRotationMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 unused) {
        //Checks if bullets are out of bounds
        //Log.v("positions",GameScreen.mPreviousX+";"+GameScreen.mPreviousY);
        if(GameScreen.p1Touch){
            player1.move();
        }
        if(GameScreen.p2Touch){
            player2.move();
        }
        for (int i = player1.getBullets().size() - 1; i >= 0; i--) {
            Bullet bullet = player1.getBullets().get(i);
            if (bullet.outOfBounds()) {
                player1.getBullets().remove(i);
                //queuedBullets.add(bullet);
            }
        }
        for (int i = player2.getBullets().size() - 1; i >= 0; i--) {
            Bullet bullet = player2.getBullets().get(i);
            if (bullet.outOfBounds()) {
                player2.getBullets().remove(i);
                //queuedBullets.add(bullet);
            }
        }

        //float[] scratch = new float[16];
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);


        if (GameScreen.p2Touch) {
            Matrix.multiplyMM(joyStickMatrix2, 0, mProjectionMatrix, 0, mViewMatrix, 0);

            Matrix.translateM(joyStickMatrix2, 0, GameScreen.getmPreviousX2Float(), GameScreen.getmPreviousY2Float(), 0); // apply translation

            p2JoyStick.draw(joyStickMatrix2, "", true, false);
        } else {
            p2JoyStick.resetColor();
        }

        if (GameScreen.p1Touch) {
            Matrix.multiplyMM(joyStickMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

            Matrix.translateM(joyStickMatrix, 0, GameScreen.getmPreviousXFloat(), GameScreen.getmPreviousYFloat(), 0); // apply translation

            p1JoyStick.draw(joyStickMatrix, "", true, false);
        } else {
            p1JoyStick.resetColor();
        }


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
        if (player1.getxLoc() < player2.getxLoc()) {
            robe.draw(mMVPMatrix, Robe.RIGHT_ROBE);
            p1Eye.draw(mMVPMatrix, Circle.FACE_LEFT, true, true);
        } else {
            robe.draw(mMVPMatrix, Robe.LEFT_ROBE);
            p1Eye.draw(mMVPMatrix, Circle.FACE_RIGHT, true, true);
        }

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix2, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.translateM(mMVPMatrix2, 0, player2.getxLoc(), player2.getyLoc(), 0);
        mSquare2.draw(mMVPMatrix2);
        mHat2.draw(mMVPMatrix2);
        counter++;
        alternateCounter++;
        int counterInterval = 1;
        int counterInterval2 = 5;
       // Log.v("queue",queuedBullets.size()+"");
        if (player2.getxLoc() < player1.getxLoc()) {
            robe2.draw(mMVPMatrix2, Robe.RIGHT_ROBE);
            p2Eye.draw(mMVPMatrix2, Circle.FACE_LEFT, true, true);
            if (counter == counterInterval) {
               /* Bullet b = queuedBullets.poll();
                if(b!=null&&queuedBullets.size()>queueMin){
                    //b.reset(player1.getxLocPx(),player1.getyLocPx(),Bullet.LEFT_FACING,Player.PLAYER_ONE);
                    player1.resetBullet(b,Bullet.LEFT_FACING,mProjectionMatrix,mViewMatrix);
                    player1.getBullets().add(b);
                    if(alternateCounter==counterInterval2){
                     //   player1.addBullet(Bullet.LEFT_FACING, mProjectionMatrix, mViewMatrix);
                    }

                }
                else {
                    //Log.v("queueGen","generated");
                    player1.addBullet(Bullet.LEFT_FACING, mProjectionMatrix, mViewMatrix);
                }

                b = queuedBullets.poll();
                if(b!=null&&queuedBullets.size()>queueMin){
                    //b.reset(player2.getxLocPx(),player2.getyLocPx(),Bullet.RIGHT_FACING,Player.PLAYER_TWO);
                    player2.resetBullet(b,Bullet.RIGHT_FACING,mProjectionMatrix,mViewMatrix);
                    player2.getBullets().add(b);
                    if(alternateCounter==counterInterval2){
                     //   player2.addBullet(Bullet.RIGHT_FACING, mProjectionMatrix, mViewMatrix);
                    }

                }
                else {
                   // Log.v("queueGen","generated");

                    player2.addBullet(Bullet.RIGHT_FACING, mProjectionMatrix, mViewMatrix);
                }
                if(alternateCounter==5) {
                    alternateCounter = 0;
                }*/
                player1.addBullet(Player.LEFT_FACING, mProjectionMatrix, mViewMatrix);
                player2.addBullet(Player.RIGHT_FACING, mProjectionMatrix, mViewMatrix);
                counter = 0;
            }
        } else {
            robe2.draw(mMVPMatrix2, Robe.LEFT_ROBE);
            p2Eye.draw(mMVPMatrix2, Circle.FACE_RIGHT, true, true);
            if (counter == counterInterval) {
                /*Bullet b = queuedBullets.poll();
                if(b!=null&&queuedBullets.size()>queueMin){
                    //b.reset(player1.getxLocPx(),player1.getyLocPx(),Bullet.RIGHT_FACING,Player.PLAYER_ONE);
                    player1.resetBullet(b,Bullet.RIGHT_FACING,mProjectionMatrix,mViewMatrix);
                    player1.getBullets().add(b);
                    if(alternateCounter==counterInterval2){
                      //  player1.addBullet(Bullet.RIGHT_FACING, mProjectionMatrix, mViewMatrix);
                    }
                }
                else {
                    Log.v("queueGen","generated");

                    player1.addBullet(Bullet.RIGHT_FACING, mProjectionMatrix, mViewMatrix);
                }
                b = queuedBullets.poll();
                if(b!=null&&queuedBullets.size()>queueMin){
                   // b.reset(player2.getxLocPx(),player2.getyLocPx(),Bullet.LEFT_FACING,Player.PLAYER_TWO);
                    player2.resetBullet(b,Bullet.LEFT_FACING,mProjectionMatrix,mViewMatrix);
                    player2.getBullets().add(b);
                    if(alternateCounter==counterInterval2){
                     //   player2.addBullet(Bullet.LEFT_FACING, mProjectionMatrix, mViewMatrix);
                    }
                }
                else {
                    Log.v("queueGen","generated");

                    player2.addBullet(Bullet.LEFT_FACING, mProjectionMatrix, mViewMatrix);

                }
                alternateCounter= 0;*/
                player1.addBullet(Player.RIGHT_FACING, mProjectionMatrix, mViewMatrix);
                player2.addBullet(Player.LEFT_FACING, mProjectionMatrix, mViewMatrix);
                if (counterInterval==counter) {
                    counter = 0;
                }
            }
        }
        //  Log.v("bullets",player1.getBullets().size()+";"+player1.getBullets().size());

    /*    Matrix.multiplyMM(mMVPMatrix3, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.translateM(mMVPMatrix3, 0, 0, 1f, 0);
        test.draw(mMVPMatrix3);*/
        player1.moveBullets();
        player2.moveBullets();
        //Collision detection with other bullets
        for (int i = player1.getBullets().size() - 1; i >= 0; i--) {
            for (int j = player2.getBullets().size() - 1; j >= 0; j--) {
                if (player1.getBullets().size() > i && player2.getBullets().size() > j) {
                    if (player1.getBullets().get(i).collideDetect(player2.getBullets().get(j))) {
                        //  Log.v("collide","p1:x:"+player1.getBullets().get(i).getxLoc()+";y:"+player1.getBullets().get(i).getyLoc()+"p2:x:"+player2.getBullets().get(i).getxLoc()+";y:"+player2.getBullets().get(i).getyLoc());
//                        collisions.add(new Collision((player1.getBullets().get(i).getxLoc()+player2.getBullets().get(i).getxLoc())/2,(player1.getBullets().get(i).getyLoc()+player2.getBullets().get(i).getyLoc())/2,mProjectionMatrix,mViewMatrix));
                        try {
                            //queuedBullets.add(player1.getBullets().get(i));
                            //queuedBullets.add(player2.getBullets().get(i));
                        }catch (IndexOutOfBoundsException e){
                        Log.v("error",e.getMessage());
                        }
                        player1.getBullets().remove(i);
                        player2.getBullets().remove(j);

                    }
                }
            }
        }
        bulletObject.draw(player1.getBullets());
        bulletObject.draw(player2.getBullets());
        //collisionEffect.draw(collisions);
        //collisions.removeAll(collisions);


        for (int i = player1.getBullets().size() - 1; i >= 0; i--) {
            if (player1.getBullets().size() > i) {
                if (player2.collideDetect(player1.getBullets().get(i))) {
                    player2.damage();
                    //queuedBullets.add(player1.getBullets().get(i));
                    player1.getBullets().remove(i);
                }
            }
        }
        for (int i = player2.getBullets().size() - 1; i >= 0; i--) {
            if (player2.getBullets().size() > i) {
                if (player1.collideDetect(player2.getBullets().get(i))) {
                    player1.damage();
                    //queuedBullets.add(player2.getBullets().get(i));
                    player2.getBullets().remove(i);
                }
            }
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                GameScreen.updateHealth();
                if (player1.getHealth() < 0 || player2.getHealth() < 0) {
                    resetGame();
                }

            }
        });


        // Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        // Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        // Matrix.translateM(scratch,0, -1.0f, 0, 0);
        // Draw shape
        // mHat.draw(scratch);
fpsCounter.logFrame();
    }

    public void resetGame() {
        if(player1.getHealth()>player2.getHealth()){
            player1.increaseScore();
            GameScreen.updateScore1();
        }
        else if(player1.getHealth()<player2.getHealth()){
            player2.increaseScore();
            GameScreen.updateScore2();
        }
        else{
            player1.increaseScore();
            GameScreen.updateScore1();
            player2.increaseScore();
            GameScreen.updateScore2();
        }
        player1.reset();
        player2.reset();
    }

    private final float[] mMVPMatrix = new float[16];
    private final float[] mMVPMatrix2 = new float[16];
    private final float[] mMVPMatrix3 = new float[16];
    private final float[] joyStickMatrix = new float[16];
    private final float[] joyStickMatrix2 = new float[16];
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
        pixelheight = height;
        pixelWidth = width;
        // Log.v("screenstuff", ratio + ";"+widthPixels/heightPixels);
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        // Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
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

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

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