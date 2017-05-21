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
        playGame();
        //testNeuralNet();
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
        System.out.println(Arrays.toString(testInput1) + " gives " + Arrays.toString(NN_Remco.getOutputLayer(testInput1)) + " | target is " + Arrays.toString(targetOutput[0]));
        System.out.println(Arrays.toString(testInput3) + " gives " + Arrays.toString(NN_Remco.getOutputLayer(testInput3)) + " | target is " + Arrays.toString(targetOutput[2]));
        System.out.println(Arrays.toString(testInput2) + " gives " + Arrays.toString(NN_Remco.getOutputLayer(testInput2)) + " | target is " + Arrays.toString(targetOutput[1]));
        System.out.println(Arrays.toString(testInput4) + " gives " + Arrays.toString(NN_Remco.getOutputLayer(testInput4)) + " | target is " + Arrays.toString(targetOutput[3]));

        System.out.println();
    }

    static void playGame() {
        int gridSize = 9;
        int amountOfPlayers = 4;
        boolean showWindow = true;
        int worldType = 1;

        int amountOfTrials = 10;
        int amountHiddenNodes = 40;
        int amountHiddenLayers = 1;
        double learningRate = 0.5;
        double randomMoveChance = 0.2;
        int roundTimeInMs = 50;
        boolean usePreviousNetwork = true;


        GameWorld world = new GameWorld(gridSize, amountOfPlayers, showWindow, worldType); // gridsize should be of 2*n +1
        world.startGame(world, amountOfTrials, amountHiddenNodes, amountHiddenLayers, learningRate, randomMoveChance, roundTimeInMs, usePreviousNetwork);
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

