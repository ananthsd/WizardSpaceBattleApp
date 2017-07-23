package com.dandibhotla.ananth.wizardspacebattleapp;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Ananth on 7/21/2017.
 */

public class Player {
    private volatile int xLoc, yLoc;
    private double width, height;
    private final double MAX_POWER = 50;
    public static final String PLAYER_ONE_START="p1";
    public static final String PLAYER_TWO_START="p2";
    private volatile int health;
    public Player(Context context, String playerType) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int widthPixels = display.widthPixels;
        int heightPixels = display.heightPixels;
        width = ((double) widthPixels) / heightPixels;
        height = ((double) heightPixels) / widthPixels;
        if(playerType.equals("p1")){
            xLoc = (int) (width * 1000);
            yLoc = 0;
        }
        else{
            xLoc = (int) (-width * 1000 );
            yLoc = 0;
        }
        health = 100;
      //  Log.v("player", "w:" + width + "; h:" + height);
    }

    public float getxLoc() {
        return (float) xLoc / 1000;
    }

    public float getyLoc() {
        return (float) yLoc / 1000;
    }

    public void move(double angle, double power) {
        //   Log.v("move","Angle:"+angle+"; Power:"+power);
        if (power > MAX_POWER) {
            power = MAX_POWER;
        }
        double xPower = (Math.cos(angle) * power);
        double yPower = (Math.sin(angle) * power);
        xLoc -= (float) xPower;
        yLoc -= (float) yPower;

       // Log.v("move", xLoc + "");
       // Log.v("move", xPower + "");

        double xMovement = 0.01f / ((MAX_POWER)) * xPower * 1000;
        double yMovement = 0.01f / ((MAX_POWER)) * yPower * 1000;
        //  Log.v("moveLoc","X:"+xLoc+"; Y:"+yLoc);
        xLoc += xMovement;
        yLoc += yMovement;
    //    Log.v("moveLoc", "X:" + xLoc + "; Y:" + yLoc);
        if (xLoc > width * 1000 ) {
            xLoc = (int) (width * 1000);
        } else if (xLoc < -width * 1000 ) {
            xLoc = (int) (-width * 1000 );
        }
        if (yLoc > height * 1000 +100) {
            yLoc = (int) (height * 1000 +100);
        } else if (yLoc < -height * 1000 -300) {
            yLoc = (int) (-height * 1000 -300);
        }
     //   Log.v("movement", "w:" + width + "; h:" + height);

    }

}
