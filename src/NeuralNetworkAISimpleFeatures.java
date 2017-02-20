import com.sun.javaws.exceptions.ExitException;
import javafx.geometry.Pos;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by joseph on 15/02/2017.
 */
public class NeuralNetworkAISimpleFeatures extends AIHandler {
    ArrayList<RealMatrix> weightMatrixArray;
    ArrayList<RealMatrix> thresholdMatrixArray;
    double output;
    double[] utility;
    int inputSize = 6;
    int hiddenLayerSize = 1;
    double learningRate = 0.1;
    double explorationChance;

    NeuralNetworkAISimpleFeatures(GameWorld world, BomberMan man, ArrayList<double[][]> weights, ArrayList<double[][]> thresholds) {
        super(world, man);
        weightMatrixArray = new ArrayList<>();
        thresholdMatrixArray = new ArrayList<>();
        for (int idx = 0; idx < weights.size(); idx++)
            weightMatrixArray.add(MatrixUtils.createRealMatrix(weights.get(idx)));
        for (int idx = 0; idx < thresholds.size(); idx++)
            thresholdMatrixArray.add(MatrixUtils.createRealMatrix(thresholds.get(idx)));
        utility = new double[6];
    }

    // manhattan distance
    double FindClosestBomb(int x, int y) { // bfs
        double manhattanDistance = -1; // if there are no bombs -1 is returned;
        for (Bomb bomb : world.activeBombList) {
            int tempDistance = Math.abs(x - bomb.x_location) + Math.abs(y - bomb.y_location);

            if (manhattanDistance == -1 || tempDistance < manhattanDistance) {
                manhattanDistance = tempDistance;
            }

        }
        return manhattanDistance;
    }

    double FindClosestEnemy(int x, int y) { // bfs
        double manhattanDistance = -1;
        for (BomberMan man : world.bomberManList) {
            if (man.alive && man != this.man) {  //Bomberman should be alive and different than the one we control
                int tempDistance = Math.abs(x - man.x_location) + Math.abs(y - man.y_location);

                if (manhattanDistance == -1 || tempDistance < manhattanDistance) {
                    manhattanDistance = tempDistance;
                }
            }
        }
        return manhattanDistance;
    }

    double PossibleSteps(int x, int y) {
        double steps = 0;
        if (x + 1 < world.gridSize && world.positions[x + 1][y].type == 2) steps++;
        if (x - 1 >= 0 && world.positions[x - 1][y].type == 2) steps++;
        if (y + 1 < world.gridSize && world.positions[x][y + 1].type == 2) steps++;
        if (y - 1 >= 0 && world.positions[x][y - 1].type == 2) steps++;

        return steps;
    }

    double AliveBombermans() {
        double alive = 0;
        for (BomberMan man : world.bomberManList) {
            if (man.alive) alive++;
        }
        return alive;
    }

    double AmountOfBombs() {
        return world.activeBombList.size();
    }

    double AmountOfBombsPlacedByYou() {
        double placed = 0;
        for (Bomb bomb : world.activeBombList) {
            if (bomb.placedBy == man) placed++;
        }
        return placed;
    }

    @Override
    void CalculateBestMove() {
        ArrayList<Integer> possibleMoves = man.AbleMoves();
        int x = man.x_location;
        int y = man.y_location;
        for (Integer type : possibleMoves) {
            if (type == 0) utility[0] = CalculateUtility(x, y, false);
            if (type == 1) utility[1] = CalculateUtility(x - 1, y, false);//move left
            if (type == 2) utility[2] = CalculateUtility(x, y - 1, false); //move up
            if (type == 3) utility[3] = CalculateUtility(x, y + 1, false);//move down
            if (type == 4) utility[4] = CalculateUtility(x + 1, y, false);//move right
            if (type == 5) utility[5] = CalculateUtility(x, y, true);
        }
        int maxIndex = 2;
        for (Integer type : possibleMoves) {
            double newNumber = utility[type];
            //System.out.println(utility[type]);
            if ((newNumber > utility[maxIndex])) {
                maxIndex = type;
            }
        }
        //TODO fix this to right move with respect to learning

        //System.out.println("best move: "+ maxIndex + "with util:" + utility[maxIndex]);
        moves.add(new MoveUtility(maxIndex, utility[maxIndex]));
    }

    double CalculateUtility(int x, int y, Boolean bombMove) { //calculate the utility of a given state
        Boolean removeBomb = false;
        Bomb bomb = null;
        if (bombMove) {
            if (world.positions[x][y].bomb == null) {
                removeBomb = true;
                bomb = new Bomb(x, y, man, world);
                world.activeBombList.add(bomb);
                world.positions[x][y].add_Bomb(bomb);
            }
        }
        RealMatrix tempVector = MatrixUtils.createRealMatrix(new double[][]{{FindClosestBomb(x, y),
                FindClosestEnemy(x, y), PossibleSteps(x, y),
                AliveBombermans(), AmountOfBombs(),
                AmountOfBombsPlacedByYou()}});

        if (removeBomb) {
            world.activeBombList.remove(bomb);
            world.positions[x][y].deleteBomb(); // if it has been placed for trial
        }

        for (int idx = 0; idx < thresholdMatrixArray.size(); idx++) { // for continues output
            // thresholdmatrix is 1 shorther than weightmatrix

            tempVector = tempVector.multiply(weightMatrixArray.get(idx));

            tempVector = tempVector.subtract(thresholdMatrixArray.get(idx));
            double[][] data = tempVector.getData();
            for (int j = 0; j < data[0].length; j++) {
                for (int i = 0; i < data.length; i++) {
                    if (data[i][j] > 0) data[i][j] = 1;
                    else data[i][j] = 0;
                }
            }

            tempVector = MatrixUtils.createRealMatrix(data);

        }

        tempVector = tempVector.multiply(weightMatrixArray.get(weightMatrixArray.size() - 1));

        if (tempVector.getRowDimension() + tempVector.getColumnDimension() - 1 == 1) return tempVector.getData()[0][0];
        else {
            System.out.println(" output of neural net not 1");
        }
        return 0;
    }

    double CalculateError(){
     MoveUtility  expectedOutcome = moves.get(moves.size()-1);
     double realOutcome = man.points.get(man.points.size()-1)-man.points.get(man.points.size()-2); // the difference in points because of the move
     double error = realOutcome - expectedOutcome.utility;
     return error;
    }

}