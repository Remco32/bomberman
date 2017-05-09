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
        //playGame();
        testNeuralNet();

        //AI_Remco.playQLearning();

    }

    static void testNeuralNet() {
        double[][] input = {{0, 0}, {0, 1}, {1, 0}, {1, 1}}; // XOR
        double[][] targetOutput = {{0}, {1}, {1}, {0}}; // XOR
        double[] testInput1 = {0, 0}; // XOR
        double[] testInput2 = {0, 1}; // XOR
        double[] testInput3 = {1, 0}; // XOR
        double[] testInput4 = {1, 1}; // XOR

        //TODO leert niet goed bij >40 hidden nodes
        NeuralNetRemco NN_Remco = new NeuralNetRemco(input, 20, 1, targetOutput, 0.5);
        NN_Remco.learn(10000);
        System.out.println();
        System.out.println(Arrays.toString(testInput1) + " gives " + Arrays.toString(NN_Remco.getOutputLayer(testInput1)));
        System.out.println(Arrays.toString(testInput2) + " gives " + Arrays.toString(NN_Remco.getOutputLayer(testInput2)));
        System.out.println(Arrays.toString(testInput3) + " gives " + Arrays.toString(NN_Remco.getOutputLayer(testInput3)));
        System.out.println(Arrays.toString(testInput4) + " gives " + Arrays.toString(NN_Remco.getOutputLayer(testInput4)));

        System.out.println();
    }

    static void playGame() {
        GameWorld world = new GameWorld(9, 2, true, 1); // gridsize should be of 2*n +1
        world.startGame(world, 1000, 20, 1, 0.8, 0.2, 1);
        System.out.println("All rounds have ended.");
    }

    private static double[][] CreateRandomWeights(int rows, int columns) {
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

