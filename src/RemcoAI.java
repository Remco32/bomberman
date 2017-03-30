import java.util.*;
import java.util.ArrayList;

/**
 * Created by Remco on 29-3-2017.
 */
public class RemcoAI {

    GameWorld world;
    BomberMan man;
    Random rnd;
    ArrayList<MoveUtility> moves;

    RemcoAI(GameWorld world, BomberMan man) {
        this.world = world;
        this.man = man;
    }

    void test(){

        while(true) man.Move(3);
    }

    //find the closest enemy, and return its coordinates in a list (x-coordinate, then y-coordinate)
    List findClosestEnemy() {
        List<Integer> coordinates = new ArrayList<Integer>();
        int amountOfPlayers = world.bomberManList.size() - 1; //minus 1 for index

        //search for enemy closest

        int distanceClosestEnemy = 0;
        //int closestEnemyID = 0;
        for (int i = 1; i <= amountOfPlayers; i++) {

            int enemyX = world.bomberManList.get(i).getX_location();
            int enemyY = world.bomberManList.get(i).getY_location();
            int distance = manhattanDistance(enemyX, man.getX_location(), enemyY, man.getY_location());
            if (distanceClosestEnemy < distance) {
                distanceClosestEnemy = distance;
                coordinates.clear(); //empty list
                coordinates.add(enemyX); //add X of closest enemy
                coordinates.add(enemyY); // add Y of closest enemy
                //closestEnemyID = i;
            }
        }
        //System.out.println("Closest enemy is ID " + closestEnemyID + " with distance " + distanceClosestEnemy);
        return coordinates;
    }



    int manhattanDistance(int x0, int x1, int y1, int y0){
        return Math.abs(x1-x0) + Math.abs(y1-y0);
    }
}
