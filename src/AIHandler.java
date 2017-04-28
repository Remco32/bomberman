import java.util.ArrayList;
import java.util.Random;

/**
 * Created by joseph on 9-2-2017.
 */

public class AIHandler {

    GameWorld world;
    BomberMan man;
    Random rnd;
    ArrayList<MoveUtility> moves;

    AIHandler(GameWorld world,BomberMan man) {
        this.world = world;
        this.man = man;
        moves = new ArrayList<>();
        rnd = new Random();
    }



    void MakeMove(){
       man.move(moves.get(moves.size()-1).getMove());
        //man.waitForNextTurn();
    }

    void CalculateBestMove() {
        if (man.alive) moves.add(new MoveUtility(randomAction(), 0));
    }

    MoveUtility.Actions randomAction(){
        int pick = new Random().nextInt(MoveUtility.Actions.values().length);
        return MoveUtility.Actions.values()[pick];
    }

}
