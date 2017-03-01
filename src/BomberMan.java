import java.util.ArrayList;

/**
 * Created by joseph on 09/02/2017.
 */
public class BomberMan {
    private Boolean DEBUGPRINT = false; // Activates debug prints for bomb

    int x_location;
    int y_location;
    int id;
    ArrayList<Integer> points;
    Boolean alive;
    GameWorld world;

    BomberMan(int x, int y, int id, GameWorld world) {
        this.x_location = x;
        this.y_location = y;
        this.id = id;
        this.world = world;
        points = new ArrayList<>();
        points.add(0);
        alive = true;
    }

    void Move(int type) { //TODO: Change to enums, low-priority
        if (!alive) return;
        int moveCost = points.get(points.size()-1)-10;
        points.add(moveCost);
        //if (type == 0) ; // do nothing; redundant
        if (type == 1) MakeMove(-1, 0);//move left
        if (type == 2) MakeMove(0, -1); //move up
        if (type == 3) MakeMove(0, 1);//move down
        if (type == 4) MakeMove(1, 0);//move right

        if (type == 5) { //place bomb
            if(DEBUGPRINT) System.out.println("player " + this.id + " placed a bomb");
            if (world.positions[x_location][y_location].bomb == null) {
                Bomb bomb = new Bomb(x_location, y_location, this, world);
                world.activeBombList.add(bomb);
                world.positions[x_location][y_location].addBomb(bomb);
            } else if(DEBUGPRINT) System.out.println("Bomb has already been placed at this location");
        }
    }

    private void MakeMove(int x, int y) {
        world.positions[x_location][y_location].deleteBomberman(this); //remove from current location
        if (x == 0) {
            if (y_location + y >= 0 && y_location + y < world.gridSize &&
                    world.positions[x_location][y_location + y].type == 2) {
                y_location += y;
            } else {
                if(DEBUGPRINT) System.out.println("player " + this.id + "cannot go to: x" + (x_location + x) + "y:" + (y_location + y));
            }
        }

        if (y == 0) {
            if (x_location + x >= 0 && x_location + x < world.gridSize &&
                    world.positions[x_location + x][y_location].type == 2) {
                x_location += x;
            } else {
                System.out.println("player " + this.id + "cannot go to: x" + (x_location + x) + "y:" + (y_location + y));
            }
        }
        world.positions[x_location][y_location].addBomberman(this); //move to new location
    }


    ArrayList<Integer> AbleMoves() {
        ArrayList<Integer> moves = new ArrayList<>();
        moves.add(0);// always possible to do nothing


        if (x_location - 1 >= 0 && world.positions[x_location - 1][y_location].type == 2) { //Type 2 measns empty space, so it's movable
            moves.add(1); // move left
        }
        if (y_location - 1 >= 0 && world.positions[x_location][y_location - 1].type == 2) {
            moves.add(2); // move up
        }
        if (y_location + 1 < world.gridSize && world.positions[x_location][y_location + 1].type == 2) {
            moves.add(3); // move down
        }
        if (x_location + 1 < world.gridSize && world.positions[x_location + 1][y_location].type == 2) {
            moves.add(4); // move right
        }
        if (world.positions[x_location][y_location].bomb == null) {
            moves.add(5); // place bomb
        }
        return moves;
    }

    void Die() {
        alive = false;
    }


}
