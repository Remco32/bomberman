import java.lang.reflect.Executable;

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

    void Round() {
        if (exploded) return;
        if (timer > 0) timer--;
        else Explode();
    }


    private void add_SubtractPoints(BomberMan man, int amount){
        int points = man.points.get(man.points.size()-1)+amount;
        man.points.add(points);
    }



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


    Boolean ExplodeHit(int xTemp,int yTemp){
        if (!world.positions[xTemp][yTemp].bombermanList.isEmpty()) {
            for (BomberMan man : world.positions[xTemp][yTemp].bombermanList) {
                add_SubtractPoints(placedBy,100); // 100 points for killing
                add_SubtractPoints(man,-300);//-300 points for dying
                man.Die();
                if(PRINT)System.out.println("player " + man.id + " has been killed by player " + placedBy.id);
            }
            world.positions[xTemp][yTemp].bombermanList.clear();
        }

        if (world.positions[xTemp][yTemp].type == 0) {
            return true;
        } else if (world.positions[xTemp][yTemp].type == 1) {
            world.positions[xTemp][yTemp].type = 2;
            add_SubtractPoints(placedBy,20);//20 points for destroying a wall
            return true;
        }
        return false;
    }

}

