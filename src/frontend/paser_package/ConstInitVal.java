package frontend.paser_package;

import java.util.ArrayList;

public class ConstInitVal {
    //为0：无{}，且只有一个exp；1:有{},无initval;2：有{}和>=1个initval
    public int type;
    public ConstExp exp;
    public ArrayList<ConstInitVal> initVals;
    public ConstInitVal(ConstExp exp){
        this.type = 0;
        this.exp = exp;
    }

    public ConstInitVal(){
        this.type = 1;
        this.initVals = new ArrayList<>();
    }
    public ConstInitVal(ConstInitVal initVal){
        this.initVals = new ArrayList<ConstInitVal>();
        this.initVals.add(initVal);
        this.type = 2;
    }
    public void appendInitVal(ConstInitVal initVal){
        this.initVals.add(initVal);
    }
}
