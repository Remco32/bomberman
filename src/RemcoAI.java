import java.util.*;
import java.util.ArrayList;

import static java.lang.Math.abs;

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

        while(true) man.move(MoveUtility.Actions.RIGHT);
    }

    void moveTowardsEnemy() {
        if (!findClosestEnemy().isEmpty()) {
            int enemyX = (int) findClosestEnemy().get(0);
            int enemyY = (int) findClosestEnemy().get(1);
            //moveToArea(enemyX, enemyY, 5);
        }
    }

    //find the closest enemy, and return its coordinates in a list (x-coordinate, then y-coordinate)
    List findClosestEnemy() {
        List<Integer> coordinates = new ArrayList<Integer>();
        int amountOfPlayers = world.bomberManList.size() - 1; //minus 1 for index

        int distanceClosestEnemy = 0;
        //int closestEnemyID = 0;
        //search for enemy closest
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

    /**
    //move towards a coordinate. Stops when it gets close.
    //TODO move in same timescale as enemies
    void moveToArea(int x, int y, int distanceToKeep){

        while(distanceToKeep < manhattanDistance(x, man.getX_location(), y, man.getY_location())) {

            if (abs(man.getX_location() - x) > abs(man.getY_location() - y)) { //x distance is bigger than y distance
                if((man.getX_location() - x) > 0){ // enemy is to the left of us
                    man.move(MoveUtility.Actions.LEFT);
                }
                else{ // enemy is to the right of us
                    man.move(MoveUtility.Actions.RIGHT);
                }
            } else {  //y distance is bigger than x distance
                if((man.getY_location() - y) > 0) { // enemy is to above us
                    man.move(MoveUtility.Actions.UP);
                }
                else{ // enemy is to below us
                    man.move(MoveUtility.Actions.DOWN);
                }

            }
        }
    }
     **/

    //Find shortest path using BFS with history list
    void searchForPath(int targetX, int targetY){


    }



    int manhattanDistance(int x0, int x1, int y1, int y0){
        return abs(x1-x0) + abs(y1-y0);
    }




    boolean checkMovementPossible(int targetX, int targetY){
        //if (world.positions[targetX][targetY] != 0 || world.positions[targetX][targetY] != 1)
        return true;
    }
}

