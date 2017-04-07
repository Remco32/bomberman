import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by Remco on 29-3-2017.
 */
public class RemcoAI {

    GameWorld world;
    BomberMan man;
    ArrayList<MoveUtility> moves;
    Queue<Pair> queue = new LinkedList<>();





    RemcoAI(GameWorld world, BomberMan man) {
        this.world = world;
        this.man = man;
    }


    void test(){

    }

    void moveTowardsEnemy() {
        if (!findClosestEnemy().isEmpty()) {
            int enemyX = (int) findClosestEnemy().get(0);
            int enemyY = (int) findClosestEnemy().get(1);

            //add our initial position to queue
            queue.add(new Pair(man.getX_location(), man.getY_location()));
            ArrayList<Pair> path = new ArrayList<>();

            ArrayList<Pair> consideredCoordinates = new ArrayList<>();

            searchForPath(enemyX, enemyY, man.getX_location(), man.getY_location(), path, consideredCoordinates);
            queue.clear();

            //consideredCoordinates.clear();

            //for(int i = 0; i < path.size(); i++){
            //    moveToArea((int)path.get(i).getFirst(), (int)path.get(i).getSecond(),0);
            //}

            //System.out.println("Visited all reachable locations.");

        }
    }

    void pairCheck(){
        Pair testPair1 = new Pair(1,2);
        Pair testPair2 = new Pair(2,2);

        ArrayList<Pair> testList = new ArrayList<>();

        testList.add(testPair1);

        if(testList.contains(testPair1)){
            System.out.println("pair1 zit erin");
        }
        if(testList.contains(testPair2)){
            System.out.println("pair2 zit erin");
        }
    }

    //TODO use pair instead of list
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


