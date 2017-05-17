import org.apache.commons.math3.util.Pair;

import java.io.Serializable;
import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/** Serialization method based on https://www.mkyong.com/java/how-to-read-and-write-java-object-to-a-file/ **/


/**
 * Created by Remco on 29-3-2017.
 */
public class RemcoAI {

    boolean DEBUGPRINTS = false;
    boolean DEBUGPRINTS_QLEARNING = true;

    GameWorld world;
    BomberMan man;
    ArrayList<MoveUtility> moves;
    Queue<Pair> queue = new LinkedList<>();

    NeuralNetRemco neuralNet;

    int RANGE = 2; //TODO add value to bomberman, and use that when placing bombs
    int TIMER_BOMB = 5; //TODO obtain value

    int UTILITY_ENEMY_WILL_STAND_IN_DANGERZONE = 2;
    int UTILITY_DEATH = -100;
    int UTILITY_STANDING_IN_DANGERZONE = -2;
    int UTILITY_IDLING = -1;
    int UTILITY_AGENT_MADE_KILL = 100;

    double DISCOUNT_FACTOR = 0.5;
    double EPSILON_RANDOMNESS = 0.1;
    double GAMMA = 0.6;

    RemcoAI(GameWorld world, BomberMan man, int amountHiddenNodes, int amountHiddenLayers, double learningRate) {
        this.world = world;
        this.man = man;

        //TODO replace with global var
        int amountOfFeatures = 3;

        double[][] currentStateVector = new double[1][amountOfFeatures * (world.gridSize * world.gridSize)];
        currentStateVector[0] = createInputVector();

        double[][] targetVector = {{2, 2, 2, 2, 2, 2}};

        this.neuralNet = new NeuralNetRemco(currentStateVector, amountHiddenNodes, amountHiddenLayers, targetVector, learningRate);
    }

    void play(int distanceToKeepInSteps, double randomMoveChance) {
        while (world.bomberManList.get(0).alive && world.PlayerCheck()) {
            //get to right distance
            while ((manhattanDistanceBomberman(man, findClosestEnemy()) > distanceToKeepInSteps + 1)) { // give a buffer in which we can keep a distance to the enemy. Useful after placing a bomb
                moveTowardsEnemy(distanceToKeepInSteps);
            }
            //try to kill enemy
            trappingStrategy();
            playQLearning(randomMoveChance);

            //Adhere to the timesteps of the game

        }
    }



    void trappingStrategy() {
        if(DEBUGPRINTS) System.out.println();
        if(DEBUGPRINTS) System.out.println("Trapping strategy in progress.");

        MoveUtility.Actions action = getBestAction(man.getX_location(), man.getY_location());
        if(DEBUGPRINTS) System.out.println(action.toString());
        man.setNextMove(action);

    }

