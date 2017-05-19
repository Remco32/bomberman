import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by joseph on 09/02/2017.
 */
public class GameWorld {
    int gridSize; // in 1 dimension
    int amountPlayers;
    private int amountOfRounds = 0;
    private Boolean windowBool;
    private ShowWindow window;

    long startTimeTrials = System.currentTimeMillis();

    int ROUND_TIME_MILISECONDS = 500;

    RemcoAI AI_Remco;

    int trials;
    int totalAmountOfTrials;
    boolean agentMadeKill = false;
    boolean DELAY_BEFORE_START = true; //enables waiting so debuging is easier

    boolean DEBUGPRINTS = true;
    boolean SHOWROUNDS = true;

    WorldPosition[][] positions;
    private ArrayList<AIHandler> ai;
    ArrayList<BomberMan> bomberManList;
    ArrayList<Bomb> activeBombList;
    ArrayList<Bomb> explodedBombList;
    private double randomMoveChance;

    GameWorld(int gridSize, int amountOfPlayers, Boolean windowBool, int worldType) {
        this.gridSize = gridSize;
        this.amountPlayers = amountOfPlayers;
        bomberManList = new ArrayList<>();
        activeBombList = new ArrayList<>();
        explodedBombList = new ArrayList<>();

        this.windowBool = windowBool;
        if (worldType == 1) {
            initWorld(); //create default world
        }
        if (worldType == 2) {
            initEmptyWorld(); //create default world
        }

        if (windowBool) window = new ShowWindow(this);

    }

    //Give all the enemies the RandomAI
    void setEnemyAI() {
        ArrayList<AIHandler> ai = new ArrayList<>();
        for (int idx = 1; idx < amountPlayers; idx++) {
            ai.add(new RandomAI(this, this.bomberManList.get(idx))); //activates enemy AI
        }
        this.ai = ai;
    }

