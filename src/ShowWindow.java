import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

}
