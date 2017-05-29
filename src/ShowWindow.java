import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

/**
 * Created by joseph on 9-2-2017.
 */
public class ShowWindow {
    JFrame frame;
    GameWorld world;
    GameCanvas gameCanvas;



    ShowWindow(GameWorld world) {

        this.world = world;
        frame = new JFrame("Bomberman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        gameCanvas = new GameCanvas(world);

        frame.add(gameCanvas, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public void repaint() {
        gameCanvas.repaint();
    }

    public void updateTitle(int currentTrial, int totalTrials, long elapsedTime, int wonRounds) {
        double estimatedTimeLeft = 0;
        double winrate = 0;
        if (currentTrial > 1) {

            int remainingTrials = totalTrials - currentTrial;
            int completedTrials = currentTrial - 1;
            double averageTimePerTrial = elapsedTime / completedTrials;

            estimatedTimeLeft = averageTimePerTrial * remainingTrials + averageTimePerTrial; // + averageTime for off-by-one error

            winrate = (double) wonRounds / (double) (currentTrial - 1);
        }

        int minutes = (int) (estimatedTimeLeft / 1000 / 60);
        int seconds = (int) (estimatedTimeLeft / 1000 % 60);

        String timeLeft = "Time left: ~";
        if(minutes > 0) timeLeft = timeLeft.concat(minutes + "m");
        if(seconds > 0)  timeLeft = timeLeft.concat(seconds + "s");
        if(seconds == 0 && minutes == 0) timeLeft = timeLeft.concat("unknown");

        frame.setTitle(timeLeft + " | Current winrate: " + String.format("%.2f", winrate) + " | Trial " + currentTrial + " of " + totalTrials);
    }


}