    private void initWorld() {
        positions = new WorldPosition[gridSize][gridSize];
        //init the grid
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                positions[x][y] = new WorldPosition(x, y, WorldPosition.Fieldtypes.SOFTWALL); // fills whole world with softwalls, will get overwritten later
                if (x % 2 == 1 && y % 2 == 1)
                    positions[x][y] = new WorldPosition(x, y, WorldPosition.Fieldtypes.HARDWALL); //add hardwalls at uneven positions
                //if (x == 0 || x == gridSize - 1) positions[x][y] = new WorldPosition(x, y, SOFTWALL); //redundant
                //if (y == 0 || y == gridSize - 1) positions[x][y] = new WorldPosition(x, y, SOFTWALL); //redundant
            }

        }

        // init the players

        int y = 0;
        int x = 0;
        int bomberManId = 1;
        if (amountPlayers > 4) amountPlayers = 4; // 4 is max amount of players
        if (amountPlayers < 1) amountPlayers = 1; // 1 is min amount of players
        for (int idx = 0; idx < amountPlayers && idx < 4; idx++) {
            for (int temp = x - 1; temp <= x + 1; temp++) {
                if (temp >= 0 && temp < gridSize) {
                    positions[temp][y].type = WorldPosition.Fieldtypes.EMPTY; //remove walls around bomberman @ y-axis
                }
            }

            for (int temp = y - 1; temp <= y + 1; temp++) {
                if (temp >= 0 && temp < gridSize) {
                    positions[x][temp].type = WorldPosition.Fieldtypes.EMPTY; //remove walls around bomberman @ x-axis
                }
            }
            bomberManList.add(new BomberMan(x, y, bomberManId++, this));
            positions[x][y].bombermanList.add(bomberManList.get(bomberManId - 2)); // min 2 because of indexing start at 1 and ++
            if (y == 0) y += gridSize - 1;
            else if (x == 0) {
                x += gridSize - 1;
                y = 0;
            }
        }
    }

    private void initEmptyWorld() {
        positions = new WorldPosition[gridSize][gridSize];
        //init the grid
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                positions[x][y] = new WorldPosition(x, y, WorldPosition.Fieldtypes.EMPTY); // fills whole world with emptyness. How deep.
                if (x % 2 == 1 && y % 2 == 1)
                    positions[x][y] = new WorldPosition(x, y, WorldPosition.Fieldtypes.HARDWALL); //add hardwalls at uneven positions
                //if (x == 0 || x == gridSize - 1) positions[x][y] = new WorldPosition(x, y, SOFTWALL); //redundant
                //if (y == 0 || y == gridSize - 1) positions[x][y] = new WorldPosition(x, y, SOFTWALL); //redundant
            }

        }

        //Set specific blocks
        //positions[0][2] = new WorldPosition(0,2, WorldPosition.Fieldtypes.SOFTWALL);
        //positions[2][2] = new WorldPosition(2,2, WorldPosition.Fieldtypes.SOFTWALL);
        //positions[2][0] = new WorldPosition(2,0, WorldPosition.Fieldtypes.SOFTWALL);
        positions[4][0] = new WorldPosition(4, 0, WorldPosition.Fieldtypes.SOFTWALL);
        positions[4][2] = new WorldPosition(4, 2, WorldPosition.Fieldtypes.SOFTWALL);
        positions[4][4] = new WorldPosition(4, 4, WorldPosition.Fieldtypes.SOFTWALL);
        positions[2][4] = new WorldPosition(2, 4, WorldPosition.Fieldtypes.SOFTWALL);
        positions[0][4] = new WorldPosition(0, 4, WorldPosition.Fieldtypes.SOFTWALL);
        //positions[0][7] = new WorldPosition(0,4, WorldPosition.Fieldtypes.SOFTWALL);

        // init the players

        int y = 0;
        int x = 0;
        int bomberManId = 1;
        if (amountPlayers > 4) amountPlayers = 4; // 4 is max amount of players
        if (amountPlayers < 1) amountPlayers = 1; // 1 is min amount of players
        for (int idx = 0; idx < amountPlayers && idx < 4; idx++) {

            bomberManList.add(new BomberMan(x, y, bomberManId++, this));
            positions[x][y].bombermanList.add(bomberManList.get(bomberManId - 2)); // min 2 because of indexing start at 1 and ++
            if (y == 0) y += gridSize - 1;
            else if (x == 0) {
                x += gridSize - 1;
                y = 0;
            }
        }
    }

    void cleanWorld() {

        //clean arrays
        ai.clear();
        bomberManList.clear(); // remove all bombermen
        activeBombList.clear();
        explodedBombList.clear();

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                positions[x][y].setType(WorldPosition.Fieldtypes.EMPTY);
            }
        }

        window.repaint();
    }

    void runGameLoop() {

        if (DELAY_BEFORE_START) {
            try {
                Thread.sleep(ROUND_TIME_MILISECONDS * 4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Thread loop = new Thread() {
            @Override
            public void run() {
                GameLoop();
            }
        };
        loop.start();

    }

    private void GameLoop() {

        //time execution time
        long startTime = System.currentTimeMillis();
        while (PlayerCheck()) {

            if (!(ai == null)) {
                for (AIHandler temp : ai) temp.CalculateBestMove();
                for (AIHandler temp : ai) {
                    temp.MakeMove(); // get the last appended move
                }
            }

            //Make requested move for the agent
            bomberManList.get(0).move(bomberManList.get(0).nextAction);

            // update all bombs
            for (Bomb bomb : activeBombList) {
                bomb.countdown();
            }
            for (Bomb bomb : activeBombList) {
                if (bomb.exploded) {
                    explodedBombList.add(bomb);
                }
            }
            for (Bomb bomb : explodedBombList) {
                bomb.cleanupCountdown();
                activeBombList.remove(bomb);
            }

            //update bomb cooldown
            for (BomberMan man : bomberManList) {
                man.updateBombCooldown();
            }

            amountOfRounds++;

            //game timestep
            if (windowBool) try {
                window.repaint();
                Thread.sleep(ROUND_TIME_MILISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //if(windowBool) window.repaint();
        }
        if (bomberManList.get(0).alive && DEBUGPRINTS) System.out.println("You won");
        else if (DEBUGPRINTS) System.out.println("You lost");

        if (DEBUGPRINTS) System.out.println("Amount of elapsed timesteps: " + amountOfRounds);
        long endTime = System.currentTimeMillis();
        if (DEBUGPRINTS) System.out.println("Elapsed time: " + (double) (endTime - startTime) / 1000 + " seconds");

        //restart game if there are still trials left to run
        if (trials > 1) { //first game isn't counted
            trials--;
            resetGame();
        }

        if (trials == 1) {
            AI_Remco.writeNetworkToFile();
            endGame();
        }

    }

    Boolean PlayerCheck() {
        if (!bomberManList.get(0).alive) return false;
        for (int idx = 1; idx < bomberManList.size(); idx++) {
            if (bomberManList.get(idx).alive) return true;
        }
        return false;
    }

    void resetGame() {
        if (DEBUGPRINTS) System.out.println();
        if (SHOWROUNDS) System.out.println("Game " + (totalAmountOfTrials - trials) + " started.");

        cleanWorld(); //empty world
        initWorld(); //reinitialize world

        amountOfRounds = 0;

        setEnemyAI();
        runGameLoop();
        AI_Remco.setBomberman(this.bomberManList.get(0)); //reset AI
        //AI_Remco.playQLearning(randomMoveChance);
        AI_Remco.play(3, 0.2);

    }

    void endGame() {
        long totalTimeElapsed = System.currentTimeMillis() - startTimeTrials;

        JFrame frame = new JFrame();
        //Show message
        JOptionPane.showMessageDialog(frame, "All trials have ended. The neural network is saved. Elapsed time: " + totalTimeElapsed + " ms.");

        //Close program
        System.exit(0);
    }

    void startGame(GameWorld world, int amountOfTrials, int amountHiddenNodes, int amountHiddenLayers, double learningRate, double randomMoveChance, int roundTimeInMs) {

        ROUND_TIME_MILISECONDS = roundTimeInMs;

        this.trials = amountOfTrials;
        this.totalAmountOfTrials = amountOfTrials;
        this.randomMoveChance = randomMoveChance;

        if (SHOWROUNDS) System.out.println("Game " + (totalAmountOfTrials - trials) + " started.");
        setEnemyAI();
        runGameLoop();
        this.AI_Remco = new RemcoAI(world, world.bomberManList.get(0), amountHiddenNodes, amountHiddenLayers, learningRate);
        AI_Remco.play(3, 0.2);
    }

    /**
    void startGame(neuralnet){


    }
     **/

}
