/**
 * Created by Remco on 5-3-2017.
 */

import java.util.concurrent.ThreadLocalRandom;


import static java.lang.Integer.max;

public class NeuralNetRemco {

    //TODO bias niet vergeten
    private int amountInputNodes;
    private int amountHiddenNodes;
    private int amountOutputNodes;
    private int amountHiddenLayers;

    private int BIASVALUE = 1;

    private double[][] neuronValueArray;// = new Double[totalAmountOfLayers][max(max(amountInputNodes,amountHiddenNodes),amountOutputNodes)]; //create a 2D array to store the value of nodes. Breadth is amount of layers, height is maximum amount of nodes
    private double[][] weightValueArray;// = new Double[totalAmountOfLayers-1][amountHiddenNodes*(amountInputNodes-1)]; // Assuming hiddennodes are the largest

    //RealMatrix neuronValueMatrix = new Array2DRowRealMatrix(initializeMatrix(3,3,0,5));
    //RealMatrix weightValueMatrix = new Array2DRowRealMatrix(initializeMatrix(3,3,0,5));


    //Constructor
    public NeuralNetRemco(int amountInputNodes, int amountHiddenNodes, int amountHiddenLayers, int amountOutputNodes){

        //store values
        this.amountInputNodes = amountInputNodes;
        this.amountHiddenNodes = amountHiddenNodes;
        this.amountOutputNodes = amountOutputNodes;
        this.amountHiddenLayers = amountHiddenLayers;

        this.neuronValueArray = new double[amountHiddenLayers+2][max(max(amountInputNodes,amountHiddenNodes),amountOutputNodes)]; //width: +2 for input and output layer, height: -1 because we start counting at 0, +1 for bias

        // TODO generalize this so it works with multiple layers, currently only works if hidden > input >= output
        this.weightValueArray = new double[amountHiddenLayers+1][amountInputNodes*amountHiddenNodes+1]; //-1 because we start counting at 0, +1 to add a slot for the biasweight
        this.weightValueArray = initializeArrayRandomValues(amountHiddenLayers+1,(amountInputNodes*amountHiddenNodes+1),0,5);

    }

    //Setters&Getters

    public double[][] getNeuronValueArray(){
        return neuronValueArray;
    }

    //Methods


    private double[][] initializeArrayRandomValues(int rows, int columns, int min, int max){
        double[][] outputArray = new double[rows][columns];

        //initialize all rows
        for(int r = 0; r<=rows-1; r++){
            for(int c = 0; c<=columns-1; c++){
                outputArray[r][c] = (double) ThreadLocalRandom.current().nextInt(min*1000, max*1000 + 1)/1000;
            }
        }

        return outputArray;
    }


    void forwardPass(){

        //TODO loop
        netInputNode(1,2);
        outputNode();
        //TODO moet je de hidden layer waarden updaten of enkel de weights?


    }


    double netInputNode(int layerNumber, int nodeNumber){
        //find weights connected to node, using array

        findWeightsToNode();

        //find node connected to weights + bias

        //multiply all weights by the inputs + bias


        return 0;
    }

    /** (a) =w=> (b)
     WeightToNode: node b is connected to w
     NodeToWeight: w is connected  to node b
     WeightFromNode: w is connected (spawns from) from node a
     NodeFromWeight: node a is connected from w: it spawns w.

     vanaf de node; naar de node
     **/

    void findWeightsFromNode(){

    }

    void findWeightsToNode(){

    }


    void findNodesFromWeights(){

    }

    void findNodesToWeights(){

    }

    void getBiasValue(int layerNumber){

    }

    double outputNode(){
        //give actual output
        //by means of an activation function, or other function

        return 0;
    }

    double activationFunction(){

        return 0;
    }

    double calculateTotalError(){

        return 0;
    }

}

