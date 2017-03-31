import java.util.ArrayList;

/**
 * Created by joseph on 17/02/2017.
 */
public class MoveUtility {
   private Actions move;
   private double utility;
   private double[] values;

    MoveUtility(Actions move,double utility){
        this.move = move;
        this.utility = utility;
        values=null;
    }

    MoveUtility(Actions move,double utility,double [] values){
        this.move = move;
        this.utility = utility;
        this.values = values;
    }

    public double getUtility() {
        return utility;
    }

    public enum Actions {
        UP, DOWN, LEFT, RIGHT, IDLE, PLACEBOMB
    }


    public Actions getMove() {
        return move;
    }

    public double[] getValues() {
        return values;
    }
}
