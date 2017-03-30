import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;

import static org.apache.commons.math3.util.FastMath.pow;

/**
 * Created by joseph on 15/02/2017.
 */
public class NeuralNetworkAISimpleFeatures extends AIHandler {
    private ArrayList<RealMatrix> weightMatrixArray;
    private double output;
    private MoveUtility[] utility;
    private int inputSize = 7; // all basic features plus 1 for the bias
    private int hiddenLayerSize = 1;
    private double learningRate = 0.1;
    private double explorationChance;

    NeuralNetworkAISimpleFeatures(GameWorld world, BomberMan man, ArrayList<double[][]> weights) {
        super(world, man);
        weightMatrixArray = new ArrayList<>();

        for (int idx = 0; idx < weights.size(); idx++)
            weightMatrixArray.add(MatrixUtils.createRealMatrix(weights.get(idx)));

        utility = new MoveUtility[6];
    }

    public int getInputSize() {
        return inputSize;
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
                int tempDistance = Math.abs(x - man.getX_location()) + Math.abs(y - man.getY_location());

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
        int x = man.getX_location();
        int y = man.getY_location();
        for (Integer type : possibleMoves) {
            if (type == 0) utility[0] = CalculateUtilityMatrix(x, y,0, false);
            if (type == 1) utility[1] = CalculateUtilityMatrix(x - 1, y,1, false);//move left
            if (type == 2) utility[2] = CalculateUtilityMatrix(x, y - 1,2, false); //move up
            if (type == 3) utility[3] = CalculateUtilityMatrix(x, y + 1,3, false);//move down
            if (type == 4) utility[4] = CalculateUtilityMatrix(x + 1, y,4, false);//move right
            if (type == 5) utility[5] = CalculateUtilityMatrix(x, y,5, true);
        }
        int maxIndex = -1;
        for (Integer type : possibleMoves) {
            double newNumber = utility[type].getUtility();
            //System.out.println(utility[type]);
            if (maxIndex==-1 || (newNumber > utility[maxIndex].getUtility())) {
                maxIndex = type;
            }
        }
        //TODO fix this to right move with respect to learning

        //System.out.println("best move: "+ maxIndex + "with util:" + utility[maxIndex]);
        moves.add(utility[maxIndex]);
    }

    MoveUtility CalculateUtilityMatrix(int x, int y,int move, Boolean bombMove) { //calculate the utility of a given state
        Boolean removeBomb = false;
        Bomb bomb = null;
        if (bombMove) { // testcase for the placing a bomb move
            if (world.positions[x][y].bomb == null) {
                removeBomb = true;
                bomb = new Bomb(x, y, man, world);
                world.activeBombList.add(bomb);
                world.positions[x][y].addBomb(bomb);
            }
        }
        double[] featureValues = {FindClosestBomb(x, y),
                FindClosestEnemy(x, y), PossibleSteps(x, y),
                AliveBombermans(), AmountOfBombs(),
                AmountOfBombsPlacedByYou(),1};//for the threshold

        RealMatrix tempVector = MatrixUtils.createRealMatrix(new double[][]{featureValues});

        if (removeBomb) {
            world.activeBombList.remove(bomb);
            world.positions[x][y].deleteBomb(); // if it has been placed for trial
        }

        for (int idx = 0; idx < weightMatrixArray.size(); idx++) { // for continues output
            //  weightmatrix
            // System.out.println("tempvector: " + tempVector);
            // System.out.println("Weights: " + weightMatrixArray.get(idx));

            tempVector = tempVector.multiply(weightMatrixArray.get(idx));
            tempVector = MatrixUtils.createRealMatrix(SigmoidActivationFunction(tempVector));
        }

        MoveUtility tempMoveUtility = new MoveUtility(move,tempVector.getData()[0][0],featureValues);
        if (tempVector.getRowDimension() + tempVector.getColumnDimension() - 2 == 1) return tempMoveUtility;
        //-2 because there will be a threshold vector in there
        else {
            System.out.println(" output of neural net not 1");
        }
        return new MoveUtility(move, Double.NEGATIVE_INFINITY); //  only the error case.
    }

    double[][] SigmoidActivationFunction(RealMatrix matrix){
        double[][] returnData = new double[1][matrix.getData()[0].length + 1];

        double[][] data = matrix.getData();

        int j,i;
        for ( j = 0; j < data[0].length; j++) {
            for (i = 0; i < data.length; i++) {
                returnData[i][j] = (1.0 / (1 + Math.exp(-data[i][j])));
            }
        }

        returnData[0][j] = 1;
        return returnData;
    }

    double CalculateTotalError(){
     MoveUtility  expectedOutcome = moves.get(moves.size()-1);
     double realOutcome = man.points.get(man.points.size()-1)-man.points.get(man.points.size()-2); // the difference in points because of the move
     double error = 0.5*pow((expectedOutcome.getUtility() - realOutcome),2);
     return error;
    }


    void updateWeights(){
        double totalError = CalculateTotalError();
        for (int idx =weightMatrixArray.size(); idx >0; idx--){
            double[][] data = weightMatrixArray.get(idx).getData();


        }

    }



}