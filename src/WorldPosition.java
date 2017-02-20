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
    int x_location;
    int y_location;
    int type; //0=hardwall,1=softwall,2=no wall

     WorldPosition(int x,int y,int type){
        this.x_location=x;
        this.y_location=y;
        this.type=type;
        bombermanList = new ArrayList<>();
    }

    void add_Bomb(Bomb bomb){
        this.bomb = bomb;
    }
    void deleteBomb(){this.bomb = null;}

    void add_bomberman(BomberMan bomberman){
        bombermanList.add(bomberman);
    }
    void deleteBomberman(BomberMan bomberman){
        bombermanList.remove(bomberman);
    }


}
