package com.dandibhotla.ananth.wizardspacebattleapp;

import android.content.Context;
import android.opengl.Matrix;
import android.util.DisplayMetrics;

/**
 * Created by Ananth on 7/23/2017.
 */

public class Bullet {
    private Context context;
    private String direction, color;
    private double width, height;
    private volatile int xLoc, yLoc;
    public static final String LEFT_FACING = "left", RIGHT_FACING = "right";
    private float[] mvpMatrix,projectionM,viewM;
    private int widthPixels,heightPixels;
    public Bullet(Context context, String direction, String color, int x, int y, float[] projectionM, float[] viewM) {
        this.context = context;
        this.direction = direction;
        this.color = color;
        DisplayMetrics display = context.getResources().getDisplayMetrics();
         widthPixels = display.widthPixels;
         heightPixels = display.heightPixels;
        width = ((double) widthPixels) / heightPixels;
        height = ((double) heightPixels) / widthPixels;
        xLoc = x;
        yLoc = y;
        mvpMatrix = new float[16];
        this.projectionM = projectionM;
        this.viewM = viewM;
        Matrix.multiplyMM(mvpMatrix, 0, projectionM, 0, viewM, 0);
        Matrix.translateM(mvpMatrix, 0, getxLoc(), getyLoc(), 0);
        //Log.v("dimen","w:"+widthPixels+"; h:"+heightPixels);
    }

    public void move() {
        if (direction.equals(LEFT_FACING)) {
            xLoc-=0.015*1000;
            Matrix.translateM(mvpMatrix, 0, -0.015f, 0, 0);

        } else {
            xLoc+=0.015*1000;
            Matrix.translateM(mvpMatrix, 0, 0.015f, 0, 0);
        }
    }

    public float getxLoc() {
        return (float) xLoc / 1000;
    }

    public float getyLoc() {
        return (float) yLoc / 1000;
    }

    public float[] getMvpMatrix() {
        return mvpMatrix;
    }

    public String getColor() {
        return color;
    }

    public String getDirection() {
        return direction;
    }
    public boolean outOfBounds(){
      //  Log.v("xLoc",xLoc+"");
       if(xLoc>width*1000||xLoc<-width*1000){
            return true;
        }
        return false;
    }
    public boolean collideDetect(Bullet b){
        if(Math.abs(b.getxLoc()-getxLoc())<0.02f&&Math.abs(b.getyLoc()-getyLoc())<0.02f){
            return true;
        }
        return false;
    }
}
