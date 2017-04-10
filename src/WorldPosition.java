import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;

/**
 * Created by joseph on 09/02/2017.
 *
 * Every position in the world grid is a world position
 */
public class WorldPosition {
    ArrayList<BomberMan> bombermanList;
    Bomb bomb;
    private int x_location;
    private int y_location;
    Fieldtypes type;
    int dangerousTimer = 0;

     WorldPosition(int x,int y,Fieldtypes type){
        this.x_location=x;
        this.y_location=y;
        this.type=type;
        bombermanList = new ArrayList<>();
    }

    public enum Fieldtypes {
        EMPTY, SOFTWALL, HARDWALL, EXPLOSION
    }

    //counts down danger timer
    void countDown(){
        dangerousTimer = bomb.getTimer();
    }

    void addBomb(Bomb bomb){
        this.bomb = bomb;
    }
    void deleteBomb(){this.bomb = null;}

    void addBomberman(BomberMan bomberman){
        bombermanList.add(bomberman);
    }
    void deleteBomberman(BomberMan bomberman){
        bombermanList.remove(bomberman);
    }

    Fieldtypes getType(){
        return type;
    }



}
