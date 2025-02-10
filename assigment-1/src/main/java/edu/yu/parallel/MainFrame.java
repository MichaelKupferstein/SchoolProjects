package edu.yu.parallel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainFrame extends JFrame {
    private final JPanel innerPanelContainer;
    private final SquareLayout squareLayout;
    private ActionListener spinHandler;
    private ActionListener stopHandler;
    private ActionListener resetHandler;
    private ActionListener closeHandler;
    private JButton spinButton;
    private boolean isSpinning = false;  // Flag to track the state of the spin button
    private Thread[] reelThreads;
    private ReelSpinner[] reelSpinners;
    private SlotReel[] slotReels;
    private List<ImageIcon> images;
    private int delay;
    private static final Logger logger = LogManager.getLogger(MainFrame.class);

    public MainFrame(String title, int width, int height) {
        super(title);
        setLayout(new BorderLayout());

        // Create a panel for the inner panels with SquareLayout
        innerPanelContainer = new JPanel();
        squareLayout = new SquareLayout(10, 20);
        innerPanelContainer.setLayout(squareLayout);

        // Add the inner panel container to the center of the main frame
        add(innerPanelContainer, BorderLayout.CENTER);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Create the Spin button and its action listener
        this.spinButton = new JButton("Spin");
        spinButton.addActionListener(this::toggleSpinStop);
        buttonPanel.add(spinButton);

        // Create the Reset button and its action listener
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(this::resetReels);
        buttonPanel.add(resetButton);

        // Create the Close button and its action listener
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(this::closeHandler);
        buttonPanel.add(closeButton);

        // Add the button panel to the bottom of the main frame
        add(buttonPanel, BorderLayout.SOUTH);

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
        setLocationRelativeTo(null);
    }

    // Method to add a panel to the inner panel container
    public void addPanel(InnerPanel panel) {
        innerPanelContainer.add(panel);
        innerPanelContainer.revalidate();
        innerPanelContainer.repaint();
    }

    // Method to toggle between Spin and Stop
    private void toggleSpinStop(ActionEvent e) {

        if (isSpinning) {
            stopReels(e);  // Stop the spinning
            spinButton.setText("Spin");  // Change button text back to "Spin"
        } else {
            spinReels(e);  // Start spinning
            spinButton.setText("Stop");  // Change button text to "Stop"
        }
        isSpinning = !isSpinning;  // Toggle the state
        if(isSpinning) {logger.info("Spin button pressed");}
        else {logger.info("Stop button pressed");}
    }

    // Method to handle the Spin button action
    private void spinReels(ActionEvent e) {
        if (spinHandler != null) {
            spinHandler.actionPerformed(e);
        }

        intizilzeReels(slotReels, images, delay);

        reelThreads = new Thread[reelSpinners.length];
        for(int i = 0; i < reelSpinners.length; i++) {
            reelThreads[i] = new Thread(reelSpinners[i]);
            reelThreads[i].start();
        }

        new Thread(this::monitorReels).start();
    }

    // Method to handle the Stop button action
    private void stopReels(ActionEvent e) {
        if (stopHandler != null) {
            stopHandler.actionPerformed(e);
        }

        if(reelSpinners != null) {
            for(ReelSpinner reelSpinner : reelSpinners) {
                reelSpinner.stop();
            }
            for(Thread thread : reelThreads){
                if(thread != null && thread.isAlive()){
                    thread.interrupt();
                }
            }
        }

    }

    // Method to handle the Reset button action
    private void resetReels(ActionEvent e) {
        if (resetHandler != null) {
            resetHandler.actionPerformed(e);
        }
        logger.info("Reset button pressed");
        stopReels(e);
        resetSpinButton();  // Reset the spin button state as well when resetting

        for(SlotReel slotReel : slotReels) {
            slotReel.reset();
        }

    }

    // Method to handle the Close button action
    private void closeHandler(ActionEvent e) {
        if (closeHandler != null) {
            closeHandler.actionPerformed(e);
        }
        stopReels(e);
        dispose();
    }

    // Method to reset the spin button to its initial state
    public void resetSpinButton() {
        isSpinning = false;  // Set the spinning state to false
        spinButton.setText("Spin");  // Set the button text to "Spin"
    }

    // Method to set the spin handler
    public void setSpinHandler(ActionListener spinHandler) {
        this.spinHandler = spinHandler;
    }

    // Method to set the stop handler
    public void setStopHandler(ActionListener stopHandler) {
        this.stopHandler = stopHandler;
    }

    // Method to set the reset handler
    public void setResetHandler(ActionListener resetHandler) {
        this.resetHandler = resetHandler;
    }

    // Method to set the close handler
    public void setCloseHandler(ActionListener closeHandler) {
        this.closeHandler = closeHandler;
    }

    // Method to make the frame visible
    public void showFrame() {
        setVisible(true);
    }

    public void intizilzeReels(SlotReel[] slotReels, List<ImageIcon> images, int delay) {
        this.slotReels = slotReels;
        this.images = images;
        this.delay = delay;
        reelSpinners = new ReelSpinner[slotReels.length];
        for(int i = 0; i < slotReels.length; i++) {
            List<ImageIcon> shuffledImages = new ArrayList<>(images);
            Collections.shuffle(shuffledImages);
            reelSpinners[i] = new ReelSpinner(slotReels[i], shuffledImages, i, delay);
        }
    }

    private void monitorReels() {
        while (true) {
            boolean allStopped = true;
            for (Thread thread : reelThreads) {
                if (thread != null && thread.isAlive()) {
                    allStopped = false;
                    break;
                }
            }
            if (allStopped) {
                SwingUtilities.invokeLater(() -> {
                    spinButton.setText("Spin");
                    isSpinning = false;
                });
                break;
            }
            try {
                Thread.sleep(100); // Check every 100 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
