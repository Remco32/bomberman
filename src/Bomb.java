import java.util.concurrent.TimeUnit;

/**
 * Created by joseph on 09/02/2017.
 */
public class Bomb {
    private Boolean DEBUGPRINT = true; // Activates debug prints for bomb

    private int timer;
    private int cleanupTimer;
    private int range;
    int x_location;
    int y_location;
    private int id;
    BomberMan placedBy;
    Boolean exploded;
    GameWorld world;

    Bomb(int x, int y, BomberMan by, GameWorld world) {

        this.x_location = x;
        this.y_location = y;
        this.id = world.activeBombList.size()+world.explodedBombList.size();
        this.placedBy = by;
        this.world = world;
        timer = 5;
        range = 2; //TODO make variable: time + powerup
        exploded = false;
    }

    void countdown() { //Counts down until the bomb explodes. Used by the gameloop.
        if (exploded) return;
        if (timer > 0){
            timer--;
            updateDangerzones();
        }


        else Explode();
    }

    void cleanupCountdown(){
        if (cleanupTimer > 0){
            cleanupTimer--;
        }
        if(exploded && cleanupTimer == 0) {
            cleanExplosion();
        }

    }


    private void ModifyPoints(BomberMan man, int amount){ //Method for adding or subtracting points from an agent
        int points = man.points.get(man.points.size()-1)+amount;
        man.points.add(points);
    }

    //Sets and updates dangerzones in world
    void createDangerzones(){
        for (int yTemp = y_location; yTemp <= y_location + range; yTemp++) {
            if (yTemp >= 0 && yTemp < world.gridSize) {
                if (!(world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.SOFTWALL))  world.positions[x_location][yTemp].dangerousTimer = timer;
                //if(ExplodeHit(x_location,yTemp)) yTemp = y_location + range+1;
            }
        }
        //check for exploding down
        for (int yTemp = y_location; yTemp >= y_location - range; yTemp--) {
            if (yTemp >= 0 && yTemp < world.gridSize) {
                if (!(world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.SOFTWALL)) world.positions[x_location][yTemp].dangerousTimer = timer;
                //if(ExplodeHit(x_location,yTemp)) yTemp = y_location-range-1;
            }
        }
        // check for exploding left
        for (int xTemp = x_location; xTemp <= x_location + range; xTemp ++) {
            if (xTemp  >= 0 && xTemp  < world.gridSize) {
                if (!(world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.SOFTWALL)) world.positions[xTemp][y_location].dangerousTimer = timer;
                //if(ExplodeHit(xTemp,y_location)) xTemp = x_location + range +1;
            }
        }
        // check for exploding right
        for (int xTemp = x_location; xTemp >= x_location - range; xTemp--) {
            if (xTemp >= 0 && xTemp < world.gridSize) {
                if (!(world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.SOFTWALL)) world.positions[xTemp][y_location].dangerousTimer = timer;
                //if(ExplodeHit(xTemp,y_location)) xTemp = x_location - range -1;
            }
        }
    }



    //TODO fix bug where an explosion can only be drawn once: next time it won't be drawn.
    private void Explode() {
        if (exploded) return;

        // check for exploding up
        for (int yTemp = y_location; yTemp <= y_location + range; yTemp++) {
            if (yTemp >= 0 && yTemp < world.gridSize) {
                if (!(world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.SOFTWALL))  world.positions[x_location][yTemp].type = WorldPosition.Fieldtypes.EXPLOSION;
               if(ExplodeHit(x_location,yTemp)) yTemp = y_location + range+1;
            }
        }
        //check for exploding down
        for (int yTemp = y_location; yTemp >= y_location - range; yTemp--) {
            if (yTemp >= 0 && yTemp < world.gridSize) {
                if (!(world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.SOFTWALL)) world.positions[x_location][yTemp].type = WorldPosition.Fieldtypes.EXPLOSION;
               if(ExplodeHit(x_location,yTemp)) yTemp = y_location-range-1;
            }
        }
        // check for exploding left
        for (int xTemp = x_location; xTemp <= x_location + range; xTemp ++) {
            if (xTemp  >= 0 && xTemp  < world.gridSize) {
                if (!(world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.SOFTWALL)) world.positions[xTemp][y_location].type = WorldPosition.Fieldtypes.EXPLOSION;
                if(ExplodeHit(xTemp,y_location)) xTemp = x_location + range +1;
            }
        }
        // check for exploding right
        for (int xTemp = x_location; xTemp >= x_location - range; xTemp--) {
            if (xTemp >= 0 && xTemp < world.gridSize) {
                if (!(world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.SOFTWALL)) world.positions[xTemp][y_location].type = WorldPosition.Fieldtypes.EXPLOSION;
                if(ExplodeHit(xTemp,y_location)) xTemp = x_location - range -1;
            }
        }

        exploded = true;

        //world.positions[x_location][y_location].deleteBomb();

        //Clean explosion graphic next turn
        cleanupTimer = 2;
        //cleanExplosion();
    }


