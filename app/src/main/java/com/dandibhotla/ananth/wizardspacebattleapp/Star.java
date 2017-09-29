package com.dandibhotla.ananth.wizardspacebattleapp;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Ananth on 9/28/2017.
 */

public class Star {

    private int mProgram, mPositionHandle, mColorHandle, mMVPMatrixHandle;
    private FloatBuffer mVertexBuffer;
    private float vertices[] = new float[364 * 3];
    private float[] mMVPMatrix = new float[16];
    private float[] lineMatrix;
    float color[] = {0f, 0f, 0f, 1f};
    private float verticalDisplacement;
    private float verticalStep = .005f;
    private Line trail;
    private float initialX, initialY,currentX,currentY;
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
    public float getDisplacement(){
        return verticalDisplacement;
    }
public void move(){
    Matrix.translateM(mMVPMatrix, 0, -verticalStep/2, -verticalStep, 0);
    verticalDisplacement+=verticalStep;
    currentX-=verticalStep/2;
    currentY-=verticalStep;
    trail.setCoords(initialX,initialY,currentX,currentY);
}
    public Star(float[] color,float[] matrix, float[] lineMatrix, float initialX, float initialY) {
        this.initialX = initialX;
        this.initialY = initialY;
        this.currentX = initialX;
        this.currentY = initialY;
        vertices[0] = 0.07f;
        vertices[1] = 0.02f;

        vertices[0] = 0.07f;
        vertices[1] = 0.02f;
        vertices[2] = 0;

        for (int i = 1; i < 364; i++) {
            vertices[(i * 3) + 0] = (float) (0.005 * Math.cos((3.14 / 180) * (float) i) + vertices[0]);
            vertices[(i * 3) + 1] = (float) (0.005 * Math.sin((3.14 / 180) * (float) i) + vertices[1]);
            vertices[(i * 3) + 2] = 0;
        }


        // Log.v("Thread", "" + verticesFaceRight[0] + "," + verticesFaceRight[1] + "," + verticesFaceRight[2]);
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexByteBuffer.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);
        this.color = color;

        mMVPMatrix = matrix;
        this.lineMatrix = lineMatrix;
        trail = new Line();
        trail.setLineWidth(3f);
    }

    public static int loadShader(int type, String shaderCode) {

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }



    public void draw() {

        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle verticesFaceRight
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
                GLES20.glVertexAttribPointer(mPositionHandle, 3,
                        GLES20.GL_FLOAT, true, 12
                        , mVertexBuffer);


        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");


        // Set colorWhite for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);


        // Draw the triangle

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 364);


        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        //trail.draw(lineMatrix,color);

    }

}