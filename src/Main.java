import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * Created by joseph on 9-2-2017.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        ArrayList<double[][]> weights = new ArrayList<>();
        ArrayList<AIHandler> ai = new ArrayList<>();

        weights.add(CreateRandomWeights(7,3));// there are 6 input values,1 threshold value, we want two output columns

        weights.add(CreateRandomWeights(4,1));


        // amount of columns defines the length of the output
        // amount of rows should == the amount of columns +1(for the threshold) from the last output in weights
        // for threshold the amount of rows should equal the amount of output nodes


        GameWorld world = new GameWorld(9,4, true); // gridsize should be of 2*n +1


        ai.add(new NeuralNetworkAISimpleFeatures(world,world.bomberManList.get(0),weights));// create neural network
        for(int idx=1;idx<world.amountPlayers;idx++) ai.add(new RandomAI(world,world.bomberManList.get(idx)));

        world.SetAi(ai);
        world.RunGameLoop();

    }

    private static double[][] CreateRandomWeights(int rows,int columns){
        Random rnd = new Random();
        double[][] matrix = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = rnd.nextDouble();
            }
        }

        return matrix;
    }
}

