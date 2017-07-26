package com.dandibhotla.ananth.wizardspacebattleapp;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Created by Ananth on 7/23/2017.
 */

public class Circle {

    private int mProgram, mPositionHandle, mColorHandle, mMVPMatrixHandle;
    private FloatBuffer mVertexBuffer, mVertexBuffer2, mVertexJoystickBuffer;
    private float verticesFaceRight[] = new float[364 * 3];
    private float verticesFaceLeft[] = new float[364 * 3];
    private float verticesJoystick[] = new float[364 * 3];
    float color[] = {1f, 1f, 1f, 1.0f};
    float colorStart[] = {1f, 1f, 1f, 1.0f};
    public static final String FACE_LEFT = "left";
    public static final String FACE_RIGHT = "right";
    private boolean colorUp = false;
    int phases = 100;
    int count = 0;
    float increment = color[0] / phases;
    float increment2 = color[1] / phases;
    float increment3 = color[2] / phases;
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public Circle(float[] color) {
        /*
        p1Eye = new Circle(-0.07f,0.02f);
        p2Eye = new Circle(0.07f,0.02f);
        */


        verticesFaceRight[0] = 0.07f;
        verticesFaceRight[1] = 0.02f;

        verticesFaceRight[0] = -0.07f;
        verticesFaceRight[1] = 0.02f;
        verticesFaceRight[2] = 0;

        for (int i = 1; i < 364; i++) {
            verticesFaceRight[(i * 3) + 0] = (float) (0.02 * Math.cos((3.14 / 180) * (float) i) + verticesFaceRight[0]);
            verticesFaceRight[(i * 3) + 1] = (float) (0.02 * Math.sin((3.14 / 180) * (float) i) + verticesFaceRight[1]);
            verticesFaceRight[(i * 3) + 2] = 0;
        }


        // Log.v("Thread", "" + verticesFaceRight[0] + "," + verticesFaceRight[1] + "," + verticesFaceRight[2]);
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(verticesFaceRight.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexByteBuffer.asFloatBuffer();
        mVertexBuffer.put(verticesFaceRight);
        mVertexBuffer.position(0);

        verticesFaceLeft[0] = 0.07f;
        verticesFaceLeft[1] = 0.02f;
        verticesFaceLeft[2] = 0;

        for (int i = 1; i < 364; i++) {
            verticesFaceLeft[(i * 3) + 0] = (float) (0.02 * Math.cos((3.14 / 180) * (float) i) + verticesFaceLeft[0]);
            verticesFaceLeft[(i * 3) + 1] = (float) (0.02 * Math.sin((3.14 / 180) * (float) i) + verticesFaceLeft[1]);
            verticesFaceLeft[(i * 3) + 2] = 0;
        }


        // Log.v("Thread", "" + verticesFaceLeft[0] + "," + verticesFaceLeft[1] + "," + verticesFaceLeft[2]);
        ByteBuffer vertexByteBuffer2 = ByteBuffer.allocateDirect(verticesFaceLeft.length * 4);
        vertexByteBuffer2.order(ByteOrder.nativeOrder());
        mVertexBuffer2 = vertexByteBuffer2.asFloatBuffer();
        mVertexBuffer2.put(verticesFaceLeft);
        mVertexBuffer2.position(0);


        verticesJoystick[0] = 0f;
        verticesJoystick[1] = 0f;
        verticesJoystick[2] = 0f;

        for (int i = 1; i < 364; i++) {
            verticesJoystick[(i * 3) + 0] = (float) (0.05 * Math.cos((3.14 / 180) * (float) i) + verticesJoystick[0]);
            verticesJoystick[(i * 3) + 1] = (float) (0.05 * Math.sin((3.14 / 180) * (float) i) + verticesJoystick[1]);
            verticesJoystick[(i * 3) + 2] = 0;
        }


        // Log.v("Thread", "" + verticesJoystick[0] + "," + verticesJoystick[1] + "," + verticesJoystick[2]);
        ByteBuffer vertexByteBuffer3 = ByteBuffer.allocateDirect(verticesJoystick.length * 4);
        vertexByteBuffer3.order(ByteOrder.nativeOrder());
        mVertexJoystickBuffer = vertexByteBuffer3.asFloatBuffer();
        mVertexJoystickBuffer.put(verticesJoystick);
        mVertexJoystickBuffer.position(0);


        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);
        this.color = color;
        colorStart = Arrays.copyOf(color, 4);
    }

    public static int loadShader(int type, String shaderCode) {

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void resetColor() {
        color[0] = colorStart[0];
        color[1] = colorStart[1];
        color[2] = colorStart[2];
        colorUp=false;
        count=0;
    }

    public void draw(float[] mvpMatrix, String position, boolean filled, boolean eye) {

        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle verticesFaceRight
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        if (eye) {
            if (position.equals(FACE_RIGHT)) {
                GLES20.glVertexAttribPointer(mPositionHandle, 3,
                        GLES20.GL_FLOAT, true, 12
                        , mVertexBuffer);

            } else {
                GLES20.glVertexAttribPointer(mPositionHandle, 3,
                        GLES20.GL_FLOAT, true, 12
                        , mVertexBuffer2);
            }
        } else {
            GLES20.glVertexAttribPointer(mPositionHandle, 3,
                    GLES20.GL_FLOAT, true, 12
                    , mVertexJoystickBuffer);


            // Log.v("colorUp",colorUp+"");
            if (count == phases) {
                count = 0;
                colorUp = !colorUp;

            }
            if (colorUp) {
                color[0] += increment;
                color[1] += increment2;
                color[2] += increment3;
            } else {
                color[0] -= increment;
                color[1] -= increment2;
                color[2] -= increment3;
            }
            count++;
/*            if(color[0]-increment<0){
                colorUp1 = true;
            }
            if(color[1]-increment<0){
                colorUp2 = true;
            }
            if(color[2]-increment<0){
                colorUp3 = true;
            }
            Log.v("colorStuff",(color[2]+increment>colorStart[2])+"");

            if(color[0] + increment > colorStart[0]){
                colorUp1 = false;
            }
            if(color[1] + increment > colorStart[1]){
                colorUp2 = false;
            }
            if(color[2] + increment > colorStart[2]){
                colorUp3 = false;
            }

          //  Log.v("colorUp",colorUp+"");
            if(colorUp1&&colorUp2&&colorUp3){
                if(color[0] + increment <= colorStart[0]){
                    color[0]+=increment;
                }
                else{
                    color[0]=colorStart[0];
                }
                if(color[1] + increment <= colorStart[1]){
                    color[1]+=increment;
                }
                else{
                    color[1]=colorStart[1];
                }
                if(color[2] + increment <= colorStart[2]){
                    color[2]+=increment;
                }
                else{
                    color[2]=colorStart[2];
                }
            }
            else{
                if(color[0] - increment >= 0.01){
                    color[0]-=increment;
                }
                else{
                    color[0]=0;
                }
                if(color[1] - increment >= 0.01){
                    color[1]+=increment;
                }
                else{
                    color[1]=0;
                }
                if(color[2] - increment >= 0.01){
                    color[2]+=increment;
                }
                else{
                    color[2]=0;
                }
            }*/

            Log.v("color", color[0] + "," + color[1] + "," + color[2] + "," + color[3]);
            Log.v("colorStart", colorStart[0] + "," + colorStart[1] + "," + colorStart[2] + "," + colorStart[3]);
        }
        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");


        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);


        // Draw the triangle
        if (filled) {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 364);
        } else {
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, 364);
        }

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }

}