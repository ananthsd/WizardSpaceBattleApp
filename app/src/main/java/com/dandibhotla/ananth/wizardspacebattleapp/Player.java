package com.dandibhotla.ananth.wizardspacebattleapp;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ananth on 7/21/2017.
 */

public class Player {
    private volatile int xLoc, yLoc;
    private double width;
    private final double MAX_POWER = 100;
    public static final String PLAYER_ONE = "p1";
    public static final String PLAYER_TWO = "p2";
    private volatile int health, score;
    private volatile ArrayList<Bullet> bullets;
    public static final String COLOR_RED = "red";
    public static final String COLOR_BLUE = "blue";
    private Context context;
    private String playerType;
    private final int damageIncrement = 5;
    private double angle, power;
    public static final String LEFT_FACING = "left", RIGHT_FACING = "right";
    private static long currentId;
    private float[] projectionM,viewM;

    public float[] getProjectionM() {
        return projectionM;
    }

    public void setProjectionM(float[] projectionM) {
        this.projectionM = projectionM;
    }

    public float[] getViewM() {
        return viewM;
    }

    public void setViewM(float[] viewM) {
        this.viewM = viewM;
    }

    public TextView getScoreView() {
        return scoreView;
    }

    public TextView getHealthView() {
        return healthView;
    }

    public void increaseScore() {
        score++;
    }

    public int getScore() {
        return score;
    }

    private TextView scoreView, healthView;
    public static float colorP1[] = {0f, 0f, 1f, 1.0f};
    public static float colorP2[] = {1f, 0f, 0f, 1f};
    public static float colorBG[] = {0f, 0f, 0f, 1f};

    public Player(Context context, String playerType, TextView scoreView, TextView healthView) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int widthPixels = display.widthPixels;
        int heightPixels = display.heightPixels;
        width = ((double) widthPixels) / heightPixels;
        if (playerType.equals("p1")) {
            xLoc = (int) (width * 1000) - 100;
            yLoc = 0;
        } else {
            xLoc = (int) (-width * 1000) + 100;
            yLoc = 0;
        }
        health = 1000;
        bullets = new ArrayList<>();
        this.context = context;
        this.playerType = playerType;
        this.scoreView = scoreView;
        this.healthView = healthView;
        //  Log.v("player", "w:" + width + "; h:" + height);
    }

    public void reset() {
        if (playerType.equals("p1")) {
            xLoc = (int) (width * 1000) - 100;
            yLoc = 0;
        } else {
            xLoc = (int) (-width * 1000) + 100;
            yLoc = 0;
        }
        health = 1000;
        bullets.removeAll(bullets);

    }

    public void setHealth(int health) {
        Log.v("sethealth",health+"");
        this.health = health;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setxLoc(float xLoc) {
        this.xLoc = (int) (xLoc * 1000);
    }

    public void setyLoc(float yLoc) {
        this.yLoc = (int) (yLoc * 1000);
    }

    public float getxLoc() {
        return (float) xLoc / 1000;
    }

    public float getyLoc() {
        return (float) yLoc / 1000;
    }

    public int getxLocPx() {
        return xLoc;
    }

    public int getyLocPx() {
        return yLoc;
    }

    public synchronized ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public void setMoveValues(double angle, double power) {
        this.angle = angle;
        this.power = power;
    }

    public void move() {
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
        // xMovement*=100;
        //yMovement*=100;
        // xMovement=Math.round(xMovement);
        // yMovement=Math.round(yMovement);
        // xMovement/=100;
        // yMovement/=100;

        Log.v("moveLoc", "X:" + xMovement + "; Y:" + yMovement);
        Log.v("moveLoc", "X:" + (int) xMovement + "; Y:" + (int) yMovement);
        xLoc += xMovement;
        yLoc += yMovement;
        // Log.v("moveLoc", "X:" + xLoc + "; Y:" + yLoc);

        if (xLoc > width * 1000 - 100) {
            xLoc = (int) (width * 1000 - 100);
        } else if (xLoc < -width * 1000 + 100) {
            xLoc = (int) (-width * 1000 + 100);
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
            bullets.add(new Bullet(context, direction, PLAYER_ONE, xLoc, yLoc, projM, viewM, currentId));
        } else {
            bullets.add(new Bullet(context, direction, PLAYER_TWO, xLoc, yLoc, projM, viewM, currentId));
        }
        currentId++;
        if (currentId == Long.MAX_VALUE) {
            currentId = 0;
        }
    }
    public void addBullet(String direction, float x, float y) {
        int xVal = (int)(x*1000);
        int yVal = (int)(y*1000);
        if (playerType.equals(PLAYER_ONE)) {
            bullets.add(new Bullet(context, direction, PLAYER_ONE, xVal, yVal, projectionM, viewM, currentId));
        } else {
            bullets.add(new Bullet(context, direction, PLAYER_TWO, xVal, yVal, projectionM, viewM, currentId));
        }
        currentId++;
        if (currentId == Long.MAX_VALUE) {
            currentId = 0;
        }
    }
    public void resetBullet(Bullet b, String direction, float[] projM, float[] viewM) {
        b.reset(xLoc, yLoc, direction, playerType);
    }

    private boolean up = false;

    public void moveBullets() {
        for (Bullet b : bullets) {
            b.move();
        }
        double interval = 0;

        if (up) {
            yLoc += interval;
        } else {
            yLoc -= interval;
        }
        if (yLoc > 900) {
            up = false;
            yLoc = 900;
            //  } else if (yLoc < -height * 1000 - 300) {
        } else if (yLoc < -900) {
            up = true;
            yLoc = -900;
        }
    }

    public int getHealth() {
        return health;
    }

    /*
        * static float squareCoords[] = {
                -0.1f, 0.1f, 0.0f,   // top left
                -0.1f, -0.1f, 0.0f,   // bottom left
                0.1f, -0.1f, 0.0f,   // bottom right
                0.1f, 0.1f, 0.0f}; // top right

                static float rightCoords[] = {
                0.1f, 0.01f, 0.0f,   // top left
                0.1f, -0.01f, 0.0f,   // bottom left
                0.15f, -0.01f, 0.0f,   // bottom right
                0.15f, 0.01f, 0.0f}; // top right
        * */

    public boolean collideDetect(Bullet b) {

        if (Math.abs(b.getxLoc() - getxLoc()) < 0.15f && Math.abs(b.getyLoc() - getyLoc()) < 0.12f) {
            Log.v("damage", "hit");
            return true;
        }
        return false;
    }

    public void damage() {
        health -= damageIncrement;
    }
}
