/**
 * Created by Remco on 5-3-2017.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


import static java.lang.Integer.max;
import static java.lang.Math.exp;

public class NeuralNetRemco {

    //DEBUG BOOLEANS
    boolean DEBUG_EXAMPLE_ARRAYS = true;
    boolean DEBUG_RANDOM_INPUT = false;

    private int BIASVALUE = 1;

    //TODO set this in constructor
    private double LEARNINGRATE = 0.5;

    private int amountHiddenLayers;
    private int[] amountOfNodesPerLayer;
    private double[] targetOutput;
    private List<Double> totalError = new ArrayList<>(); //list so we can keep the previous values



    double[][] neuronValueArray;// = new Double[totalAmountOfLayers][max(max(amountInputNodes,amountHiddenNodes),amountOutputNodes)]; //create a 2D array to store the value of nodes. Breadth is amount of layers, height is maximum amount of nodes
    double[][] weightValueArray;// = new Double[totalAmountOfLayers-1][amountHiddenNodes*(amountInputNodes-1)]; // Assuming hiddennodes are the largest

    //RealMatrix neuronValueMatrix = new Array2DRowRealMatrix(initializeMatrix(3,3,0,5));
    //RealMatrix weightValueMatrix = new Array2DRowRealMatrix(initializeMatrix(3,3,0,5));

    //Constructor
    public NeuralNetRemco(int amountInputNodes, int amountHiddenNodes, int amountOutputNodes, int amountHiddenLayers, double[] targetOutput) {

        //store values
        this.amountHiddenLayers = amountHiddenLayers;
        this.targetOutput = targetOutput;

        //Store amount of nodes per layer in array //TODO replace with array.lenght when initial sizes are correct
        this.amountOfNodesPerLayer = new int[amountHiddenLayers + 2];
        this.amountOfNodesPerLayer[0] = amountInputNodes;
        for (int i = 1; i < amountHiddenLayers + 1; i++) {
            this.amountOfNodesPerLayer[i] = amountHiddenNodes;
        }
        this.amountOfNodesPerLayer[amountHiddenLayers + 1] = amountOutputNodes;

        this.neuronValueArray = new double[amountHiddenLayers + 2][max(max(amountInputNodes, amountHiddenNodes), amountOutputNodes)]; //+2 for input and output layer

        for (int i = 0; i < amountHiddenLayers + 2; i++) {
            this.neuronValueArray[i] = new double[amountOfNodesPerLayer[i]];
        }

        // TODO generalize this so it works with multiple layers, currently only works if hidden > input >= output
        //TODO remove redundant rows
        this.weightValueArray = new double[amountHiddenLayers + 1][(amountInputNodes * amountHiddenNodes) + 1]; //+1 to add a slot for the biasweight
        this.weightValueArray = initializeArrayRandomValues(amountHiddenLayers + 1, (amountInputNodes * amountHiddenNodes + 1), 0, 1);

        //DEBUG_EXAMPLE_ARRAYS INITIALIZATION
        if (DEBUG_EXAMPLE_ARRAYS) {
            neuronValueArray[0] = new double[]{.05, .1};
            weightValueArray[0] = new double[]{.15, .25, .2, .30, .35};
            weightValueArray[1] = new double[]{.4, .5, .45, .55, .6};
        }
        if (DEBUG_RANDOM_INPUT) {
            for (int i = 0; i < amountInputNodes; i++) {
                neuronValueArray[0][i] = (double) ThreadLocalRandom.current().nextInt(1 * 1000, 2 * 1000) / 1000;
            }
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

    void forwardPass() {
        //System.out.println("output " + outputNode(netInputNode(1, 0)));

        //update values of nodes:
        //amount of layers
        for (int layer = 1; layer < amountHiddenLayers + 2; layer++) //amountHiddenLayers+1 == final layer index
            //amount of nodes
            for (int node = 0; node < amountOfNodesPerLayer[layer]; node++) {
                //Get the netInput, transform that into the output, and update the value
                neuronValueArray[layer][node] = outputNode(netInputNode(layer, node));
                //neuronValueArray[1][0] = outputNode(netInputNode(1,0)); // for layer 1, node 0 AKA hidden0
            }

        //calculate error
        //amount of nodes, look only in final layer AKA output layer
        double errorNode = 0;
        for (int node = 0; node < amountOfNodesPerLayer[amountHiddenLayers + 1]; node++) {
            errorNode += calculateError(neuronValueArray[amountHiddenLayers + 1][node], targetOutput[node]);
            //System.out.println("Totaal error is nu " + errorNode);
        }
        //add sum of the error to list
        totalError.add(errorNode);

        //list.get(list.size() - 1); pakt nieuwste element

    }

    void backwardsPass() {
        //TODO loop over alle weightarrays

        //if(neuronValueArray weightValueArray[].length - 1

        updateWeightsToOutputLayer();

    }

    private double netInputNode(int layerNumber, int nodeNumber) {

        List<Integer> list = new ArrayList<>();
        //find weights connected to the node, add those to a list
        list = findWeightsToNode(layerNumber, nodeNumber);

        //System.out.println("Weights are: ");

        double value = 0;
        //multiply weight with corresponding input
        for (int i = 0; i < list.size(); i++) {
            //we use the fact that the first corresponding weight has to be multiplied with the first input
            //System.out.println("Nummer van weight " + list.get(i));
            //System.out.println("Waarde van weight " + weightValueArray[layerNumber - 1][list.get(i)]);
            //System.out.println("Waarde van input " + neuronValueArray[layerNumber - 1][i]);

            value += weightValueArray[layerNumber - 1][list.get(i)] * neuronValueArray[layerNumber - 1][i];
        }
        //add bias: weight multiplied by biasvalue
        value += weightValueArray[layerNumber - 1][weightValueArray[layerNumber - 1].length - 1] * BIASVALUE;
        //System.out.println(value);
        return value;

        //multiply all weights by the inputs + bias

    }

    private void updateWeightsToOutputLayer() {

        /**
         for (int layer = 0; layer < amountHiddenLayers + 1; layer++) { //amountHiddenLayers+1 == final layer index, -1 because we have a layer less because weights are between neuronlayers
         for (int weight = 0; weight < weightValueArray[layer].length-1; weight++) {
         //int nodeToWeight = findNodeToWeight(layer,weight);
         //int nodeFromWeight = findNodeFromWeight(layer,weight);
         }
         }
         **/

        //werkt alleen met hiddenlayer <-> outputlayer, oftewel laatste layer

        int outputLayer = neuronValueArray.length - 1; //final layer, AKA outputlayer

        for (int weight = 0; weight < weightValueArray[outputLayer - 1].length - 1; weight++) { //-1 because we skip the bias

            double gradient = 0;
            int nodeToWeight = findNodeToWeight(outputLayer - 1, weight);
            int nodeFromWeight = findNodeFromWeight(outputLayer - 1, weight);

            gradient = neuronValueArray[outputLayer][nodeToWeight] - targetOutput[nodeToWeight];
            gradient *= (neuronValueArray[outputLayer][nodeToWeight] * (1 - neuronValueArray[outputLayer][nodeToWeight]));
            gradient *= neuronValueArray[outputLayer - 1][nodeFromWeight];
            //System.out.println("Gradient van weight " + weight + " = " + gradient);

            //apply learning rate & update weight
            weightValueArray[outputLayer - 1][weight] = weightValueArray[outputLayer - 1][weight] - LEARNINGRATE * gradient;
            //System.out.println("Updated value van weight " + weight + " = " + weightValueArray[outputLayer - 1][weight]);
            //System.out.println();

        }

        return;

    }



    private double calculateError(double output, double target) {
        return Math.pow((target - output), 2)/2;
    }

    /** (Node_a) =Weight_w=> (Node_b)
     WeightToNode: w is connected to b
     NodeToWeight: b is connected to w
     WeightFromNode: w is connected (starts from) from node a
     NodeFromWeight: node a is connected from w: it spawns w.

     vanaf de node; naar de node
     **/

    //Returns list of numbers corresponding to the weights that are connecting FROM a node.
    //i.e. situation (Node_a) =Weight_w=> (Node_b). findWeightsToNode(0,a) gives {w}
    private List findWeightsFromNode(int layerNumber, int nodeNumber) {
        List<Integer> list = new ArrayList<>();

        //add all connected weights
        //TODO check of het de laatste layer betreft, die heeft geen bias node.
        for(int i = 0; i < neuronValueArray[layerNumber+1].length; i++) {
            //System.out.println("Node " + nodeNumber + " heeft weight " + (nodeNumber*neuronValueArray[layerNumber+1].length+i) );
            list.add(nodeNumber*neuronValueArray[layerNumber+1].length+i);
        }
        return list;
    }

    //Returns list of numbers corresponding to the weights that are connecting TO a node.
    //i.e. situation (Node_a) =Weight_w=> (Node_b). findWeightsToNode(1,b) gives {w}
    private List findWeightsToNode(int layerNumber, int nodeNumber) {

        List<Integer> list = new ArrayList<>();

        for (int i = nodeNumber; i < (amountOfNodesPerLayer[layerNumber - 1] * amountOfNodesPerLayer[layerNumber]); i += amountOfNodesPerLayer[layerNumber]) {
            //System.out.println( weightValueArray[layerNumber-1][i] );
            //System.out.println("Weight w"+ i + " from layer " + (layerNumber-1) + " to layer " + layerNumber);
            list.add(i);
        }
        return list;
    }

    //Returns the node number where the weight is connecting towards
    //i.e. situation (Node_a) =Weight_w=> (Node_b). Result will be b.
    int findNodeToWeight(int weightLayerNumber, int weightNumber) {

        //System.out.println("Weight w " + weightNumber + " uit weightlaag " + weightLayerNumber + " hoort bij volgende node nummer " + weightNumber%amountOfNodesPerLayer[weightLayerNumber+1]);

        return weightNumber%amountOfNodesPerLayer[weightLayerNumber+1];
    }

    //Returns the node number where the weight is coming from
    //i.e. situation (Node_a) =Weight_w=> (Node_b). Result will be a.
    int findNodeFromWeight(int weightLayerNumber, int weightNumber) {

        //System.out.println("Weight w " + weightNumber + " uit weightlaag " + weightLayerNumber + " hoort bij vorige node nummer " + weightNumber/amountOfNodesPerLayer[weightLayerNumber+1]);

        return weightNumber/amountOfNodesPerLayer[weightLayerNumber+1];
    }

    void getBiasValue(int layerNumber) {

    }

    //give actual output
    //by means of an activation function, or other function
    double outputNode(double input){
        return (1/(1+exp(-(input))));

    }

}

