package edu.yu.parallel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.List;
import java.util.Random;

public class ReelSpinner implements Runnable{

    private static final Logger logger = LogManager.getLogger(ReelSpinner.class);
    private final SlotReel slotReel;
    private List<ImageIcon> images;
    private int reelNum;
    private volatile boolean running = true;
    private int delay;

    public ReelSpinner(SlotReel slotReel, List<ImageIcon> images, int reelNum, int delay) {
        this.slotReel = slotReel;
        this.images = images;
        this.reelNum = reelNum;
        this.delay = delay;
    }

    @Override
    public void run() {

        Random rand = new Random();
        int duration = 2000 + rand.nextInt(2000);
        long endTime = System.currentTimeMillis() + duration;

        while (running && System.currentTimeMillis() < endTime) {
            for(ImageIcon image : images) {
                if(!running) break;
                slotReel.setImage(image);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        if(running) {
            logger.info("Slot reel #" + reelNum + " completed");
        }else{
            logger.info("Slot reel #" + reelNum + " was interrupted");
        }
    }

    public void stop() {
        running = false;
    }

}
