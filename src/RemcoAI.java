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

    void play(int distanceToKeepInSteps) {
        //while (world.bomberManList.get(0).alive) {
        moveTowardsEnemy(distanceToKeepInSteps);
        //Try trapping enemy
        trappingStrategy();
        //}
    }

    void trappingStrategy(){
        System.out.println("Do cool stuff.");
    }

    void moveTowardsEnemy(int distanceToKeepInSteps) {
        if (world.bomberManList.size() <= 1) { //no other players
            return;
        }
        BomberMan enemy = findClosestEnemy();

        //We are not yet at the right location
        while (!(manhattanDistanceBomberman(man, enemy) == distanceToKeepInSteps)) {
            int enemyX = enemy.getX_location();
            int enemyY = enemy.getY_location();

            //add our initial position to queue
            queue.add(new Pair(man.getX_location(), man.getY_location()));

            ArrayList<Pair> consideredCoordinates = new ArrayList<>();

            int agentX = man.getX_location();
            int agentY = man.getY_location();

            searchAndGoToLocation(enemyX, enemyY, agentX, agentY, consideredCoordinates, distanceToKeepInSteps);
            queue.clear();
            consideredCoordinates.clear();

            //check if our coordinates are the same: that means no path
            if (agentX == man.getX_location() && agentY == man.getY_location()) {
                //bomb the wall towards the enemy
                System.out.println("No path, creating one!");
                bombTowardsDirection(getEnemyDirection(enemyX, enemyY));
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //try again for a path
            }


        }

        //We arived at location
        System.out.println("Right location and distance!");


    }

    void bombTowardsDirection(MoveUtility.Actions direction){
        switch (direction){
            case UP:
                //move towards wall in up direction
                while(checkMovementPossible(man.getX_location(),man.getY_location()-1)){
                    moveToArea(man.getX_location(),man.getY_location()-1,0);
                }
                //place bomb
                man.move(MoveUtility.Actions.PLACEBOMB);
                //TODO replace with avoidDanger()
                //move back
                for(int i = 0; i < 3; i++){
                    moveToArea(man.getX_location(),man.getY_location()+1,0);
                }
                break;
            case DOWN:
                //move towards wall in up direction
                while(checkMovementPossible(man.getX_location(),man.getY_location()+1)){
                    moveToArea(man.getX_location(),man.getY_location()+1,0);
                }
                //place bomb
                man.move(MoveUtility.Actions.PLACEBOMB);
                //move back
                for(int i = 0; i < 3; i++){
                    moveToArea(man.getX_location(),man.getY_location()-1,0);
                }
                break;
            case RIGHT:
                //move towards wall in up direction
                while(checkMovementPossible(man.getX_location()+1,man.getY_location())){
                    moveToArea(man.getX_location()+1,man.getY_location(),0);
                }
                //place bomb
                man.move(MoveUtility.Actions.PLACEBOMB);
                //move back
                for(int i = 0; i < 3; i++){
                    moveToArea(man.getX_location()-1,man.getY_location(),0);
                }
                break;
            case LEFT:
                //move towards wall in up direction
                while(checkMovementPossible(man.getX_location()-1,man.getY_location())){
                    moveToArea(man.getX_location()-1,man.getY_location(),0);
                }
                //place bomb
                man.move(MoveUtility.Actions.PLACEBOMB);
                //move back
                for(int i = 0; i < 3; i++){
                    moveToArea(man.getX_location()+1,man.getY_location(),0);
                }
                break;


        }


    }

    MoveUtility.Actions getEnemyDirection(int enemyX, int enemyY){
        if (abs(man.getX_location() - enemyX) > abs(man.getY_location() - enemyY)) { //x distance is bigger than y distance
            if((man.getX_location() - enemyX) > 0){ // enemy is to the left of us
                return MoveUtility.Actions.LEFT;
            }
            else{ // enemy is to the right of us
                return MoveUtility.Actions.RIGHT;
            }
        } else {  //y distance is bigger than x distance
            if((man.getY_location() - enemyY) > 0) { // enemy is to above us
                return MoveUtility.Actions.UP;
            }
            else{ // enemy is to below us
                return MoveUtility.Actions.DOWN;
            }

        }


    }

    //Moves the agent out of harms way
    void avoidDanger(){
        //check if there is a dangerzone in our field
        if(world.positions[man.getX_location()][man.getY_location()].dangerousTimer > 0){
            //move out of the way, preferably to a safe field

        }

    }



    //find the closest enemy, and return it
    BomberMan findClosestEnemy() {
        List<Integer> coordinates = new ArrayList<Integer>();
        int amountOfPlayers = world.bomberManList.size() - 1; //minus 1 for index

        int distanceClosestEnemy = 0;
        int closestEnemyID = 0;
        //search for enemy closest
        for (int i = 1; i <= amountOfPlayers; i++) {

            int enemyX = world.bomberManList.get(i).getX_location();
            int enemyY = world.bomberManList.get(i).getY_location();
            int distance = manhattanDistance(enemyX, man.getX_location(), enemyY, man.getY_location());
            if (distanceClosestEnemy < distance) {
                distanceClosestEnemy = distance;
                /**
                coordinates.clear(); //empty list
                coordinates.add(enemyX); //add X of closest enemy
                coordinates.add(enemyY); // add Y of closest enemy
                 **/
                closestEnemyID = i;

            }
        }
        //System.out.println("Closest enemy is ID " + closestEnemyID + " with distance " + distanceClosestEnemy);
        return world.bomberManList.get(closestEnemyID);
    }


    //move agent towards a coordinate. Stops when it gets close.
    void moveToArea(int x, int y, int distanceToKeep){

        while(distanceToKeep < manhattanDistance(x, man.getX_location(), y, man.getY_location())) {

            //TODO replace with enemy direction methodcall
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



    //finds a path, if possible, and moves to target
    void searchAndGoToLocation(int targetX, int targetY, int ownX, int ownY, ArrayList<Pair> consideredCoordinates, int distanceToKeepInSteps) {

        //DEBUG
        //targetX = 8;
        //targetY = 8;

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
            consideredCoordinates.add(new Pair(ownX, ownY));

        }

        //BASE CASE
        if (targetX == ownX && targetY == ownY) {

            System.out.println("Our coordinates are " + man.getX_location() + " " + man.getY_location());
            System.out.println("path = " + consideredCoordinates.toString());

            //Achterstevoren lijst af, checken of manhatten distance 1 is, daaruit nieuwe lijst maken wat uiteindelijk pad is
            ArrayList<Pair> finalPath = new ArrayList<>();

            //Put last element in a variable
            currentPair = consideredCoordinates.get(consideredCoordinates.size() - 1);

            //Add the last element to our final path
            finalPath.add(currentPair);

            //Go through all elements of list
            for (int i = consideredCoordinates.size() - 1; i > 0; i--) {
                Pair comparingPair = consideredCoordinates.get(i);
                //Check if it is possible to go to each other
                if (manhattanDistance(((int) currentPair.getFirst()), (int) comparingPair.getFirst(), (int) currentPair.getSecond(), (int) comparingPair.getSecond()) == 1) {
                    finalPath.add(comparingPair);
                    currentPair = comparingPair;
                }

            }
            System.out.println("FINAL path = " + finalPath.toString());

            for (int i = finalPath.size() - 1; i - distanceToKeepInSteps >= 0; i--) {
                moveToArea((int) finalPath.get(i).getFirst(), (int) finalPath.get(i).getSecond(), 0);
            }
            System.out.println("Done moving, coordinates now are " + man.getX_location() + " " + man.getY_location());
            System.out.println();
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

            searchAndGoToLocation(targetX, targetY, ownX + 1, ownY, consideredCoordinates, distanceToKeepInSteps);

        }
        if (checkMovementPossible(ownX - 1, ownY) && !(consideredCoordinates.contains(new Pair((ownX - 1), ownY)))) {

            searchAndGoToLocation(targetX, targetY, ownX - 1, ownY, consideredCoordinates,distanceToKeepInSteps);

        }
        if (checkMovementPossible(ownX, ownY - 1) && !(consideredCoordinates.contains(new Pair(ownX, (ownY - 1))))) {

            searchAndGoToLocation(targetX, targetY, ownX, ownY - 1, consideredCoordinates,distanceToKeepInSteps);

        }
        if (checkMovementPossible(ownX, ownY + 1) && !(consideredCoordinates.contains(new Pair(ownX, (ownY + 1))))) {

            searchAndGoToLocation(targetX, targetY, ownX, ownY + 1, consideredCoordinates,distanceToKeepInSteps);

        }

        //Start again, since queue isn't empty yet
        searchAndGoToLocation(targetX, targetY, ownX, ownY, consideredCoordinates,distanceToKeepInSteps);
        return;
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
         searchAndGoToLocation(targetX, targetY, ownX-1, ownY, consideredCoordinates);
         }
         }

        //check if we can make a move to the RIGHT
        if (checkMovementPossible(ownX + 1, ownY)) {
            //check if our target is to the right
            //if (targetX == ownX+1 && targetY == ownY && !consideredCoordinates.contains(new Pair(ownX+1, ownY))) {
            //add final step to the list
            consideredCoordinates.add(new Pair(ownX + 1, ownY));
            searchAndGoToLocation(targetX, targetY, ownX + 1, ownY, consideredCoordinates);
            //return consideredCoordinates;
            //}
            //else{
            //    searchAndGoToLocation(targetX, targetY, ownX+1, ownY, consideredCoordinates);
            //}
            //if(!consideredCoordinates.contains(new Pair(ownX+1, ownY))) {
            //  consideredCoordinates.add(new Pair(ownX+1, ownY));
            //searchAndGoToLocation(targetX, targetY, ownX+1, ownY, consideredCoordinates);
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
         searchAndGoToLocation(targetX, targetY, ownX, ownY - 1, consideredCoordinates);
         }
         }


        //See if we can move DOWN
        if (checkMovementPossible(ownX, ownY + 1)) {
            //if (targetX == ownX && targetY == ownY + 1 && !consideredCoordinates.contains(new Pair(ownX, ownY + 1))) {

            //add final step to the list
            consideredCoordinates.add(new Pair(ownX, ownY + 1));
            searchAndGoToLocation(targetX, targetY, ownX, ownY + 1, consideredCoordinates);

            //return consideredCoordinates;
            //}
            //else{
            //    searchAndGoToLocation(targetX, targetY, ownX, ownY + 1, consideredCoordinates);
            //}
            //if (!consideredCoordinates.contains(new Pair(ownX, ownY + 1))) {
            //  consideredCoordinates.add(new Pair(ownX, ownY + 1));
            //searchAndGoToLocation(targetX, targetY, ownX, ownY + 1, consideredCoordinates);
            //}
        }

        //No solution found, return empty list
    }
    **/






    int manhattanDistance(int x0, int x1, int y1, int y0){
        return abs(x1-x0) + abs(y1-y0);
    }

    int manhattanDistanceBomberman(BomberMan bomber1, BomberMan bomber2){
        return manhattanDistance(bomber1.getX_location(),  bomber2.getX_location(),  bomber2.getY_location(),  bomber1.getY_location());
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

    double simplifiedQFunction(){

        return 0;
    }

    //calculates reward for taking an action
    int rewardFunction(int xAgent, int yAgent, MoveUtility.Actions action){

        //Points awarded for killing enemy -- not possible by just one move, has to be done by bomb

        //Points awarded for danger zone being placed on enemy

        //Points penalty for being killed

        //Points penalty for standing in dangerzone, depending on the danger level (timer)

        //Points penalty for idling, but less points deducted than for standing in dangerzone.


        return 0;
    }

    //returns a list of possible actions for a state
    ArrayList<MoveUtility.Actions> possibleActions(int xAgent, int yAgent){
        ArrayList<MoveUtility.Actions> moveList = new ArrayList<>();

        //Check for movements
        if (checkMovementPossible(xAgent - 1, yAgent)) {
            moveList.add(MoveUtility.Actions.LEFT);
        }
        if (checkMovementPossible(xAgent + 1, yAgent)) {
            moveList.add(MoveUtility.Actions.RIGHT);
        }
        if (checkMovementPossible(xAgent, yAgent - 1)) {
            moveList.add(MoveUtility.Actions.UP);
        }
        if (checkMovementPossible(xAgent, yAgent + 1)) {
            moveList.add(MoveUtility.Actions.DOWN);
        }

        if(man.bombCooldown == 0){ //No cooldown
            moveList.add(MoveUtility.Actions.PLACEBOMB);
        }

        //Idling is always possible
        moveList.add(MoveUtility.Actions.IDLE);


        return moveList;
    }

}

