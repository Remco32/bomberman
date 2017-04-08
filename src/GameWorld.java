import java.util.ArrayList;

/**
 * Created by joseph on 09/02/2017.
 */
public class GameWorld {
    int gridSize; // in 1 dimension
    int amountPlayers;
    private int amountOfRounds=0;
    private Boolean windowBool;
    private ShowWindow window;

    WorldPosition[][] positions;
    private ArrayList<AIHandler> ai;
    ArrayList<BomberMan> bomberManList;
    ArrayList<Bomb> activeBombList;
    ArrayList<Bomb> explodedBombList;


    GameWorld(int gridSize, int amountOfPlayers, Boolean windowBool, int worldType) {
        this.gridSize = gridSize;
        this.amountPlayers = amountOfPlayers;
        bomberManList = new ArrayList<>();
        activeBombList = new ArrayList<>();
        explodedBombList = new ArrayList<>();

        this.windowBool = windowBool;
        if (worldType == 1) {
            InitWorld(); //create default world
        }
        if (worldType == 2) {
            InitEmptyWorld(); //create default world
        }

        if (windowBool) window = new ShowWindow(this);

    }

    void SetAi(ArrayList<AIHandler> ai){
        this.ai=ai;
    }
    private void InitWorld() {
        positions = new WorldPosition[gridSize][gridSize];
        //init the grid
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                positions[x][y] = new WorldPosition(x, y, WorldPosition.Fieldtypes.SOFTWALL); // fills whole world with softwalls, will get overwritten later
                if (x % 2 == 1 && y % 2 == 1) positions[x][y] = new WorldPosition(x, y, WorldPosition.Fieldtypes.HARDWALL); //add hardwalls at uneven positions
                //if (x == 0 || x == gridSize - 1) positions[x][y] = new WorldPosition(x, y, SOFTWALL); //redundant
                //if (y == 0 || y == gridSize - 1) positions[x][y] = new WorldPosition(x, y, SOFTWALL); //redundant
            }

        }
        //TODO randomize the grid

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

    private void InitEmptyWorld() {
        positions = new WorldPosition[gridSize][gridSize];
        //init the grid
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                positions[x][y] = new WorldPosition(x, y, WorldPosition.Fieldtypes.EMPTY); // fills whole world with emptyness. How deep.
                if (x % 2 == 1 && y % 2 == 1) positions[x][y] = new WorldPosition(x, y, WorldPosition.Fieldtypes.HARDWALL); //add hardwalls at uneven positions
                //if (x == 0 || x == gridSize - 1) positions[x][y] = new WorldPosition(x, y, SOFTWALL); //redundant
                //if (y == 0 || y == gridSize - 1) positions[x][y] = new WorldPosition(x, y, SOFTWALL); //redundant
            }

        }

        //Set specific blocks
        //positions[0][2] = new WorldPosition(0,2, WorldPosition.Fieldtypes.SOFTWALL);
        //positions[2][2] = new WorldPosition(2,2, WorldPosition.Fieldtypes.SOFTWALL);
        //positions[2][0] = new WorldPosition(2,0, WorldPosition.Fieldtypes.SOFTWALL);
        positions[4][0] = new WorldPosition(4,0, WorldPosition.Fieldtypes.SOFTWALL);
        positions[4][2] = new WorldPosition(4,2, WorldPosition.Fieldtypes.SOFTWALL);
        positions[4][4] = new WorldPosition(4,4, WorldPosition.Fieldtypes.SOFTWALL);
        positions[2][4] = new WorldPosition(2,4, WorldPosition.Fieldtypes.SOFTWALL);
        positions[0][4] = new WorldPosition(0,4, WorldPosition.Fieldtypes.SOFTWALL);

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

    void RunGameLoop(){

        Thread loop = new Thread(){
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
            for(AIHandler temp:ai)temp.CalculateBestMove();
            for (AIHandler temp:ai) {
               temp.MakeMove(); // get the last appended move
            }
            // update all bombs
            for (Bomb bomb : activeBombList) {
                bomb.Countdown();

            }
            for (Bomb bomb : activeBombList) {
                if(bomb.exploded){
                    explodedBombList.add(bomb);
                }
            }
            for(Bomb bomb : explodedBombList){
                bomb.cleanupCountdown();
                activeBombList.remove(bomb);
            }


            amountOfRounds++;
            if(windowBool) try {
                window.repaint();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (bomberManList.get(0).alive) System.out.println("You won");
        else System.out.println("You lost");

        System.out.println(amountOfRounds);
        long endTime = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (double) (endTime - startTime)/1000 + " seconds");
    }

    private Boolean PlayerCheck() {
        if (!bomberManList.get(0).alive) return false;
        for (int idx = 1; idx < bomberManList.size(); idx++) {
            if (bomberManList.get(idx).alive) return true;
        }
        return false;
    }

}
