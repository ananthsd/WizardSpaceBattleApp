package com.dandibhotla.ananth.wizardspacebattleapp;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.DisplayMetrics;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ananth on 9/28/2017.
 */

public class HomeMenuRenderer implements GLSurfaceView.Renderer {
        private float screenWidth, screenHeight;
        private Context context;
        private ArrayList<Star> stars;
        private ArrayList<Line> lines;
        private int scaler = 3;
    private boolean red2Green,green2Blue,blue2Red;
    private double ratioCounter=1;
    private int count = 0;
    private int colorType;
        public HomeMenuRenderer(Context context) {
            this.context = context;
            stars = new ArrayList<>();
            lines = new ArrayList<>();
            red2Green =true;
            green2Blue = false;
            blue2Red=false;
        }

        @Override
        public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig config) {
            // Set the background frame colorWhite
            //  GLES20.glClearColor(0.25f, 0.93f, 0.36f, 1.0f);
            GLES20.glClearColor(1f, 1f, 1f, 1.0f);

            //float[] colorWhite = {1f, 1f, 1f, 1.0f};
            //p1Eye = new Circle(colorWhite);

        }


        @Override
        public void onDrawFrame(GL10 unused) {
            //Checks if bullets are out of bounds
            //Log.v("positions",GameScreen.mPreviousX+";"+GameScreen.mPreviousY);


            //float[] scratch = new float[16];
            // Redraw background colorWhite
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glClearColor(1f, 1f, 1f, 1.0f);
            // Set the camera position (View matrix)
            if(count ==5) {
                addLines();
                count = 0;
            }

            for(int i = 0; i < stars.size(); i++){
                stars.get(i).draw();
                stars.get(i).move();
                if(stars.get(i).getDisplacement()>=1f){
                    stars.remove(i);
                    i--;
                }
            }
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            Matrix.multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
            for(int i = 0; i < lines.size(); i++){
                lines.get(i).draw(MVPMatrix);
                if(lines.size()>0&&lines.get(i).move()){
                    if(lines.size()>0) {
                        lines.remove(i);
                        i--;
                    }
                }

            }
        count++;

        }
        private int getNewColor(double ratio, int Color1, int Color2){
            int red = (int)Math.abs((ratio * Color.red(Color2)) + ((1 - ratio) * Color.red(Color1)));
            int green = (int)Math.abs((ratio * Color.green(Color2)) + ((1 - ratio) * Color.green(Color1)));
            int blue = (int)Math.abs((ratio * Color.blue(Color2)) + ((1 - ratio) * Color.blue(Color1)));
            return Color.argb(1,red,green,blue);
        }
    private float[] MVPMatrix = new float[16];

        public void addLines(){

           /* float xStart = ((float)Math.random()*(screenWidth*2))-screenWidth*1.0f;
            //Log.v("lines",xStart+"");
            float yStart = 1;
            float[] mMVPMatrix = new float[16];
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            // Calculate the projection and view transformation
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
//((float)Math.random()*(screenWidth*2))-screenWidth

            Matrix.translateM(mMVPMatrix, 0, xStart, yStart, 0); // apply translation
            Matrix.scaleM(mMVPMatrix,0,scaler,scaler,scaler);*/
            ratioCounter-=0.05;
            if(ratioCounter<0){
                ratioCounter=1;
                colorType++;
                if(colorType==7){
                    colorType=0;
                }
                if(red2Green){
                    red2Green=false;
                    green2Blue= true;
                }
                else if(green2Blue){
                    green2Blue = false;
                    blue2Red = true;
                }
                else{
                    blue2Red = false;
                    red2Green = true;
                }
            }
            int newColor = 0;
            switch (colorType){
                case 0:
                    newColor = getNewColor(ratioCounter,Color.RED,Color.argb(1,255,69,0));
                    break;
                case 1:
                    newColor = getNewColor(ratioCounter,Color.YELLOW,Color.argb(1,255,69,0));
                    break;
                case 2:
                    newColor = getNewColor(ratioCounter,Color.GREEN,Color.YELLOW);
                    break;
                case 3:
                    newColor = getNewColor(ratioCounter,Color.BLUE,Color.GREEN);
                    break;
                case 4:
                    newColor = getNewColor(ratioCounter,Color.argb(1,75,0,130),Color.BLUE);
                    break;
                case 5:
                    newColor = getNewColor(ratioCounter,Color.argb(1,238,130,238),Color.argb(1,75,0,130));
                    break;
                case 6:
                    newColor = getNewColor(ratioCounter,Color.RED,Color.argb(1,238,130,238));
                    break;

            }
/*
            if(red2Green){
                newColor = getNewColor(ratioCounter,Color.RED,Color.GREEN);
            }
            else if(green2Blue){
                newColor = getNewColor(ratioCounter,Color.GREEN,Color.BLUE);
            }
            else{
                newColor = getNewColor(ratioCounter,Color.BLUE,Color.RED);
            }*/
            float[] startColor ={Color.red(newColor)/255.0f,Color.green(newColor)/255.0f,Color.blue(newColor)/255.0f,1f};
            float[] lineMatrix = new float[16];
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            // Calculate the projection and view transformation
            Matrix.multiplyMM(lineMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

            Line line = new Line(lineMatrix,startColor,0,1,true);


            line.setLineWidth(3f);
            lines.add(line);
             line = new Line(lineMatrix,startColor,0,1,false);


            line.setLineWidth(3f);
            lines.add(line);
            //stars.add(new Star(startColor,mMVPMatrix,lineMatrix,xStart,yStart));


        }
        //private final float[] mMVPMatrix = new float[16];
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
        public void clearLines(){
            lines.clear();
        }
}
