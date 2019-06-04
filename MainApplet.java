package com.code;

import javax.swing.*;

/**
 * Main Program to run as an applet
 * The display area is 640x480.
 */
public class MainApplet extends JApplet {
    @Override
    public void init() {
        // Run UI in the Event Dispatcher Thread
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setContentPane(new BallWorld(1280, 800)); // BallWorld is a JPanel
            }
        });
    }
}
