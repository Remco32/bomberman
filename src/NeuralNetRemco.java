/**
 * Created by Remco on 5-3-2017.
 */

//TODO list met tuples vervangen

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


import static java.lang.Integer.max;
import static java.lang.Math.exp;

class NeuralNetRemco {

    //DEBUG BOOLEANS
    private boolean DEBUG_EXAMPLE_ARRAYS = false;
    private boolean DEBUG_RANDOM_INPUT = false;

    private int BIASVALUE = 1;

    private double learningRate;

    private int amountHiddenLayers;
    private int[] amountOfNodesPerLayer;
    private double[][] inputSet;
    private double[][] targetOutputSet;
    private double[] targetOutput;
    private List<Double> totalError = new ArrayList<>(); //list so we can keep the previous values

    private double[][] neuronValueArray;
    private double[][] weightValueArray;

    //Constructor
    //TODO change targetSet to targetVector
    public NeuralNetRemco(double[][] inputVector, int amountHiddenNodes, int amountHiddenLayers, double[][] targetOutput, double learningRate) {

        //store values
        this.amountHiddenLayers = amountHiddenLayers;
        this.targetOutputSet = targetOutput;
        this.learningRate = learningRate;

        //set our own targetoutput to the first set
        this.targetOutput = targetOutputSet[0];

        //Store amount of nodes per layer in array //TODO replace amountOfNodesPerLayer with array.length when initial sizes are correct
        this.amountOfNodesPerLayer = new int[amountHiddenLayers + 2];
        this.amountOfNodesPerLayer[0] = inputVector[0].length;
        for (int i = 1; i < amountHiddenLayers + 1; i++) {
            this.amountOfNodesPerLayer[i] = amountHiddenNodes;
        }
        this.amountOfNodesPerLayer[amountHiddenLayers + 1] = targetOutput[0].length;

        this.neuronValueArray = new double[amountHiddenLayers + 2][max(max(inputVector.length, amountHiddenNodes), targetOutput.length)]; //+2 for input and output layer


        for (int i = 0; i < amountHiddenLayers + 2; i++) {
            this.neuronValueArray[i] = new double[amountOfNodesPerLayer[i]];
        }
        this.inputSet = inputVector;


        this.weightValueArray = new double[amountHiddenLayers + 1][max(max(inputVector.length, amountHiddenNodes), targetOutput.length)];

        //set sizes of weightValueArray
        for(int layer = 0; layer < (amountHiddenLayers +1); layer++){
            this.weightValueArray[layer] = new double[(amountOfNodesPerLayer[layer] * amountOfNodesPerLayer[layer+1])+1];
        }

        //initialize weights
        for(int layer = 0; layer < (amountHiddenLayers +1); layer++) {
            this.weightValueArray[layer] = initializeArrayRandomValues(((amountOfNodesPerLayer[layer] * amountOfNodesPerLayer[layer+1])+1), 0, 1);
        }

        //DEBUG_EXAMPLE_ARRAYS INITIALIZATION
        if (DEBUG_EXAMPLE_ARRAYS) {
            neuronValueArray[0] = new double[]{.05, .1};
            weightValueArray[0] = new double[]{.15, .25, .2, .30, .35};
            weightValueArray[1] = new double[]{.4, .5, .45, .55, .6};
        }
        if (DEBUG_RANDOM_INPUT) {
            for (int i = 0; i < inputVector.length; i++) {
                neuronValueArray[0][i] = (double) ThreadLocalRandom.current().nextInt(1 * 1000, 2 * 1000) / 1000;
            }
        }

    }




    //Methods

    void changeTargetOutputSet(double[][] newTargetOutputSet){
        targetOutputSet = newTargetOutputSet;
    }

    double[] getTargetOutput(){
        return targetOutput;
    }


    private double[] initializeArrayRandomValues(int rows, int min, int max) {
        double[] outputArray = new double[rows];

        //initialize all rows
        for (int r = 0; r <= rows - 1; r++) {
            outputArray[r] = (double) ThreadLocalRandom.current().nextInt(min * 1000, max * 1000 + 1) / 1000;
        }

        return outputArray;
    }



    void learn(int epochs){
        neuronValueArray[0] = inputSet[0];
        targetOutput = targetOutputSet[0];
            for (; epochs > 0 ; epochs--) {
                for (int i = inputSet.length; i > 0 ; i--){
                    neuronValueArray[0] = inputSet[i-1];
                    targetOutput = targetOutputSet[i-1];
                    forwardPass();
                    backwardsPass();

                System.out.println(totalError.get(totalError.size() - 1)); //pakt nieuwste element
            }
        }


    }

    /**
    double[] getOutput(double[] input){
        neuronValueArray[0] = input;
        for (int layer = 1; layer < amountHiddenLayers + 2; layer++) //amountHiddenLayers+1 == final layer index
            //amount of nodes
            for (int node = 0; node < amountOfNodesPerLayer[layer]; node++) {
                //Get the netInput, transform that into the output, and update the value
                neuronValueArray[layer][node] = outputNode(netInputNode(layer, node));
            }
        return neuronValueArray[amountOfNodesPerLayer.length-1];
    }
    **/

    double[] getOutputLayer(){
        return neuronValueArray[amountHiddenLayers+1]; //hiddenlayer + input + output, index starts at 0
    }


