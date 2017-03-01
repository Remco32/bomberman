import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by joseph on 9-2-2017.
 */
public class Images {
    Image road;
    Image wallSoft;
    Image wallHard;
    Image bomb;
    Image grass;
    Image player1;
    Image player2;
    Image player3;
    Image player4;


    Images(){
        try {
            BufferedImage buffer = ImageIO.read(getClass().getResource("wallBreak.jpeg"));
            wallSoft = buffer.getScaledInstance(50, 50, wallSoft.SCALE_DEFAULT);
            buffer = ImageIO.read(getClass().getResource("concrete.jpg"));
            wallHard = buffer.getScaledInstance(50, 50, wallSoft.SCALE_DEFAULT);
            buffer = ImageIO.read(getClass().getResource("bomb.png"));
            bomb = buffer.getScaledInstance(30, 30, wallSoft.SCALE_DEFAULT);
            //buffer = ImageIO.read(getClass().getResource("green.png"));
            //bomb = buffer.getScaledInstance(30, 30, wallSoft.SCALE_DEFAULT);
            buffer = ImageIO.read(getClass().getResource("grass.png"));
            grass = buffer.getScaledInstance(50, 50, wallSoft.SCALE_DEFAULT);
            buffer = ImageIO.read(getClass().getResource("bomberman1.png"));
            player1 = buffer.getScaledInstance(40, 40, wallSoft.SCALE_DEFAULT);
            buffer = ImageIO.read(getClass().getResource("bomberman2.png"));
            player2 = buffer.getScaledInstance(40, 40, wallSoft.SCALE_DEFAULT);
            buffer = ImageIO.read(getClass().getResource("bomberman3.png"));
            player3 = buffer.getScaledInstance(40, 40, wallSoft.SCALE_DEFAULT);
            buffer = ImageIO.read(getClass().getResource("bomberman4.png"));
            player4 = buffer.getScaledInstance(40, 40, wallSoft.SCALE_DEFAULT);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
