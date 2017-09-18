package com.dandibhotla.ananth.wizardspacebattleapp;

import android.util.Log;

/**
 * Created by Ananth on 9/18/2017.
 */

public class FPSCounter {
    long startTime = System.nanoTime();
    int frames = 0;

    public void logFrame() {
        frames++;
        if(System.nanoTime() - startTime >= 1000000000) {
            Log.d("FPSCounter", "fps: " + frames);
            frames = 0;
            startTime = System.nanoTime();
        }
    }
}