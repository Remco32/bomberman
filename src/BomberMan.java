import java.util.ArrayList;

/**
 * Created by joseph on 09/02/2017.
 */
public class BomberMan {
    private Boolean DEBUGPRINT = false; // Activates debug prints for bomb

    private int x_location;
    private int y_location;
    int id;
    ArrayList<Integer> points;
    Boolean alive;
    GameWorld world;
    int bombCooldown; //cooldown time for when new bomb can be placed
    int BOMBCOOLDOWNTIME = 5;

    MoveUtility.Actions nextAction = MoveUtility.Actions.IDLE; //Has a value to avoid nullpointer exceptions

    BomberMan(int x, int y, int id, GameWorld world) {
        this.x_location = x;
        this.y_location = y;
        this.id = id;
        this.world = world;
        points = new ArrayList<>();
        points.add(0);
        alive = true;
        bombCooldown = 0;
    }

    //decreases cooldown of bomb by 1.
    void updateBombCooldown() {
        if (bombCooldown > 0) bombCooldown--;
    }

    void move(MoveUtility.Actions action) {
        if (!alive) return;
        int moveCost = points.get(points.size() - 1) - 10;
        points.add(moveCost);
        //if (type == 0) ; // do nothing; redundant
        switch (action) {
            case IDLE:
                //waitForNextTurn();
            case UP:
                MakeMove(0, -1); //move up
                //waitForNextTurn();
                break;
            case DOWN:
                MakeMove(0, 1);//move down
                //waitForNextTurn();
                break;
            case LEFT:
                MakeMove(-1, 0);//move left
                //waitForNextTurn();
                break;
            case RIGHT:
                MakeMove(1, 0);//move right
                //waitForNextTurn();
                break;
            case PLACEBOMB:
                if (world.positions[x_location][y_location].bomb == null && bombCooldown == 0) {
                    Bomb bomb = new Bomb(x_location, y_location, this, world);
                    world.activeBombList.add(bomb);
                    world.positions[x_location][y_location].addBomb(bomb);
                    if (DEBUGPRINT) System.out.println("player " + this.id + " placed a bomb");
                    bomb.createDangerzones();
                    bombCooldown = BOMBCOOLDOWNTIME;

                    //waitForNextTurn();

                } else if (DEBUGPRINT) System.out.println("Bomb has already been placed at this location");
                break;
        }

    }





    private void MakeMove(int x, int y) {
        world.positions[x_location][y_location].deleteBomberman(this); //remove from current location
        if (x == 0) {
            if (y_location + y >= 0 && y_location + y < world.gridSize &&
                    world.positions[x_location][y_location + y].type == WorldPosition.Fieldtypes.EMPTY) {
                y_location += y;
            } else {
                if (DEBUGPRINT)
                    System.out.println("player " + this.id + " cannot go to: x: " + (x_location + x) + " y:" + (y_location + y));
            }
        }

        if (y == 0) {
            if (x_location + x >= 0 && x_location + x < world.gridSize &&
                    world.positions[x_location + x][y_location].type == WorldPosition.Fieldtypes.EMPTY) {
                x_location += x;
            } else {
                if (DEBUGPRINT)
                    System.out.println("player " + this.id + " cannot go to: x: " + (x_location + x) + " y:" + (y_location + y));
            }
        }
        world.positions[x_location][y_location].addBomberman(this); //move to new location



    }

    void waitForNextTurn(){

        //Adhere to the timesteps of the game
        try {
            Thread.sleep(world.ROUND_TIME_MILISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    ArrayList<MoveUtility.Actions> AbleMoves() {
        ArrayList<MoveUtility.Actions> moves = new ArrayList<>();
        moves.add(MoveUtility.Actions.IDLE);// always possible to do nothing

        if (x_location - 1 >= 0 && world.positions[x_location - 1][y_location].type == WorldPosition.Fieldtypes.EMPTY) { //Type 2 means empty space, so it's movable
            moves.add(MoveUtility.Actions.LEFT); // move left
        }
        if (y_location - 1 >= 0 && world.positions[x_location][y_location - 1].type == WorldPosition.Fieldtypes.EMPTY) {
            moves.add(MoveUtility.Actions.UP); // move up
        }
        if (y_location + 1 < world.gridSize && world.positions[x_location][y_location + 1].type == WorldPosition.Fieldtypes.EMPTY) {
            moves.add(MoveUtility.Actions.DOWN); // move down
        }
        if (x_location + 1 < world.gridSize && world.positions[x_location + 1][y_location].type == WorldPosition.Fieldtypes.EMPTY) {
            moves.add(MoveUtility.Actions.RIGHT); // move right
        }
        if (world.positions[x_location][y_location].bomb == null) {
            moves.add(MoveUtility.Actions.PLACEBOMB); // place bomb
        }
        return moves;
    }

    void Die() {
        alive = false;
    }

    int getX_location() {
        return x_location;
    }

    int getY_location() {
        return y_location;
    }

    //To put in the gameloop
    void setNextMove(MoveUtility.Actions action) {
        nextAction = action;
        //don't make another action before waiting your turn
        waitForNextTurn();
    }
}
