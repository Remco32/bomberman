import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

/**
 * Created by joseph on 9-2-2017.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        //ArrayList<double[][]> weights = new ArrayList<>();
        ArrayList<AIHandler> ai = new ArrayList<>();

        //weights.add(CreateRandomWeights(7,3));// there are 6 input values,1 threshold value, we want two output columns

        //weights.add(CreateRandomWeights(4,1));


        // amount of columns defines the length of the output
        // amount of rows should == the amount of columns +1(for the threshold) from the last output in weights
        // for threshold the amount of rows should equal the amount of output nodes


        GameWorld world = new GameWorld(9,4, true, 1); // gridsize should be of 2*n +1


        //ai.add(new NeuralNetworkAISimpleFeatures(world,world.bomberManList.get(0),weights));// create neural network
        for(int idx=1;idx<world.amountPlayers;idx++) {
            ai.add(new RandomAI(world, world.bomberManList.get(idx))); //activates enemy AI
        }

        //world.SetAi(ai);
        world.SetAi();
        world.RunGameLoop();
        RemcoAI AI_Remco = new RemcoAI(world,world.bomberManList.get(0)); //add player

        AI_Remco.playQLearning();

        }

/**

        double[][] input = { {0, 0} , {0 ,1} , {1 ,0} , {1 ,1} }; // XOR
        double[][] targetOutput = { {0} , {1} , {1} , {0} }; // XOR
        double[] testInput1 =  {0, 0} ; // XOR
        double[] testInput2 =  {0, 1} ; // XOR
        double[] testInput3 =  {1, 0} ; // XOR
        double[] testInput4 =  {1, 1} ; // XOR

        //TODO leert niet goed bij >40 hidden nodes
        NeuralNetRemco NN_Remco = new NeuralNetRemco(input,20,1,targetOutput, 0.5);
        NN_Remco.learn(10000);
        System.out.println();
        System.out.println(Arrays.toString(NN_Remco.getOutputLayer(testInput1)));
        System.out.println(Arrays.toString(NN_Remco.getOutputLayer(testInput2)));
        System.out.println(Arrays.toString(NN_Remco.getOutputLayer(testInput3)));
        System.out.println(Arrays.toString(NN_Remco.getOutputLayer(testInput4)));


        System.out.println();
 }
**/




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

