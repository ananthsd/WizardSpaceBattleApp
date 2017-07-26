package com.dandibhotla.ananth.wizardspacebattleapp;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.ArrayList;

/**
 * Created by Ananth on 7/21/2017.
 */

public class Player {
    private volatile int xLoc, yLoc;
    private double width, height;
    private final double MAX_POWER = 100;
    public static final String PLAYER_ONE = "p1";
    public static final String PLAYER_TWO = "p2";
    private volatile int health;
    private volatile ArrayList<Bullet> bullets;
    public static final String COLOR_RED = "red";
    public static final String COLOR_BLUE = "blue";
    private Context context;
    private String playerType;
   public static float colorP1[] = {0f, 0f, 1f, 1.0f};
   public static float colorP2[] = {1f, 0f, 0f, 1f};
    public Player(Context context, String playerType) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int widthPixels = display.widthPixels;
        int heightPixels = display.heightPixels;
        width = ((double) widthPixels) / heightPixels;
        height = ((double) heightPixels) / widthPixels;
        if (playerType.equals("p1")) {
            xLoc = (int) (width * 1000)-100;
            yLoc = 0;
        } else {
            xLoc = (int) (-width * 1000)+100;
            yLoc = 0;
        }
        health = 100;
        bullets = new ArrayList<>();
        this.context = context;
        this.playerType = playerType;
        //  Log.v("player", "w:" + width + "; h:" + height);
    }

    public float getxLoc() {
        return (float) xLoc / 1000;
    }

    public float getyLoc() {
        return (float) yLoc / 1000;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public void move(double angle, double power) {
        //   Log.v("move","Angle:"+angle+"; Power:"+power);
        if (power > MAX_POWER) {
            power = MAX_POWER;
        }
        double xPower = (Math.cos(angle) * power);
        double yPower = (Math.sin(angle) * power);
        xLoc -= (float) xPower / 2;
        yLoc -= (float) yPower / 2;

        // Log.v("move", xLoc + "");
        // Log.v("move", xPower + "");

        double xMovement = 0.01f / ((MAX_POWER)) * xPower * 1000;
        double yMovement = 0.01f / ((MAX_POWER)) * yPower * 1000;
        //  Log.v("moveLoc","X:"+xLoc+"; Y:"+yLoc);
        xLoc += xMovement;
        yLoc += yMovement;
       // Log.v("moveLoc", "X:" + xLoc + "; Y:" + yLoc);

        if (xLoc > width * 1000-100) {
            xLoc = (int) (width * 1000-100);
        } else if (xLoc < -width * 1000+100) {
            xLoc = (int) (-width * 1000+100);
        }
        // if (yLoc > height * 1000 + 100) {
        if (yLoc > 900) {

            yLoc = 900;
            //  } else if (yLoc < -height * 1000 - 300) {
        } else if (yLoc < -900) {
            yLoc = -900;
        }
        //   Log.v("movement", "w:" + width + "; h:" + height);

    }

    public void addBullet(String direction, float[] projM, float[] viewM) {

        if (playerType.equals(PLAYER_ONE)) {
            bullets.add(new Bullet(context, direction, PLAYER_ONE, xLoc, yLoc, projM, viewM));
        } else {
            bullets.add(new Bullet(context, direction, PLAYER_TWO, xLoc, yLoc, projM, viewM));
        }
    }

    public void moveBullets() {
        for (Bullet b : bullets) {
            b.move();
        }
    }
}
