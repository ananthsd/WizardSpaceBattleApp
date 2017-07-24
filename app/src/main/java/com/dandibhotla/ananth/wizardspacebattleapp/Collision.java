package com.dandibhotla.ananth.wizardspacebattleapp;

import android.opengl.Matrix;

/**
 * Created by Ananth on 7/24/2017.
 */

public class Collision {
    private float xLoc,yLoc;
    private float[] mvpMatrix;
    public float getxLoc() {
        return xLoc;
    }

    public float getyLoc() {
        return yLoc;
    }

    public Collision(float xLoc, float yLoc, float[] projectionM, float[] viewM){
        this.xLoc = xLoc;

        this.yLoc = yLoc;
        mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, projectionM, 0, viewM, 0);
        Matrix.translateM(mvpMatrix, 0, getxLoc(), getyLoc(), 0);
    }

    public float[] getMvpMatrix() {
        return mvpMatrix;
    }
}
