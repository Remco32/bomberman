import java.util.ArrayList;

/**
 * Created by joseph on 15-2-2017.
 */
public class RandomAI extends AIHandler {

    RandomAI(GameWorld world,BomberMan man) {
        super(world,man);
        this.world = world;
        this.man = man;
    }

    void CalculateBestMove() {
        if(man.alive) moves.add(MakeEducatedMove(man));
    }

    private MoveUtility MakeEducatedMove(BomberMan man) {
        ArrayList<MoveUtility.Actions> moveList = man.AbleMoves();
        ArrayList<Bomb> bombList = findBombLocations(man.getX_location(), man.getY_location(), 2);//range can change
        double[] utilityList = new double[moveList.size()];
        if (!bombList.isEmpty()) {
            for (int x = 0; x < moveList.size(); x++) {
                utilityList[x] = 0;
                for (Bomb bomb : bombList) {
                    utilityList[x] += CalcUtility(bomb, moveList.get(x), man);
                }
            }
            int maxIndex = 0;
            for (int i = 1; i < moveList.size(); i++) {
                double newNumber = utilityList[i];
                if ((newNumber > utilityList[maxIndex])) {
                    maxIndex = i;
                }
            }
            return new MoveUtility(moveList.get(maxIndex),utilityList[maxIndex]);
        }
       return new MoveUtility(SemiRandomMove(man),0);

    }

    private double CalcUtility(Bomb bomb, MoveUtility.Actions move, BomberMan man) {
        int x = man.getX_location();
        int y = man.getY_location();
        if (move == MoveUtility.Actions.LEFT) x--;
        if (move == MoveUtility.Actions.UP) y--;
        if (move == MoveUtility.Actions.DOWN) y++;
        if (move == MoveUtility.Actions.RIGHT) x++;
        int xUtility = Math.abs(x - bomb.x_location);
        int yUtility = Math.abs(y - bomb.y_location);
        return Math.sqrt(xUtility) + Math.sqrt(yUtility);
//root

    }

    private ArrayList<Bomb> findBombLocations(int x_location, int y_location, int range) {
        ArrayList<Bomb> bombList = new ArrayList<>();
        range++;
        for (int xIdx = x_location - range; xIdx <= x_location + range; xIdx++) {
            for (int yIdx = y_location - range; yIdx <= y_location + range; yIdx++) {
                if (xIdx >= 0 && xIdx < world.gridSize && yIdx >= 0 && yIdx < world.gridSize) {
                    if (world.positions[xIdx][yIdx].bomb != null)
                        bombList.add(world.positions[xIdx][yIdx].bomb);
                }
            }
        }
        return bombList;
    }


    private MoveUtility.Actions SemiRandomMove(BomberMan man) {
        int y_location = man.getY_location();
        int x_location = man.getX_location();

        int surround = 0;
        if (x_location + 1 < world.gridSize && world.positions[x_location + 1][y_location].type == WorldPosition.Fieldtypes.EMPTY) {
            surround++;
        }

        if (x_location - 1 >= 0 && world.positions[x_location - 1][y_location].type == WorldPosition.Fieldtypes.EMPTY) {
            surround++;
        }
        if (y_location + 1 < world.gridSize && world.positions[x_location][y_location + 1].type == WorldPosition.Fieldtypes.EMPTY) {
            surround++;
        }

        if (y_location - 1 >= 0 && world.positions[x_location][y_location - 1].type == WorldPosition.Fieldtypes.EMPTY) {
            surround++;
        }
        if (surround < 2) {
            return MoveUtility.Actions.PLACEBOMB; //place bomb because is surrounded
        }

        while (true) { // semi random move
            int random = rnd.nextInt() % 5;
            if (x_location + 1 < world.gridSize && world.positions[x_location + 1][y_location].type == WorldPosition.Fieldtypes.EMPTY && random == 0) {
                return MoveUtility.Actions.RIGHT; // move right
            }

            if (x_location - 1 >= 0 && world.positions[x_location - 1][y_location].type == WorldPosition.Fieldtypes.EMPTY && random == 1) {
                return MoveUtility.Actions.LEFT; // move left
            }
            if (y_location + 1 < world.gridSize && world.positions[x_location][y_location + 1].type == WorldPosition.Fieldtypes.EMPTY && random == 2) {
                return MoveUtility.Actions.DOWN; // move down
            }

            if (y_location - 1 >= 0 && world.positions[x_location][y_location - 1].type == WorldPosition.Fieldtypes.EMPTY && random == 3) {
                return MoveUtility.Actions.UP; // move up
            }
            if (random == 4 && world.positions[x_location][y_location].bomb == null) {
                return MoveUtility.Actions.PLACEBOMB;
            }
        }
    }

}
