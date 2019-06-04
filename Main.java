package com.code;

import com.code.BallWorld;

import javax.swing.*;

/**
 * Main Program for running the bouncing ball as a standalone application.
 */
public class Main {
    // Entry main program
    public static void main(String[] args) {
        // Run UI in the Event Dispatcher Thread (EDT), instead of Main thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("A World of Balls");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(new BallWorld(1280, 800)); // BallWorld is a JPanel
                frame.pack();            // Preferred size of BallWorld
                frame.setVisible(true);  // Show it
            }
        });
    }
}
