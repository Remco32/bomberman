/**
 * Created by joseph on 09/02/2017.
 */
public class Bomb {
    private Boolean PRINT = true; // debugging

    private int timer;
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

    void Countdown() { //Counts down until the bomb explodes. Used by the gameloop.
        if (exploded) return;
        if (timer > 0) timer--;
        else Explode();
    }


    private void ModifyPoints(BomberMan man, int amount){ //Method for adding or subtracting points from an agent
        int points = man.points.get(man.points.size()-1)+amount;
        man.points.add(points);
    }


    //TODO add explosion graphic
    private void Explode() {
        if (exploded) return;

        // check for exploding up
        for (int yTemp = y_location; yTemp <= y_location + range; yTemp++) {
            if (yTemp >= 0 && yTemp < world.gridSize) {
               if(ExplodeHit(x_location,yTemp)) yTemp = y_location + range+1;
            }
        }
        //check for exploding down
        for (int yTemp = y_location; yTemp >= y_location - range; yTemp--) {
            if (yTemp >= 0 && yTemp < world.gridSize) {
               if(ExplodeHit(x_location,yTemp)) yTemp = y_location-range-1;
            }
        }
        // check for exploding left
        for (int xTemp = x_location; xTemp <= x_location + range; xTemp ++) {
            if (xTemp  >= 0 && xTemp  < world.gridSize) {
                if(ExplodeHit(xTemp,y_location)) xTemp = x_location + range +1;
            }
        }
        // check for exploding right
        for (int xTemp = x_location; xTemp >= x_location - range; xTemp--) {
            if (xTemp >= 0 && xTemp < world.gridSize) {
                if(ExplodeHit(xTemp,y_location)) xTemp = x_location - range -1;
            }
        }

        exploded = true;
        world.positions[x_location][y_location].deleteBomb();
    }


    private Boolean ExplodeHit(int xTemp, int yTemp){
        if (!world.positions[xTemp][yTemp].bombermanList.isEmpty()) {
            for (BomberMan man : world.positions[xTemp][yTemp].bombermanList) { //check if a bomberman is hit
                ModifyPoints(placedBy,100); // 100 points for killing
                ModifyPoints(man,-300);//-300 points for dying
                man.Die();
                if(PRINT)System.out.println("player " + man.id + " has been killed by player " + placedBy.id);
            }
            world.positions[xTemp][yTemp].bombermanList.clear();
        }

        if (world.positions[xTemp][yTemp].type == 0) {
            return true;
        } else if (world.positions[xTemp][yTemp].type == 1) { //type 1 is a softwall
            world.positions[xTemp][yTemp].type = 2; // change the wall to an empty space
            ModifyPoints(placedBy,20);//award 20 points to the agent for destroying a wall
            return true;
        }
        return false;
    }

}