    void forwardPass(double[] input){
        //Change the input of the network
        neuronValueArray[0] = input;
        //Do the forwardpass
        forwardPass();
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
        //if(neuronValueArray weightValueArray[].length - 1

        //UPDATE FROM INPUT TO OUTPUT (LEFT TO RIGHT)
        updateWeightsHiddenLayer();
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

            if(neuronValueArray[layerNumber - 1][i] != 0) {
                value += weightValueArray[layerNumber - 1][list.get(i)] * neuronValueArray[layerNumber - 1][i];
            }
            //No need for else statement, value will be 0 in that case. Adding 0 to the total value is useless.
        }
        //add bias: weight multiplied by biasvalue
        value += weightValueArray[layerNumber - 1][weightValueArray[layerNumber - 1].length - 1] * BIASVALUE;
        //System.out.println(value);
        return value;

        //multiply all weights by the inputSet + bias

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

            double gradient;
            int nodeToWeight = findNodeToWeight(outputLayer - 1, weight);
            int nodeFromWeight = findNodeFromWeight(outputLayer - 1, weight);

            gradient = neuronValueArray[outputLayer][nodeToWeight] - targetOutput[nodeToWeight];
            gradient *= (neuronValueArray[outputLayer][nodeToWeight] * (1 - neuronValueArray[outputLayer][nodeToWeight]));
            gradient *= neuronValueArray[outputLayer - 1][nodeFromWeight];
            //System.out.println("Gradient van weight " + weight + " = " + gradient);

            //apply learning rate & update weight
            weightValueArray[outputLayer - 1][weight] = weightValueArray[outputLayer - 1][weight] - learningRate * gradient;
            //System.out.println("Updated value van weight " + weight + " = " + weightValueArray[outputLayer - 1][weight]);
            //System.out.println();

        }

    }

    private void updateWeightsHiddenLayer() {

        //TODO expand to multiple hidden layers

        //DEBUG
        //int weight = 0; //first weight
        int weightLayer = 0;  //of first layer of weights

        for (int weight = 0; weight < weightValueArray[weightLayer].length - 1; weight++) { //-1 because we skip the bias

            //get relevant nodes and weights, for this weight
            int inputNodeNumber = findNodeFromWeight(weightLayer, weight); //i1
            //System.out.println("inputNodeNumber for " + weight + " at weightLayer " + weightLayer + " = " + inputNodeNumber);

            int outputNodeNumber = findNodeToWeight(weightLayer, weight); //h1
            //System.out.println("ownOutputNodeNumber for " + weight + " at weightLayer " + weightLayer + " = " + outputNodeNumber);

            List<Integer> outputOutputWeights;
            outputOutputWeights = findWeightsFromNode(weightLayer + 1, outputNodeNumber); //w5, w7
            //System.out.println("outputOutputWeights for " + outputNodeNumber + " at weightLayer " + (weightLayer + 1) + " = " + outputOutputWeights);

            ////////////////

            double gradientTotalErrorToOutputHid = 0;
            for (int i = 0; i < outputOutputWeights.size(); i++) { //i is the outputNode
                int outputWeight = outputOutputWeights.get(i);

                int outputNode = findNodeToWeight(weightLayer + 1, outputOutputWeights.get(i));

                double out_i = neuronValueArray[weightLayer + 2][outputNode];
                double target_i = targetOutput[outputNode];

                //the gradient of Error_i with respect to output_i
                double gradientErrorOutToOutputOut = out_i - target_i;
                //the gradient of output_i with respect to net_i
                double gradientOutputOutToNetOut = out_i * (1 - out_i);

                //the gradient of Error_i with respect to net_i
                double gradientErrorOutToNetOut = gradientErrorOutToOutputOut * gradientOutputOutToNetOut;

                double gradientNetOutToOutputHid = weightValueArray[weightLayer + 1][outputWeight];

                double gradientErrorOutToOutputHid = gradientErrorOutToNetOut * gradientNetOutToOutputHid;
                gradientTotalErrorToOutputHid += gradientErrorOutToOutputHid;
            }

            double gradientOutputHidToNetHid = neuronValueArray[weightLayer + 1][outputNodeNumber] * (1 - neuronValueArray[weightLayer + 1][outputNodeNumber]);
            double gradientTotalErrorToWeight = gradientTotalErrorToOutputHid * gradientOutputHidToNetHid * neuronValueArray[weightLayer][inputNodeNumber];

            //apply learning rate & update weight
            weightValueArray[weightLayer][weight] = weightValueArray[weightLayer][weight] - learningRate * gradientTotalErrorToWeight;

        }
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
    private int findNodeToWeight(int weightLayerNumber, int weightNumber) {

        //System.out.println("Weight w " + weightNumber + " uit weightlaag " + weightLayerNumber + " hoort bij volgende node nummer " + weightNumber%amountOfNodesPerLayer[weightLayerNumber+1]);

        return weightNumber%amountOfNodesPerLayer[weightLayerNumber+1];
    }

    //Returns the node number where the weight is coming from
    //i.e. situation (Node_a) =Weight_w=> (Node_b). Result will be a.
    private int findNodeFromWeight(int weightLayerNumber, int weightNumber) {

        //System.out.println("Weight w " + weightNumber + " uit weightlaag " + weightLayerNumber + " hoort bij vorige node nummer " + weightNumber/amountOfNodesPerLayer[weightLayerNumber+1]);

        return weightNumber/amountOfNodesPerLayer[weightLayerNumber+1];
    }


    //give actual output
    //by means of an activation function, or other function
    private double outputNode(double input){
        return (1/(1+exp(-(input))));

    }


}

