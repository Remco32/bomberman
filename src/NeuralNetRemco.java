/**
 * Created by Remco on 5-3-2017.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


import static java.lang.Integer.max;

public class NeuralNetRemco {


    boolean DEBUG = true;

    private int amountHiddenLayers;

    private int[] amountOfNodesPerLayer;

    private int BIASVALUE = 1;

    double[][] neuronValueArray;// = new Double[totalAmountOfLayers][max(max(amountInputNodes,amountHiddenNodes),amountOutputNodes)]; //create a 2D array to store the value of nodes. Breadth is amount of layers, height is maximum amount of nodes
    double[][] weightValueArray;// = new Double[totalAmountOfLayers-1][amountHiddenNodes*(amountInputNodes-1)]; // Assuming hiddennodes are the largest

    //RealMatrix neuronValueMatrix = new Array2DRowRealMatrix(initializeMatrix(3,3,0,5));
    //RealMatrix weightValueMatrix = new Array2DRowRealMatrix(initializeMatrix(3,3,0,5));


    //Constructor
    public NeuralNetRemco(int amountInputNodes, int amountHiddenNodes, int amountOutputNodes, int amountHiddenLayers){

        //store values
        this.amountHiddenLayers = amountHiddenLayers;

        //Store amount of nodes per layer in array
        this.amountOfNodesPerLayer = new int[amountHiddenLayers+2];
        this.amountOfNodesPerLayer[0] = amountInputNodes;
        for(int i = 1; i<amountHiddenLayers+1;i++){
            this.amountOfNodesPerLayer[i] = amountHiddenNodes;
        }
        this.amountOfNodesPerLayer[amountHiddenLayers+1] = amountOutputNodes;

        this.neuronValueArray = new double[amountHiddenLayers+2][max(max(amountInputNodes,amountHiddenNodes),amountOutputNodes)]; //+2 for input and output layer

        // TODO generalize this so it works with multiple layers, currently only works if hidden > input >= output
        this.weightValueArray = new double[amountHiddenLayers+1][amountInputNodes*amountHiddenNodes+1]; //+1 to add a slot for the biasweight
        this.weightValueArray = initializeArrayRandomValues(amountHiddenLayers+1,(amountInputNodes*amountHiddenNodes+1),0,5);

        //DEBUG INITIALIZATION
        if (DEBUG) {
            neuronValueArray[0] = new double[]{.05, .1};
            weightValueArray[0] = new double[]{.15, .25, .2, .30, .35};
            weightValueArray[1] = new double[]{.4, .5, .45, .55, .6};
            neuronValueArray[2] = new double[]{.01, .99};
        }

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

    private double[] setInputs(int value){ //TODO verschillende waardes
        int rows = amountOfNodesPerLayer[0];

        double[] output = new double[rows];
        for(int r = 0; r<=rows-1; r++){
            output[r] = value;
        }
        return output;
    }


    void forwardPass(){

        //TODO loop
        netInputNode(1,0); //first hidden node
        outputNode();
        //TODO moet je de hidden layer waarden updaten of enkel de weights?


    }


    double netInputNode(int layerNumber, int nodeNumber){
        //LOOP

        //find weights connected to node, using array

        List<Integer> list = new ArrayList<>();
        list = findWeightsToNode(layerNumber, nodeNumber);

        //System.out.println("Weights are: ");

        double value = 0 ;
        //multiply weight with corresponding input
        for (int i = 0; i < list.size(); i++) {
            //we use the fact that the first corresponding weight has to be multiplied with the first input
            System.out.println("Nummer van weight " + list.get(i));
            System.out.println("Waarde van weight " + weightValueArray[layerNumber - 1][list.get(i)]);
            System.out.println("Waarde van input " + neuronValueArray[layerNumber - 1][i]);


            value += weightValueArray[layerNumber - 1][list.get(i)] * neuronValueArray[layerNumber - 1][i];
        }
        //add bias: weight multiplied by biasvalue
        value += weightValueArray[layerNumber-1][weightValueArray[layerNumber-1].length-1] * BIASVALUE;
        System.out.println(value);


        /**
        for (int i = 0; i < list.size(); i++) {
            //System.out.println(list.get(i) + " ");
            for (int j = 0; j < amountOfNodesPerLayer[layerNumber - 1]; j++) {
                //System.out.println(weightValueArray[layerNumber - 1][i]);
                //System.out.println(neuronValueArray[layerNumber - 1][j]);

                System.out.println(weightValueArray[layerNumber - 1][i] * neuronValueArray[layerNumber - 1][j] + weightValueArray[layerNumber-1][weightValueArray[layerNumber-1].length-1] * BIASVALUE);


            }
        }
         **/

        //find node connected to weights + bias

        //multiply all weights by the inputs + bias


        return 0;
    }

    /** (a) =w=> (b)
     WeightToNode: w is connected to b
     NodeToWeight: b is connected to w
     WeightFromNode: w is connected (starts from) from node a
     NodeFromWeight: node a is connected from w: it spawns w.

     vanaf de node; naar de node
     **/

    void findWeightsFromNode(){

    }

    List findWeightsToNode(int layerNumber, int nodeNumber){

        List<Integer> list = new ArrayList<>();

        for(int i = nodeNumber; i<(amountOfNodesPerLayer[layerNumber-1]*amountOfNodesPerLayer[layerNumber]); i+=amountOfNodesPerLayer[layerNumber] ){
            //System.out.println( weightValueArray[layerNumber-1][i] );
            //System.out.println("Weight w"+ i + " from layer " + (layerNumber-1) + " to layer " + layerNumber);
            list.add(i);
        }
        return list;
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

