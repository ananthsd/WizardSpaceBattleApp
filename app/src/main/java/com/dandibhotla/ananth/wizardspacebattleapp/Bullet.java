package com.dandibhotla.ananth.wizardspacebattleapp;

import android.content.Context;
import android.opengl.Matrix;
import android.util.DisplayMetrics;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;

/**
 * Created by Ananth on 7/23/2017.
 */

public class Bullet {
    private Context context;
    private String direction, color;
    private double width, height;
    private long id;
    public void reset(int xLoc, int yLoc, String direction, String color) {
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        Matrix.multiplyMM(mvpMatrix, 0, projectionM, 0, viewM, 0);
        Matrix.translateM(mvpMatrix, 0, getxLoc(), getyLoc(), 0);
        this.direction = direction;
        this.color = color;
    }
    public long getId(){
        return id;
    }
    public void sendData(GoogleApiClient googleApiClient, String roomID, Participant participant){
        byte[] bulletPos = new byte[5];
        bulletPos[0]=(byte)('B');
        bulletPos[1]=(byte)getxLoc();
        bulletPos[2]=(byte)getyLoc();
        bulletPos[3]=(byte)getId();
        bulletPos[4]=(byte)(getDirection().equals(Player.LEFT_FACING) ? 'L' : 'R');
        Games.RealTimeMultiplayer.sendUnreliableMessage(googleApiClient, bulletPos, roomID,
                participant.getParticipantId());
    }
    private volatile int xLoc, yLoc;

    private float[] mvpMatrix,projectionM,viewM;
    private int widthPixels,heightPixels;
    private double speed;
    public Bullet(Context context, String direction, String color, int x, int y, float[] projectionM, float[] viewM, long id) {
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
        speed = 0.05;
        this.id = id;
    }

    public void move() {
        if (direction.equals(Player.LEFT_FACING)) {
            xLoc-=speed*1000;
            Matrix.translateM(mvpMatrix, 0, -(float)speed, 0, 0);

        } else {
            xLoc+=speed*1000;
            Matrix.translateM(mvpMatrix, 0, (float)speed, 0, 0);
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
        if(Math.abs(b.getxLoc()-getxLoc())<0.05f&&Math.abs(b.getyLoc()-getyLoc())<0.02f){
            return true;
        }
        return false;
    }

    public void setxLoc(int xLoc) {
        this.xLoc = xLoc;
    }

    public void setyLoc(int yLoc) {
        this.yLoc = yLoc;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Bullet)obj).getId()==id;
    }
}
