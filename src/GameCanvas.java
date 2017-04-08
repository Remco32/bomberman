import javax.swing.*;
import java.awt.*;

/**
 * Created by joseph on 9-2-2017.
 */
public class GameCanvas extends JPanel {
    GameWorld world;
    private Images image;

    GameCanvas(GameWorld world) {
        this.world = world;
        image = new Images();
        setPreferredSize(new Dimension(world.gridSize * 50, world.gridSize * 50));
    }

    public void paint(Graphics g) {
        super.paint(g);
        WorldPosition position;
        for (int x = 0; x < world.gridSize; x++) {
            for (int y = 0; y < world.gridSize; y++) {
                position = world.positions[x][y];
                if (position.type == WorldPosition.Fieldtypes.HARDWALL) paintWallHard(g, x * 50, y * 50);
                if (position.type == WorldPosition.Fieldtypes.SOFTWALL) paintWallSoft(g, x * 50, y * 50);
                if (position.type == WorldPosition.Fieldtypes.EMPTY) paintRoad(g, x * 50, y * 50);
                if (position.type == WorldPosition.Fieldtypes.EXPLOSION) paintExplosion(g, x * 50, y * 50);
                if (!position.bombermanList.isEmpty())
                    paintBomberMan(g, x * 50 + 5, y * 50 + 5, position.bombermanList.get(0));
                if (position.bomb != null) paintBomb(g, x * 50 + 10, y * 50 + 10);
            }
        }
    }

    private void paintWallSoft(Graphics g, int x, int y) {
        g.drawImage(image.wallSoft, x, y, null);
    }

    private void paintWallHard(Graphics g, int x, int y) {
        g.drawImage(image.wallHard, x, y, null);
    }

    private void paintRoad(Graphics g, int x, int y) {
        //g.setColor(Color.gray);
        //g.fillRect(x, y, 50, 50);
        g.drawImage(image.grass, x, y, null);
    }

    private void paintBomb(Graphics g, int x, int y) {
        g.drawImage(image.bomb, x, y, null);
    }

    private void paintExplosion(Graphics g, int x, int y) {
        g.drawImage(image.explosion, x, y, null);
    }

    private void paintBomberMan(Graphics g, int x, int y, BomberMan man) {
        if (man.id == 1) g.drawImage(image.player1, x, y, Color.gray, null);
        if (man.id == 2) g.drawImage(image.player2, x, y, Color.gray, null);
        if (man.id == 3) g.drawImage(image.player3, x, y, Color.gray, null);
        if (man.id == 4) g.drawImage(image.player4, x, y, Color.gray, null);


    }

}
