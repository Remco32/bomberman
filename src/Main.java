import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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


        GameWorld world = new GameWorld(9,4, false); // gridsize should be of 2*n +1


        ai.add(new NeuralNetworkAISimpleFeatures(world,world.bomberManList.get(0),weights));// create neural network
        for(int idx=1;idx<world.amountPlayers;idx++) ai.add(new RandomAI(world,world.bomberManList.get(idx)));

        world.SetAi(ai);
        world.RunGameLoop();


/**
        double[][] input = { {.05, .10} };
        double[][] targetOutput = { {.01, 0.99} };
        double[] testInput1 =  {.01, 0.99} ;
 **/


///**
        double[][] input = { {0, 0} , {0 ,1} , {1 ,0} , {1 ,1} }; // XOR
        double[][] targetOutput = { {0} , {1} , {1} , {0} }; // XOR
        double[] testInput1 =  {0, 0} ; // XOR
        double[] testInput2 =  {0, 1} ; // XOR
        double[] testInput3 =  {1, 0} ; // XOR
        double[] testInput4 =  {1, 1} ; // XOR
// **/
/**
        double[][] input = { {0, 0} , {0 ,1} , {1 ,0} , {1 ,1} }; // OR
        double[][] targetOutput = { {0} , {1} , {1} , {1} }; // OR
        double[] testInput1 =  {0, 0} ; // OR
        double[] testInput2 =  {0, 1} ; // OR
        double[] testInput3 =  {1, 0} ; // OR
        double[] testInput4 =  {1, 1} ; // OR
**/
        //TODO leert niet goed bij >40 hidden nodes
        NeuralNetRemco AI_Remco = new NeuralNetRemco(input,5,1,targetOutput, 0.5);
        AI_Remco.learn(10000);
        System.out.println();
        System.out.println(Arrays.toString(AI_Remco.getOutput(testInput1)));
        ///**
        System.out.println(Arrays.toString(AI_Remco.getOutput(testInput2)));
        System.out.println(Arrays.toString(AI_Remco.getOutput(testInput3)));
        System.out.println(Arrays.toString(AI_Remco.getOutput(testInput4)));
        // **/

        System.out.println();

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