    void cleanExplosion(){

        world.positions[x_location][y_location].deleteBomb();

        // check for exploding up
        for (int yTemp = y_location; yTemp <= y_location + range; yTemp++) {
            if (yTemp >= 0 && yTemp < world.gridSize) {
                if (!(world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.SOFTWALL))  world.positions[x_location][yTemp].type = WorldPosition.Fieldtypes.EMPTY;
            }
        }
        //check for exploding down
        for (int yTemp = y_location; yTemp >= y_location - range; yTemp--) {
            if (yTemp >= 0 && yTemp < world.gridSize) {
                if (!(world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[x_location][yTemp].getType() == WorldPosition.Fieldtypes.SOFTWALL)) world.positions[x_location][yTemp].type = WorldPosition.Fieldtypes.EMPTY;
            }
        }
        // check for exploding left
        for (int xTemp = x_location; xTemp <= x_location + range; xTemp ++) {
            if (xTemp  >= 0 && xTemp  < world.gridSize) {
                if (!(world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.SOFTWALL)) world.positions[xTemp][y_location].type = WorldPosition.Fieldtypes.EMPTY;
            }
        }
        // check for exploding right
        for (int xTemp = x_location; xTemp >= x_location - range; xTemp--) {
            if (xTemp >= 0 && xTemp < world.gridSize) {
                if (!(world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.HARDWALL
                        || world.positions[xTemp][y_location].getType() == WorldPosition.Fieldtypes.SOFTWALL)) world.positions[xTemp][y_location].type = WorldPosition.Fieldtypes.EMPTY;
            }
        }

        //cleanDangerzones();

    }


    private Boolean ExplodeHit(int xTemp, int yTemp){
        if (!world.positions[xTemp][yTemp].bombermanList.isEmpty()) {
            for (BomberMan man : world.positions[xTemp][yTemp].bombermanList) { //check if a bomberman is hit
                ModifyPoints(placedBy,100); // 100 points for killing
                ModifyPoints(man,-300);//-300 points for dying
                man.Die();
                if(DEBUGPRINT) {
                    System.out.println("player " + man.id + " has been killed by player " + placedBy.id);
                }
                if(placedBy.id == 0){ //killed by our agent
                    //notify agent
                    world.agentMadeKill = true;
                }

            }
            world.positions[xTemp][yTemp].bombermanList.clear();
        }

        if (world.positions[xTemp][yTemp].type == WorldPosition.Fieldtypes.HARDWALL) {
            return true;
        } else if (world.positions[xTemp][yTemp].type == WorldPosition.Fieldtypes.SOFTWALL) { //type 1 is a softwall
            world.positions[xTemp][yTemp].type = WorldPosition.Fieldtypes.EMPTY; // change the wall to an empty space
            ModifyPoints(placedBy,20);//award 20 points to the agent for destroying a wall
            return true;
        }
        return false;
    }

    int getTimer(){
        return timer;
    }

    //alias for readability
    void updateDangerzones(){
        createDangerzones();
    }

}

