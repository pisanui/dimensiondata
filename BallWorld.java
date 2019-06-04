package com.code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * The control logic and main display panel for game.
 */
public class BallWorld extends JPanel {
    private static final int UPDATE_RATE = 30;  // Frames per second (fps)

    private ContainerBox box;  // The container rectangular box

    private DrawCanvas canvas; // Custom canvas for drawing the box/ball
    private int canvasWidth;
    private int canvasHeight;

    private static final float EPSILON_TIME = 1e-2f;  // Threshold for zero time

    // Balls
    private static final int MAX_BALLS = 25; // Max number allowed
    private int currentNumBalls;             // Number currently active
    private Ball[] balls = new Ball[MAX_BALLS];

    /**
     * Constructor to create the UI components and init the game objects.
     * Set the drawing canvas to fill the screen (given its width and height).
     *
     * @param width : screen width
     * @param height : screen height
     */
    public BallWorld(int width, int height) {

        canvasWidth = width;
        canvasHeight = height;

        currentNumBalls = 11;
        balls[0] = new Ball(100, 410, 25, 3, 34, Color.YELLOW);
        balls[1] = new Ball(80, 350, 25, 2, -114, Color.YELLOW);
        balls[2] = new Ball(530, 400, 30, 3, 14, Color.GREEN);
        balls[3] = new Ball(400, 400, 30, 3, 14, Color.GREEN);
        balls[4] = new Ball(400, 50, 35, 1, -47, Color.PINK);
        balls[5] = new Ball(480, 320, 35, 4, 47, Color.PINK);
        balls[6] = new Ball(80, 150, 40, 1, -114, Color.ORANGE);
        balls[7] = new Ball(100, 240, 40, 2, 60, Color.ORANGE);
        balls[8] = new Ball(250, 400, 50, 3, -42, Color.BLUE);
        balls[9] = new Ball(200, 80, 70, 6, -84, Color.CYAN);
        balls[10] = new Ball(500, 170, 90, 6, -42, Color.MAGENTA);

        // The rest of the balls, that can be launched using the launch button
        for (int i = currentNumBalls; i < MAX_BALLS; ++i) {
            balls[i] = new Ball(20, canvasHeight - 20, 15, 5, 45, Color.black);
        }
        // Init the Container Box to fill the screen
        box = new ContainerBox(0, 0, canvasWidth, canvasHeight, Color.BLACK, Color.WHITE);
        // Init the custom drawing panel for drawing the game
        canvas = new DrawCanvas();
        this.setLayout(new BorderLayout());
        this.add(canvas, BorderLayout.CENTER);

        // Handling window resize.
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component c = (Component)e.getSource();
                Dimension dim = c.getSize();
                canvasWidth = dim.width;
                canvasHeight = dim.height;
                // Adjust the bounds of the container to fill the window
                box.set(0, 0, canvasWidth, canvasHeight);
            }
        });

        // Start the ball bouncing
        gameStart();
    }

    public void gameStart() {
        // Run the game logic in its own thread.
        Thread gameThread = new Thread() {
            public void run() {
                while (true) {
                    long beginTimeMillis, timeTakenMillis, timeLeftMillis;
                    beginTimeMillis = System.currentTimeMillis();

                    // Execute one game step
                    gameUpdate();
                    // Refresh the display
                    repaint();

                    // Provide the necessary delay to meet the target rate
                    timeTakenMillis = System.currentTimeMillis() - beginTimeMillis;
                    timeLeftMillis = 1000L / UPDATE_RATE - timeTakenMillis;
                    if (timeLeftMillis < 5) timeLeftMillis = 5; // Set a minimum

                    // Delay and give other thread a chance
                    try {
                        Thread.sleep(timeLeftMillis);
                    } catch (InterruptedException ex) {}
                }
            }
        };
        gameThread.start();  // Invoke GaemThread.run()
    }

    /** Update the game objects, detect collision and provide response. */
    public void gameUpdate() {
        float timeLeft = 1.0f;  // One time-step to begin with

        // Repeat until the one time-step is up
        do {
            // Find the earliest collision up to timeLeft among all objects
            float tMin = timeLeft;

            // Check collision between two balls
            for (int i = 0; i < currentNumBalls; ++i) {
                for (int j = 0; j < currentNumBalls; ++j) {
                    if (i < j) {
                        balls[i].intersect(balls[j], tMin);
                        if (balls[i].earliestCollisionResponse.t < tMin) {
                            tMin = balls[i].earliestCollisionResponse.t;
                        }
                    }
                }
            }
            // Check collision between the balls and the box
            for (int i = 0; i < currentNumBalls; ++i) {
                balls[i].intersect(box);
                if (balls[i].earliestCollisionResponse.t < tMin) {
                    tMin = balls[i].earliestCollisionResponse.t;
                }
            }

            // Update all the balls up to the detected earliest collision time tMin,
            // or timeLeft if there is no collision.
            for (int i = 0; i < currentNumBalls; ++i) {
                balls[i].update(tMin);
            }

            timeLeft -= tMin;                // Subtract the time consumed and repeat
        } while (timeLeft > EPSILON_TIME);  // Ignore remaining time less than threshold
    }

    /** The custom drawing panel for the bouncing ball (inner class). */
    class DrawCanvas extends JPanel {
        /** Custom drawing codes */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);    // Paint background
            // Draw the box and the ball
            box.draw(g);

            for (int i = 0; i < balls.length; i++) {
                balls[i].draw(g);
                // Display ball's information
                g.setColor(Color.WHITE);
                g.setFont(new Font("Courier New", Font.PLAIN, 12));
                //g.drawString("Ball " + balls[i].toString(), 20, 30 * (i + 1));
            }
        }

        /** Called back to get the preferred size of the component. */
        @Override
        public Dimension getPreferredSize() {
            return (new Dimension(canvasWidth, canvasHeight));
        }
    }
}
