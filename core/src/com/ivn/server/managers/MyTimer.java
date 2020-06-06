package com.ivn.server.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Timer;

import javax.swing.*;

public class MyTimer {
    private int min;
    private int seg;
    private int totalSeg;

    public boolean isFinished;

    private int originalTotalSeg;

    public MyTimer(int min, int seg){
        this.min = min;
        this.seg = seg;
        totalSeg = seg + min*60;
        originalTotalSeg = totalSeg;

    }

    private Timer.Task myTimerTask = new Timer.Task() {
        @Override
        public void run() {
            totalSeg--;

            seg = totalSeg % 60;
            min = totalSeg / 60;

            if(totalSeg == 0)
                isFinished = true;
        }
    };

    public void start(){
        totalSeg = originalTotalSeg;
        seg = totalSeg % 60;
        min = totalSeg / 60;
        isFinished = false;
        Timer.schedule(myTimerTask, 1f, 1f, originalTotalSeg - 1);
    }

    public void stop(){
        myTimerTask.cancel();
    }

    public void drawTimer(Batch batch, BitmapFont font){
        batch.begin();
        font.draw(batch, toString(), Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
        batch.end();
    }

    public String toString(){
        String minString;
        if (min < 10)
            minString = 0+""+min;
        else
            minString = min+"";

        String segString;
        if (seg < 10)
            segString = 0+""+seg;
        else
            segString = seg+"";

        return minString+":"+segString;
    }
}
