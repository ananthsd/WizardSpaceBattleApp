package com.dandibhotla.ananth.wizardspacebattleapp;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Ananth on 7/23/2017.
 */

public class Circle {

    private  int mProgram, mPositionHandle, mColorHandle, mMVPMatrixHandle ;
    private FloatBuffer mVertexBuffer,mVertexBuffer2;
    private float verticesFaceRight[] = new float[364 * 3];
    private float verticesFaceLeft[] = new float[364 * 3];
    float color[] = { 1f, 1f, 1f, 1.0f };
    public static final String FACE_LEFT = "left";
    public static final String FACE_RIGHT = "right";

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

    public Circle(){
        /*
        p1Eye = new Circle(-0.07f,0.02f);
        p2Eye = new Circle(0.07f,0.02f);
        */


            verticesFaceRight[0] = 0.07f;
            verticesFaceRight[1] = 0.02f;

        verticesFaceRight[0] = -0.07f;
        verticesFaceRight[1] = 0.02f;
        verticesFaceRight[2] = 0;

        for(int i =1; i <364; i++){
            verticesFaceRight[(i * 3)+ 0] = (float) (0.02 * Math.cos((3.14/180) * (float)i ) + verticesFaceRight[0]);
            verticesFaceRight[(i * 3)+ 1] = (float) (0.02 * Math.sin((3.14/180) * (float)i ) + verticesFaceRight[1]);
            verticesFaceRight[(i * 3)+ 2] = 0;
        }


        Log.v("Thread",""+ verticesFaceRight[0]+","+ verticesFaceRight[1]+","+ verticesFaceRight[2]);
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(verticesFaceRight.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexByteBuffer.asFloatBuffer();
        mVertexBuffer.put(verticesFaceRight);
        mVertexBuffer.position(0);

        verticesFaceLeft[0] = 0.07f;
        verticesFaceLeft[1] = 0.02f;
        verticesFaceLeft[2] = 0;

        for(int i =1; i <364; i++){
            verticesFaceLeft[(i * 3)+ 0] = (float) (0.02 * Math.cos((3.14/180) * (float)i ) + verticesFaceLeft[0]);
            verticesFaceLeft[(i * 3)+ 1] = (float) (0.02 * Math.sin((3.14/180) * (float)i ) + verticesFaceLeft[1]);
            verticesFaceLeft[(i * 3)+ 2] = 0;
        }


        Log.v("Thread",""+ verticesFaceLeft[0]+","+ verticesFaceLeft[1]+","+ verticesFaceLeft[2]);
        ByteBuffer vertexByteBuffer2 = ByteBuffer.allocateDirect(verticesFaceLeft.length * 4);
        vertexByteBuffer2.order(ByteOrder.nativeOrder());
        mVertexBuffer2 = vertexByteBuffer2.asFloatBuffer();
        mVertexBuffer2.put(verticesFaceLeft);
        mVertexBuffer2.position(0);


        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);

    }

    public static int loadShader(int type, String shaderCode){

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


    public void draw (float[] mvpMatrix,String position){

        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle verticesFaceRight
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        if(position.equals(FACE_RIGHT)) {
            GLES20.glVertexAttribPointer(mPositionHandle, 3,
                    GLES20.GL_FLOAT, false, 12
                    , mVertexBuffer);
        }
        else{
            GLES20.glVertexAttribPointer(mPositionHandle, 3,
                    GLES20.GL_FLOAT, false, 12
                    , mVertexBuffer2);
        }

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");



        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);



        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 364);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }

}