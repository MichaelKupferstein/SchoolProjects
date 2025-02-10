package edu.yu.parallel;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class App {

    private final static Logger logger = LogManager.getLogger(App.class);
    private final static String TITLE = "Jackpot";
    private final static int WIDTH = 600;
    private final static int HEIGHT = 400;
    private final static int DFT_NUM_REELS = 3;
    private final static String INITIAL_IMAGE = "/click.png";
    private final static String REEL_IMAGE_DIR = "/teams";
    private final static int TRANSITION_DELAY = 50;


    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        logger.info("Hello today!");

        // Override the default by setting the SLOT_REELS environment
        // variable with a positive integer number
        var numReels = getNumberOfSlotReels();
        logger.info("Using " + String.valueOf(numReels) + " slot reels");

        var initialImage = ImageLoader.loadImageFromResources(INITIAL_IMAGE);

        //Load the list of MLB teams and log number of imagges loaded
        List<ImageIcon> teamLogos = ImageLoader.loadImagesFromResources(REEL_IMAGE_DIR);
        logger.info("Loaded " + teamLogos.size() + " images");

        // Create 3 slot reels and initialize with the above image
        var slotReels = new SlotReel[numReels];
        for (int i = 0; i < numReels; i++) {
            slotReels[i] = new SlotReel(String.valueOf(i), new InnerPanel(), initialImage);
        }

        // Create the main frame and add the 3 slot reels to it
        MainFrame mainFrame = new MainFrame(TITLE, WIDTH, HEIGHT);
        for (SlotReel slotReel : slotReels) {
            mainFrame.addPanel(slotReel.getPanel());
        }

        mainFrame.intizilzeReels(slotReels, teamLogos, TRANSITION_DELAY);

        final Object syncObject = new Object();
        // Set the close handler to notify the main thread
        mainFrame.setCloseHandler(e -> {
            mainFrame.dispose();
            logger.info("Close button pressed");
            synchronized (syncObject) {
                syncObject.notify();
            }
        });

        mainFrame.setVisible(true);

        synchronized (syncObject) {
            try {
                syncObject.wait();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        logger.info("Goodbye!");
    }

    private static int getNumberOfSlotReels() {
        String numReelsEnv = System.getProperty("SLOT_REELS");
        if (numReelsEnv != null) {
            try {
                int numReels = Integer.parseInt(numReelsEnv);
                if (numReels < 1)
                    throw new IllegalArgumentException(numReelsEnv);
                return numReels;
            } catch (Exception e) {
                logger.info("Invalid number for SLOT_REELS (" + numReelsEnv + "). Using default value.");
                return DFT_NUM_REELS;
            }
        } else {
            return DFT_NUM_REELS;
        }
    }
 }