    void moveTowardsEnemy(int distanceToKeepInSteps) {
        System.out.println();
        if(DEBUGPRINTS) System.out.println("Moving to enemy strategy in progress.");
        if (world.bomberManList.size() <= 1) { //no other players
            return;
        }
        BomberMan enemy = findClosestEnemy();

        //We are not yet at the right location
        while (!(manhattanDistanceBomberman(man, enemy) <= distanceToKeepInSteps) && man.alive) { //closer is also fine
            int enemyX = enemy.getX_location();
            int enemyY = enemy.getY_location();

            //add our initial position to queue
            queue.add(new Pair(man.getX_location(), man.getY_location()));

            ArrayList<Pair> consideredCoordinates = new ArrayList<>();

            int agentX = man.getX_location();
            int agentY = man.getY_location();

            if(!queue.isEmpty()) {
                searchAndGoToLocation(enemyX, enemyY, agentX, agentY, consideredCoordinates, distanceToKeepInSteps);
            }
            else{
                break;
            }
            queue.clear();
            consideredCoordinates.clear();

            //check if our coordinates are the same: that means no path
            if (agentX == man.getX_location() && agentY == man.getY_location()) {
                //bomb the wall towards the enemy
                if(DEBUGPRINTS) System.out.println("No path, creating one!");
                findAndGoToPathInDirection(getDirectionTarget(enemyX, enemyY));
                bombTowardsDirection(getDirectionTarget(enemyX, enemyY));
                avoidDanger();

                int bombTimer = 0;

                //search for our bomb, if there is one
                for(Bomb bomb : world.activeBombList){
                    //check if our own bomb
                    if(bomb.placedBy == man){
                        bombTimer = bomb.getTimer();
                    }
                }
                //wait for bomb to explode

                try {
                    if(DEBUGPRINTS) System.out.println("Waiting " + (world.ROUND_TIME_MILISECONDS + bombTimer * world.ROUND_TIME_MILISECONDS) + "ms for bomb to explode");
                    Thread.sleep(world.ROUND_TIME_MILISECONDS + bombTimer * world.ROUND_TIME_MILISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                //try again for a path
            }

        }

        //We arrived at location
        if(DEBUGPRINTS) System.out.println("Right location and distance!");

    }

    void bombTowardsDirection(MoveUtility.Actions direction) {
        switch (direction) {
            case UP:
                //move towards wall in up direction
                while (checkMovementPossible(man.getX_location(), man.getY_location() - 1)) {
                    moveToArea(man.getX_location(), man.getY_location() - 1, 0); //keep going up until we hit a wall
                }
                //place bomb
                man.setNextMove(MoveUtility.Actions.PLACEBOMB);
                //avoidDanger();
                /**
                 //move back
                 for (int i = 0; i < 3; i++) {
                 moveToArea(man.getX_location(), man.getY_location() + 1, 0);
                 }
                 **/
                break;
            case DOWN:
                //move towards wall in up direction
                while (checkMovementPossible(man.getX_location(), man.getY_location() + 1)) {
                    moveToArea(man.getX_location(), man.getY_location() + 1, 0);
                }
                //place bomb
                man.setNextMove(MoveUtility.Actions.PLACEBOMB);
                //avoidDanger();
                /**
                 //move back
                 for (int i = 0; i < 3; i++) {
                 moveToArea(man.getX_location(), man.getY_location() - 1, 0);
                 }
                 **/
                break;
            case RIGHT:
                //move towards wall in up direction
                while (checkMovementPossible(man.getX_location() + 1, man.getY_location())) {
                    moveToArea(man.getX_location() + 1, man.getY_location(), 0);
                }
                //place bomb
                man.setNextMove(MoveUtility.Actions.PLACEBOMB);
                //avoidDanger();
                /**
                 //move back
                 for (int i = 0; i < 3; i++) {
                 moveToArea(man.getX_location() - 1, man.getY_location(), 0);
                 }
                 **/
                break;
            case LEFT:
                //move towards wall in up direction
                while (checkMovementPossible(man.getX_location() - 1, man.getY_location())) {
                    moveToArea(man.getX_location() - 1, man.getY_location(), 0);
                }
                //place bomb
                man.setNextMove(MoveUtility.Actions.PLACEBOMB);
                //avoidDanger();
                /**
                 //move back
                 for (int i = 0; i < 3; i++) {
                 moveToArea(man.getX_location() + 1, man.getY_location(), 0);
                 }**/
                break;

        }

    }

    MoveUtility.Actions getDirectionTarget(int enemyX, int enemyY) {
        if (abs(man.getX_location() - enemyX) > abs(man.getY_location() - enemyY)) { //x distance is bigger than y distance
            if ((man.getX_location() - enemyX) > 0) { // enemy is to the left of us
                return MoveUtility.Actions.LEFT;
            } else { // enemy is to the right of us
                return MoveUtility.Actions.RIGHT;
            }
        } else {  //y distance is bigger than x distance
            if ((man.getY_location() - enemyY) > 0) { // enemy is to above us
                return MoveUtility.Actions.UP;
            } else { // enemy is to below us
                return MoveUtility.Actions.DOWN;
            }

        }

    }

    //Moves the agent out of harms way
    void avoidDanger() {

        /**
        int dangerTimerCurrentPosition = world.positions[man.getX_location()][man.getY_location()].dangerousTimer;

        //check if there is a dangerzone in our field
        while (dangerTimerCurrentPosition > 0) {
            //get all possible actions
            ArrayList<MoveUtility.Actions> allActions = giveAllPossibleActions(man.getX_location(),man.getY_location());

            //take the one that will lead us to a zone without danger
            if(allActions.contains(MoveUtility.Actions.UP)){
                if(world.positions[man.getX_location()][man.getY_location()-1].dangerousTimer <= dangerTimerCurrentPosition){ //going up will put us in a safer, or as safe, place
                    moveToArea(man.getX_location(), man.getY_location()-1, 0);
                }
            }
            if(allActions.contains(MoveUtility.Actions.DOWN)){
                if(world.positions[man.getX_location()][man.getY_location()+1].dangerousTimer <= dangerTimerCurrentPosition){
                    moveToArea(man.getX_location(), man.getY_location()+1, 0);
                }
            }
            if(allActions.contains(MoveUtility.Actions.RIGHT)){
                if(world.positions[man.getX_location()+1][man.getY_location()].dangerousTimer <= dangerTimerCurrentPosition){
                    moveToArea(man.getX_location()+1, man.getY_location(), 0);
                }
            }
            if(allActions.contains(MoveUtility.Actions.LEFT)){
                if(world.positions[man.getX_location()-1][man.getY_location()].dangerousTimer <= dangerTimerCurrentPosition){
                    moveToArea(man.getX_location()-1, man.getY_location(), 0);
                }
            }

            //update danger value
            dangerTimerCurrentPosition = world.positions[man.getX_location()][man.getY_location()].dangerousTimer;



         }**/
        //alternative would be using getBestAction, but that includes placing bombs

        int dangerTimerCurrentPosition = world.positions[man.getX_location()][man.getY_location()].dangerousTimer;

        //check if there is a dangerzone in our field
        while (dangerTimerCurrentPosition > 0) {
            man.setNextMove(getBestAction(man.getX_location(), man.getY_location()));
            //update danger value
            dangerTimerCurrentPosition = world.positions[man.getX_location()][man.getY_location()].dangerousTimer;
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
    void moveToArea(int x, int y, int distanceToKeep) {

        while (distanceToKeep < manhattanDistance(x, man.getX_location(), y, man.getY_location())) {

            //TODO replace with enemy direction methodcall
            if (abs(man.getX_location() - x) > abs(man.getY_location() - y)) { //x distance is bigger than y distance
                if ((man.getX_location() - x) > 0) { // enemy is to the left of us
                    man.setNextMove(MoveUtility.Actions.LEFT);
                    
                } else { // enemy is to the right of us
                    man.setNextMove(MoveUtility.Actions.RIGHT);
                    
                }
            } else {  //y distance is bigger than x distance
                if ((man.getY_location() - y) > 0) { // enemy is to above us
                    man.setNextMove(MoveUtility.Actions.UP);
                    
                } else { // enemy is to below us
                    man.setNextMove(MoveUtility.Actions.DOWN);
                    
                }

            }
        }
    }



    void searchAndGoToLocation(int targetX, int targetY, int ownX, int ownY){
        ArrayList<Pair> consideredCoordinates = new ArrayList<>();
        queue.add(new Pair(man.getX_location(), man.getY_location()));
        searchAndGoToLocation(targetX, targetY, ownX, ownY, consideredCoordinates, 0);
        queue.clear();

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
        Pair currentPair;
        try {
            currentPair = queue.remove();
        }catch (NoSuchElementException E){
            return; //prevents crashing
        }
        //can't dequeue twice and still get the same pair.
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

            //System.out.println("Our coordinates are " + man.getX_location() + " " + man.getY_location());
            //System.out.println("path = " + consideredCoordinates.toString());

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
            if(DEBUGPRINTS) System.out.println("FINAL path = " + finalPath.toString());

            for (int i = finalPath.size() - 1; i - distanceToKeepInSteps >= 0; i--) {
                moveToArea((int) finalPath.get(i).getFirst(), (int) finalPath.get(i).getSecond(), 0);
            }
            //System.out.println("Done moving, coordinates now are " + man.getX_location() + " " + man.getY_location());
            //System.out.println();
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
            searchAndGoToLocation(targetX, targetY, ownX - 1, ownY, consideredCoordinates, distanceToKeepInSteps);
        }
        if (checkMovementPossible(ownX, ownY - 1) && !(consideredCoordinates.contains(new Pair(ownX, (ownY - 1))))) {
            searchAndGoToLocation(targetX, targetY, ownX, ownY - 1, consideredCoordinates, distanceToKeepInSteps);
        }
        if (checkMovementPossible(ownX, ownY + 1) && !(consideredCoordinates.contains(new Pair(ownX, (ownY + 1))))) {
            searchAndGoToLocation(targetX, targetY, ownX, ownY + 1, consideredCoordinates, distanceToKeepInSteps);
        }

        //Start again, since queue isn't empty yet
        searchAndGoToLocation(targetX, targetY, ownX, ownY, consideredCoordinates, distanceToKeepInSteps);
        return;
    }

    /**
     * //See if we can move LEFT
     * if (checkMovementPossible(ownX-1, ownY)) {
     * if (targetX == ownX-1 && targetY == ownY) { //found target
     * //once we are back at our own position, move to the found path
     * /**
     * man.setNextMove(MoveUtility.Actions.LEFT);
     * try {
     * Thread.sleep(500);
     * } catch (InterruptedException e) {
     * e.printStackTrace();
     * }
     * <p>
     * return consideredCoordinates;
     * }
     * if(!consideredCoordinates.contains(new Pair(ownX-1, ownY))) {
     * consideredCoordinates.add(new Pair(ownX-1, ownY));
     * searchAndGoToLocation(targetX, targetY, ownX-1, ownY, consideredCoordinates);
     * }
     * }
     * <p>
     * //check if we can make a move to the RIGHT
     * if (checkMovementPossible(ownX + 1, ownY)) {
     * //check if our target is to the right
     * //if (targetX == ownX+1 && targetY == ownY && !consideredCoordinates.contains(new Pair(ownX+1, ownY))) {
     * //add final step to the list
     * consideredCoordinates.add(new Pair(ownX + 1, ownY));
     * searchAndGoToLocation(targetX, targetY, ownX + 1, ownY, consideredCoordinates);
     * //return consideredCoordinates;
     * //}
     * //else{
     * //    searchAndGoToLocation(targetX, targetY, ownX+1, ownY, consideredCoordinates);
     * //}
     * //if(!consideredCoordinates.contains(new Pair(ownX+1, ownY))) {
     * //  consideredCoordinates.add(new Pair(ownX+1, ownY));
     * //searchAndGoToLocation(targetX, targetY, ownX+1, ownY, consideredCoordinates);
     * //}
     * }
     * /**
     * //See if we can move UP
     * if (checkMovementPossible(ownX, ownY-1)) {
     * if (targetX == ownX && targetY == ownY-1) {
     * /**
     * man.setNextMove(MoveUtility.Actions.UP);
     * try {
     * Thread.sleep(500);
     * } catch (InterruptedException e) {
     * e.printStackTrace();
     * }
     * return consideredCoordinates;
     * }
     * if (!consideredCoordinates.contains(new Pair(ownX, ownY - 1))) {
     * consideredCoordinates.add(new Pair(ownX, ownY - 1));
     * searchAndGoToLocation(targetX, targetY, ownX, ownY - 1, consideredCoordinates);
     * }
     * }
     * <p>
     * <p>
     * //See if we can move DOWN
     * if (checkMovementPossible(ownX, ownY + 1)) {
     * //if (targetX == ownX && targetY == ownY + 1 && !consideredCoordinates.contains(new Pair(ownX, ownY + 1))) {
     * <p>
     * //add final step to the list
     * consideredCoordinates.add(new Pair(ownX, ownY + 1));
     * searchAndGoToLocation(targetX, targetY, ownX, ownY + 1, consideredCoordinates);
     * <p>
     * //return consideredCoordinates;
     * //}
     * //else{
     * //    searchAndGoToLocation(targetX, targetY, ownX, ownY + 1, consideredCoordinates);
     * //}
     * //if (!consideredCoordinates.contains(new Pair(ownX, ownY + 1))) {
     * //  consideredCoordinates.add(new Pair(ownX, ownY + 1));
     * //searchAndGoToLocation(targetX, targetY, ownX, ownY + 1, consideredCoordinates);
     * //}
     * }
     * <p>
     * //No solution found, return empty list
     * }
     **/

    int manhattanDistance(int x0, int x1, int y1, int y0) {
        return abs(x1 - x0) + abs(y1 - y0);
    }

    int manhattanDistance(BomberMan bomber, int x0, int y0) {
        return abs(bomber.getX_location() - x0) + abs(bomber.getY_location()- y0);
    }

    int manhattanDistanceBomberman(BomberMan bomber1, BomberMan bomber2) {
        return manhattanDistance(bomber1.getX_location(), bomber2.getX_location(), bomber2.getY_location(), bomber1.getY_location());
    }

    boolean checkMovementPossible(int targetX, int targetY) {
        //out of bounds
        if (targetX > world.gridSize - 1 || targetY > world.gridSize - 1 || targetX < 0 || targetY < 0) {
            return false;
        }

        //Return a false if there is a hardwall or softwall at this position
        if (world.positions[targetX][targetY].getType() == WorldPosition.Fieldtypes.HARDWALL
                || world.positions[targetX][targetY].getType() == WorldPosition.Fieldtypes.SOFTWALL) {
            return false;

        }
        return true;

    }

    //calculates reward for taking an action, given coordinates and an action
    int rewardFunction(int xAgent, int yAgent, MoveUtility.Actions action) {

        //
        //Points awarded for killing enemy -- not possible by just one move, has to be done by bomb
        //

        if (world.agentMadeKill) {
            world.agentMadeKill = false;
            return UTILITY_AGENT_MADE_KILL;
        }

        //
        //Points awarded for danger zone being placed on enemy
        //
        if (action == MoveUtility.Actions.PLACEBOMB && checkEnemyInPotentialDangerzone(man.getX_location(), man.getY_location(), RANGE)) { //In case we are able to place a bomb, and we will trap someone with it.
            return UTILITY_ENEMY_WILL_STAND_IN_DANGERZONE;
        }

        //
        //Points penalty for being killed
        //
        if (action != MoveUtility.Actions.PLACEBOMB) { //any movement
            //Check if moving to a direction will get us killed
            switch (action) {
                case UP:
                    if (world.positions[xAgent][yAgent - 1].dangerousTimer == 1) { //moving here will kill us
                        return UTILITY_DEATH;
                    }
                    break;
                case DOWN:
                    if (world.positions[xAgent][yAgent + 1].dangerousTimer == 1) { //moving here will kill us
                        return UTILITY_DEATH;
                    }
                    break;
                case LEFT:
                    if (world.positions[xAgent - 1][yAgent].dangerousTimer == 1) { //moving here will kill us
                        return UTILITY_DEATH;
                    }
                    break;
                case RIGHT:
                    if (world.positions[xAgent + 1][yAgent].dangerousTimer == 1) { //moving here will kill us
                        return UTILITY_DEATH;
                    }
                    break;
                case IDLE:
                    if (world.positions[xAgent][yAgent].dangerousTimer == 1) { //staying here will kill us
                        return UTILITY_DEATH;
                    }
                    break;
            }

        }
        //
        //Points penalty for standing in dangerzone, depending on the danger level (timer)
        //
        int penalty = 0;
        if (action != MoveUtility.Actions.PLACEBOMB) { //any movement

            Bomb closestBomb = null;
            if (!world.activeBombList.isEmpty()) {
                closestBomb = getClosestBomb(man.getX_location(), man.getY_location());
                //System.out.println("Bomb is in direction " + getDirectionTarget(closestBomb.x_location, closestBomb.y_location).toString());

            }
            //Check if moving to a direction will get us killed
            switch (action) {
                case UP:
                    if (world.positions[xAgent][yAgent - 1].dangerousTimer > 0) { //moving here will be dangerous
                        //penalty added for moving towards a bomb
                        if (closestBomb != null && (getDirectionTarget(closestBomb.x_location, closestBomb.y_location) == MoveUtility.Actions.UP)) { //there is a bomb, and it's in the direction we want to go
                            penalty = -10;
                        }
                        //returns a value that gets higher when the bomb gets closer to 0. Gives highest penalty for time==1, since time==0 equals death anyway.
                        return UTILITY_STANDING_IN_DANGERZONE + (world.positions[xAgent][yAgent - 1].dangerousTimer - 1) * (-UTILITY_STANDING_IN_DANGERZONE / TIMER_BOMB) + penalty;
                    }
                    break;
                case DOWN:
                    if (world.positions[xAgent][yAgent + 1].dangerousTimer > 0) {
                        if (closestBomb != null && (getDirectionTarget(closestBomb.x_location, closestBomb.y_location) == MoveUtility.Actions.DOWN)) { //there is a bomb, and it's in the direction we want to go
                            penalty = -10;
                        }
                        return UTILITY_STANDING_IN_DANGERZONE + (world.positions[xAgent][yAgent + 1].dangerousTimer - 1) * (-UTILITY_STANDING_IN_DANGERZONE / TIMER_BOMB) + penalty;
                    }
                    break;
                case LEFT:
                    if (world.positions[xAgent - 1][yAgent].dangerousTimer > 0) {
                        if (closestBomb != null && (getDirectionTarget(closestBomb.x_location, closestBomb.y_location) == MoveUtility.Actions.LEFT)) { //there is a bomb, and it's in the direction we want to go
                            penalty = -10;
                        }
                        return UTILITY_STANDING_IN_DANGERZONE + (world.positions[xAgent - 1][yAgent].dangerousTimer - 1) * (-UTILITY_STANDING_IN_DANGERZONE / TIMER_BOMB) + penalty;
                    }
                    break;
                case RIGHT:
                    if (world.positions[xAgent + 1][yAgent].dangerousTimer > 0) {
                        if (closestBomb != null && (getDirectionTarget(closestBomb.x_location, closestBomb.y_location) == MoveUtility.Actions.RIGHT)) { //there is a bomb, and it's in the direction we want to go
                            penalty = -10;
                        }
                        return UTILITY_STANDING_IN_DANGERZONE + (world.positions[xAgent + 1][yAgent].dangerousTimer - 1) * (-UTILITY_STANDING_IN_DANGERZONE / TIMER_BOMB) + penalty;
                    }
                    break;
                case IDLE:
                    if (world.positions[xAgent][yAgent].dangerousTimer > 0) { //staying here will be dangerous
                        if (closestBomb.x_location == xAgent && closestBomb.y_location == yAgent) {
                            penalty = -20;
                        }
                        return UTILITY_STANDING_IN_DANGERZONE + (world.positions[xAgent][yAgent].dangerousTimer - 1) * (-UTILITY_STANDING_IN_DANGERZONE / TIMER_BOMB) + penalty;
                    }
                    break;
            }

            //
            //Points penalty for idling, but less points deducted than for standing in dangerzone.
            //Gets checked after previous statement, because the penalty for idling in a dangerzone is higher.
            //
            if (action == MoveUtility.Actions.IDLE) {
                return UTILITY_IDLING;
            }

        }
        return 0;
    }

    //returns a list of possible actions for a state
    ArrayList<MoveUtility.Actions> giveAllPossibleActions(int xAgent, int yAgent) {
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

        if (man.bombCooldown == 0) { //No cooldown
            moveList.add(MoveUtility.Actions.PLACEBOMB);
        }

        //Idling is always possible
        moveList.add(MoveUtility.Actions.IDLE);

        return moveList;
    }

    //Return the action that corresponds to a certain index. i.e. 0 is left, 1 is right, 2 is up, etc
    MoveUtility.Actions giveActionAtIndex(int index) {
        switch (index) {
            case 0:
                return MoveUtility.Actions.LEFT;
            case 1:
                return MoveUtility.Actions.RIGHT;
            case 2:
                return MoveUtility.Actions.UP;
            case 3:
                return MoveUtility.Actions.DOWN;
            case 4:
                return MoveUtility.Actions.PLACEBOMB;
            case 5:
                return MoveUtility.Actions.IDLE;

        }

        return null;
    }

    MoveUtility.Actions getBestAction(int xAgent, int yAgent) {

        int maxReward = -10000; //negative value, since we can have a 'least worst' option that is <0 points
        MoveUtility.Actions bestAction = null;

        ArrayList<MoveUtility.Actions> possibleActions = giveAllPossibleActions(man.getX_location(), man.getY_location());

        for (MoveUtility.Actions action : possibleActions) {
            if (rewardFunction(xAgent, yAgent, action) > maxReward) {
                maxReward = rewardFunction(xAgent, yAgent, action);
                bestAction = action;
            }
        }
        return bestAction;
    }

    //Check if, when placing a bomb here, an enemy will stand in a dangerzone
    boolean checkEnemyInPotentialDangerzone(int x_location, int y_location, int range) {
        for (int yTemp = y_location; yTemp <= y_location + range; yTemp++) {
            if (yTemp >= 0 && yTemp < world.gridSize) {
                if (!(world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.SOFTWALL)
                        && !world.positions[x_location][yTemp].bombermanList.isEmpty() // there is a bomberman here...
                        && !world.positions[x_location][yTemp].bombermanList.contains(man)) { //and it ain't us
                    return true;
                }
            }
        }

        for (int yTemp = y_location; yTemp >= y_location - range; yTemp--) {
            if (yTemp >= 0 && yTemp < world.gridSize) {
                if (!(world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.SOFTWALL)
                        && !world.positions[x_location][yTemp].bombermanList.isEmpty() // there is a bomberman here...
                        && !world.positions[x_location][yTemp].bombermanList.contains(man)) { //and it ain't us
                    return true;
                }
            }
        }
        for (int xTemp = x_location; xTemp <= x_location + range; xTemp++) {
            if (xTemp >= 0 && xTemp < world.gridSize) {
                if (!(world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.SOFTWALL)
                        && !world.positions[xTemp][y_location].bombermanList.isEmpty() // there is a bomberman here...
                        && !world.positions[xTemp][y_location].bombermanList.contains(man)) { //and it ain't us
                    return true;
                }
            }
        }
        for (int xTemp = x_location; xTemp >= x_location - range; xTemp--) {
            if (xTemp >= 0 && xTemp < world.gridSize) {
                if (!(world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.SOFTWALL)
                        && !world.positions[xTemp][y_location].bombermanList.isEmpty() // there is a bomberman here...
                        && !world.positions[xTemp][y_location].bombermanList.contains(man)) { //and it ain't us
                    return true;
                }
            }
        }
        return false;
    }

    Bomb getClosestBomb(int ownX, int ownY) {
        Bomb closestBomb = null;
        int closestBombDistance = 999;
        for (Bomb bomb : world.activeBombList) {
            if (manhattanDistance(ownX, bomb.x_location, bomb.y_location, ownY) < closestBombDistance) {
                closestBombDistance = manhattanDistance(ownX, bomb.x_location, bomb.y_location, ownY);
                closestBomb = bomb;
            }
        }
        return closestBomb;
    }

    //TODO reduce randomMoveChance over time
    void playQLearning(double randomMoveChance) {

        //TODO replace with global var
        int amountOfFeatures = 3;
        //TODO replace with multiple networks
        int amountOfMovementOptions = 6;

        //variables
        double[] previousState;
        MoveUtility.Actions previousAction;
        MoveUtility.Actions action;
        int rewardForAction;

        double[][] currentStateVector = new double[1][amountOfFeatures * (world.gridSize * world.gridSize)];
        currentStateVector[0] = createInputVector();

        //create empty target vector for constructor
        double[][] targetVector = {{50, 50, 50, 50, 50, 50}}; //TODO make this an input variable

        /** Q-learning starts here **/

        //Keep making moves until the game ends
        while (world.bomberManList.get(0).alive && world.PlayerCheck()) {
            /** Do a forwardpass **/
            neuralNet.forwardPass(currentStateVector[0]);
            //System.out.println(Arrays.toString(neuralNet.getOutputLayer()));

            /** pick a random move or highest Q-value **/
            ArrayList<MoveUtility.Actions> allActions = giveAllPossibleActions(man.getX_location(), man.getY_location());

            //generate random number between 0.000 and 1.000
            double random = (double) ThreadLocalRandom.current().nextInt(0 * 1000, 1 * 1000 + 1) / 1000;
            if (random > (1 - randomMoveChance)) { //take random action
                //Get all possible moves

                int randomActionValue = new Random().nextInt(allActions.size());
                action = allActions.get(randomActionValue);
                if(DEBUGPRINTS_QLEARNING) System.out.println("Random action was " + action);

            } else { //use highest q-value
                //get the highest Q-value index
                double[] outputLayer = neuralNet.getOutputLayer();
                //take node with highest activation
                int actionIndex = getArrayIndexHighestValue(outputLayer);
                action = giveActionAtIndex(actionIndex);

                //check if action is possible, else take the next highest one
                while (!allActions.contains(action)) {
                    outputLayer[actionIndex] = 0;
                    actionIndex = getArrayIndexHighestValue(outputLayer);
                    action = giveActionAtIndex(actionIndex);

                }
                if(DEBUGPRINTS_QLEARNING) System.out.println("Q-value action was " + action);
                if(DEBUGPRINTS_QLEARNING) System.out.println("QValue was " + outputLayer[actionIndex]);

            }

            //Save current state and action
            //TODO add action to stateVector
            previousState = currentStateVector[0];
            previousAction = action;

            /** make move **/
            rewardForAction = rewardFunction(man.getX_location(), man.getY_location(), action);
            man.setNextMove(action);

            if(DEBUGPRINTS_QLEARNING) System.out.println("Reward was " + rewardForAction);
            if(DEBUGPRINTS_QLEARNING) System.out.println();

            /** Feed forward again **/
            //get the new state as vector
            currentStateVector[0] = createInputVector();
            neuralNet.forwardPass(currentStateVector[0]);

            /** Calculate Q-target **/
            //get the highest Q-value index
            double[] outputLayer = neuralNet.getOutputLayer();
            //take node with highest activation
            int actionIndex = getArrayIndexHighestValue(outputLayer);
            action = giveActionAtIndex(actionIndex);

            //check if action is possible, else take the next highest one
            while (!allActions.contains(action)) {
                outputLayer[actionIndex] = 0;
                actionIndex = getArrayIndexHighestValue(outputLayer);
                action = giveActionAtIndex(actionIndex);

            }
            double QTarget = rewardForAction + GAMMA * outputLayer[actionIndex];

            /** Update the target with our new value **/
            //get current target
            double[] targetOutput = neuralNet.getTargetOutput();
            //change the value
            targetOutput[actionIndex] = QTarget;
            targetVector[0] = targetOutput;
            //neuralNet.changeTargetOutputSet(targetVector);
            neuralNet.targetOutput = targetOutput;

            /** Do the backward pass **/
            neuralNet.backwardsPass();

            //adhere the timesteps
            //man.waitForNextTurn();
        }

    }


    double[] createInputVector() {
        int amountOfFeatures = 3;
        /**
         * Features:
         * [0] == empty space or soft wall {0,1}
         * [1] == Dangerzone {0-1}
         * [2] == Bomberman (self not included) {0,1}
         */

        double inputVector[] = new double[amountOfFeatures * (world.gridSize * world.gridSize)];
        //populate inputVector
        for (int index = 0; index < (world.gridSize * world.gridSize); index++) {

            int x = index % world.gridSize; // x goes from 0-8
            int y = index / world.gridSize; // y goes from 0-8 as well. Increments after 8 iterations

            //[0] == empty space or soft wall {0,1}
            //if (index % amountOfFeatures == 0) {
            if (world.positions[x][y].type == WorldPosition.Fieldtypes.SOFTWALL) {
                inputVector[0 + amountOfFeatures * index] = 1; //offset = 0
                //inputVector[index] = 1;
            } else {
                inputVector[0 + amountOfFeatures * index] = 0;
                //inputVector[index] = 0;
            }

            //[1] == Dangerzone {0-1}
            //if (index % amountOfFeatures == 1) {
            if (world.positions[x][y].dangerousTimer > 0) {

                double stepSize = (double) 1 / (double) TIMER_BOMB;
                double dangerScale = ((TIMER_BOMB - world.positions[x][y].dangerousTimer) + 1) * stepSize;

                inputVector[1 + amountOfFeatures * index] = dangerScale; //offset = 1
                //inputVector[index] = 1;
            } else {
                inputVector[1 + amountOfFeatures * index] = 0;
                //inputVector[index] = 0;
            }
            //}

            //[2] == Bomberman (self not included) {0,1}
            //if (index % amountOfFeatures == 2) {
            if (!world.positions[x][y].bombermanList.isEmpty() && !world.positions[x][y].bombermanList.contains(man)) { //contains a bomberman, that isn't us
                inputVector[2 + amountOfFeatures * index] = 1; //offset = 2
                //inputVector[index] = 1;
            } else {
                inputVector[2 + amountOfFeatures * index] = 0;
                //inputVector[index] = 0;
            }
            //}
        }

        //double QValue = // reward_next + discountRate * maxQ(next_state, this_action)

        //System.out.println("Input vector made");

        return inputVector;

    }

    int getArrayIndexHighestValue(double[] array) {
        double highestValue = 0;
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > highestValue) {
                highestValue = array[i];
                index = i;
            }
        }
        return index;
    }

    void setBomberman(BomberMan man) {
        this.man = man;
    }

    //Get as close to a direction (i.e. down/south) as possible
    void findAndGoToPathInDirection(MoveUtility.Actions direction) {

        if(DEBUGPRINTS) System.out.println("findAndGoToPathInDirection");

        //TODO remove unnecessary search in the wrong direction

        int x_target = -10;
        int y_target = -10;
        int manhattanDistanceToTarget = world.gridSize * 2;
        int oldManhattanDistance = world.gridSize * 2 + 1;

        //look at manhattandistannce around ourselves for empty spaces
        int searchDistance = 1;
        while (oldManhattanDistance > manhattanDistanceToTarget && !(searchDistance > world.gridSize * 2)) {

            for (int x = 0 - searchDistance; x <= searchDistance; x++) {
                for (int y = 0 - searchDistance; y <= searchDistance; y++) {
                    //check if this is a valid location
                    if (checkMovementPossible(man.getX_location() + x, man.getY_location() + y)) {
                        //check if move is in the right direction
                        if ((getDirectionTarget(man.getX_location() + x, man.getY_location() + y) == direction) && (x != 0 || y != 0)) { // Don't move when both are 0: would be no move
                            //look around agent's coordinates
                            if (world.positions[man.getX_location() + x][man.getY_location() + y].type == WorldPosition.Fieldtypes.EMPTY) {
                                //old distance is longer
                                if (manhattanDistanceToTarget > manhattanDistance(man, man.getX_location() + x, man.getY_location() + y)) {
                                    manhattanDistanceToTarget = manhattanDistance(man, man.getX_location() + x, man.getY_location() + y);
                                    x_target = man.getX_location() + x;
                                    y_target = man.getY_location() + y;
                                    oldManhattanDistance = manhattanDistanceToTarget;///TODO
                                }

                            }
                        }
                    }
                }
            }
            //increment the search area
            searchDistance++;
            if (manhattanDistanceToTarget == oldManhattanDistance && x_target != -10 && y_target != -10) { //distance did not change, old location was best bet
                //if now no empty space is found, go to the closest location to our agent, from the previous step
                searchAndGoToLocation(x_target, y_target, man.getX_location(), man.getY_location());
            }
        }

    }

    void writeNetworkToFile() {
        try {
            FileOutputStream f = new FileOutputStream(new File("NeuralNetwork"));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(this.neuralNet);

            o.close();
            f.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
            System.err.println(e);

        }
    }

    void readNetworkFromFile(){

    }


}