    //move agent towards a coordinate. Stops when it gets close.
    void moveToArea(int x, int y, int distanceToKeep){

        while(distanceToKeep < manhattanDistance(x, man.getX_location(), y, man.getY_location())) {

            if (abs(man.getX_location() - x) > abs(man.getY_location() - y)) { //x distance is bigger than y distance
                if((man.getX_location() - x) > 0){ // enemy is to the left of us
                    man.move(MoveUtility.Actions.LEFT);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else{ // enemy is to the right of us
                    man.move(MoveUtility.Actions.RIGHT);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {  //y distance is bigger than x distance
                if((man.getY_location() - y) > 0) { // enemy is to above us
                    man.move(MoveUtility.Actions.UP);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else{ // enemy is to below us
                    man.move(MoveUtility.Actions.DOWN);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }


//TODO IMPLEMENTEER EEN QUEUE DIT WERKT ZO NIET ANDERS

    void searchForPath(int targetX, int targetY, int ownX, int ownY, ArrayList<Pair> path, ArrayList<Pair> consideredCoordinates) {

        //we will never have to cross the same space again in a path.

        //DEBUG
        targetX = 6;
        targetY = 4;

        //No answer
        if (queue.isEmpty()) {
            return;
        }

        //take entry from queue
        Pair currentPair = queue.remove(); //can't dequeue twice and still get the same pair.
        ownX = (int) currentPair.getFirst();
        ownY = (int) currentPair.getSecond();

        //coordinate has been checked already: abort
        if (consideredCoordinates.contains(new Pair(ownX, ownY))) {

            return;
        }

        if (!(consideredCoordinates.contains(new Pair(ownX, ownY)))) {
            //add our current position to consideredCoordinates, so we don't visit them again.
            path.add(new Pair(ownX, ownY));
            consideredCoordinates.add(new Pair(ownX, ownY));

        }



        //BASE CASE
        if (targetX == ownX && targetY == ownY) {

            System.out.println("path = " + path.toString());

            //Achterstevoren lijst af, checken of manhatten distance 1 is, daaruit nieuwe lijst maken wat uiteindelijk pad is
            ArrayList<Pair> finalPath = new ArrayList<>();

            //Put last element in a variable
            currentPair = path.get(path.size() -1);

            //Add the last element to our final path
            finalPath.add(currentPair);

            //Go through all elements of list
            for(int i = path.size() -1 ; i > 0 ; i--){
                Pair comparingPair = path.get(i);
                //Check if it is possible to go to each other
                if(manhattanDistance( ((int) currentPair.getFirst()),  (int) comparingPair.getFirst(), (int) currentPair.getSecond(), (int) comparingPair.getSecond()) == 1) {
                    finalPath.add(comparingPair);
                    currentPair = comparingPair;
                }

            }
            System.out.println("FINAL path = " + finalPath.toString());


             for (int i = finalPath.size() -1 ; i >= 0 ; i--) {
             moveToArea((int) finalPath.get(i).getFirst(), (int) finalPath.get(i).getSecond(), 0);

             }


            return;
        }

        //look around to all possible moves

        //check if we can make a move to the RIGHT, and if we haven't visited this spot before
        if ((checkMovementPossible(ownX + 1, ownY)) && !(consideredCoordinates.contains(new Pair((ownX + 1), ownY))) && !(queue.contains(new Pair((ownX + 1), ownY)))) {
            //add this new location to the queue
            queue.add(new Pair(ownX + 1, ownY));

        }
        //check if we can make a move to the LEFT, and if we haven't visited this spot before
        if (checkMovementPossible(ownX - 1, ownY) && !(consideredCoordinates.contains(new Pair((ownX - 1), ownY))) && !(queue.contains(new Pair((ownX - 1), ownY)))) {
            //add this new location to the queue
            queue.add(new Pair(ownX - 1, ownY));
        }
        //check if we can make a move to the UP, and if we haven't visited this spot before
        if (checkMovementPossible(ownX, ownY - 1) && !(consideredCoordinates.contains(new Pair(ownX, (ownY - 1)))) && !(queue.contains(new Pair((ownX), ownY - 1)))) {
            //add this new location to the queue
            queue.add(new Pair(ownX, ownY - 1));
        }
        //check if we can make a move to the DOWN, and if we haven't visited this spot before
        if (checkMovementPossible(ownX, ownY + 1) && !(consideredCoordinates.contains(new Pair(ownX, (ownY + 1)))) && !(queue.contains(new Pair((ownX), ownY + 1)))) {
            //add this new location to the queue
            queue.add(new Pair(ownX, ownY + 1));
        }

        //After adding all possible options, we make a move
        if ((checkMovementPossible(ownX + 1, ownY)) && !(consideredCoordinates.contains(new Pair((ownX + 1), ownY)))) {

            searchForPath(targetX, targetY, ownX + 1, ownY, path, consideredCoordinates);

        }
        if (checkMovementPossible(ownX - 1, ownY) && !(consideredCoordinates.contains(new Pair((ownX - 1), ownY)))) {

            searchForPath(targetX, targetY, ownX - 1, ownY, path, consideredCoordinates);

        }
        if (checkMovementPossible(ownX, ownY - 1) && !(consideredCoordinates.contains(new Pair(ownX, (ownY - 1))))) {

            searchForPath(targetX, targetY, ownX, ownY - 1, path, consideredCoordinates);

        }
        if (checkMovementPossible(ownX, ownY + 1) && !(consideredCoordinates.contains(new Pair(ownX, (ownY + 1))))) {

            searchForPath(targetX, targetY, ownX, ownY + 1, path, consideredCoordinates);

        }

        //Start again, since queue isn't empty yet
        searchForPath(targetX, targetY, ownX, ownY, path, consideredCoordinates);
    }





        /**
         //See if we can move LEFT
         if (checkMovementPossible(ownX-1, ownY)) {
         if (targetX == ownX-1 && targetY == ownY) { //found target
         //once we are back at our own position, move to the found path
         /**
         man.move(MoveUtility.Actions.LEFT);
         try {
         Thread.sleep(500);
         } catch (InterruptedException e) {
         e.printStackTrace();
         }

         return consideredCoordinates;
         }
         if(!consideredCoordinates.contains(new Pair(ownX-1, ownY))) {
         consideredCoordinates.add(new Pair(ownX-1, ownY));
         searchForPath(targetX, targetY, ownX-1, ownY, consideredCoordinates);
         }
         }

        //check if we can make a move to the RIGHT
        if (checkMovementPossible(ownX + 1, ownY)) {
            //check if our target is to the right
            //if (targetX == ownX+1 && targetY == ownY && !consideredCoordinates.contains(new Pair(ownX+1, ownY))) {
            //add final step to the list
            consideredCoordinates.add(new Pair(ownX + 1, ownY));
            searchForPath(targetX, targetY, ownX + 1, ownY, consideredCoordinates);
            //return consideredCoordinates;
            //}
            //else{
            //    searchForPath(targetX, targetY, ownX+1, ownY, consideredCoordinates);
            //}
            //if(!consideredCoordinates.contains(new Pair(ownX+1, ownY))) {
            //  consideredCoordinates.add(new Pair(ownX+1, ownY));
            //searchForPath(targetX, targetY, ownX+1, ownY, consideredCoordinates);
            //}
        }
        /**
         //See if we can move UP
         if (checkMovementPossible(ownX, ownY-1)) {
         if (targetX == ownX && targetY == ownY-1) {
         /**
         man.move(MoveUtility.Actions.UP);
         try {
         Thread.sleep(500);
         } catch (InterruptedException e) {
         e.printStackTrace();
         }
         return consideredCoordinates;
         }
         if (!consideredCoordinates.contains(new Pair(ownX, ownY - 1))) {
         consideredCoordinates.add(new Pair(ownX, ownY - 1));
         searchForPath(targetX, targetY, ownX, ownY - 1, consideredCoordinates);
         }
         }


        //See if we can move DOWN
        if (checkMovementPossible(ownX, ownY + 1)) {
            //if (targetX == ownX && targetY == ownY + 1 && !consideredCoordinates.contains(new Pair(ownX, ownY + 1))) {

            //add final step to the list
            consideredCoordinates.add(new Pair(ownX, ownY + 1));
            searchForPath(targetX, targetY, ownX, ownY + 1, consideredCoordinates);

            //return consideredCoordinates;
            //}
            //else{
            //    searchForPath(targetX, targetY, ownX, ownY + 1, consideredCoordinates);
            //}
            //if (!consideredCoordinates.contains(new Pair(ownX, ownY + 1))) {
            //  consideredCoordinates.add(new Pair(ownX, ownY + 1));
            //searchForPath(targetX, targetY, ownX, ownY + 1, consideredCoordinates);
            //}
        }

        //No solution found, return empty list
    }
    **/






    int manhattanDistance(int x0, int x1, int y1, int y0){
        return abs(x1-x0) + abs(y1-y0);
    }

    boolean checkMovementPossible(int targetX, int targetY) {
        //out of bounds
        if (targetX > world.gridSize-1 || targetY > world.gridSize-1 || targetX < 0 || targetY < 0) {
            return false;
        }

        //Return a false if there is a hardwall or softwall at this position
        if (world.positions[targetX][targetY].getType() == WorldPosition.Fieldtypes.HARDWALL
                || world.positions[targetX][targetY].getType() == WorldPosition.Fieldtypes.SOFTWALL) {
            return false;

        }
        return true;

    }

}

