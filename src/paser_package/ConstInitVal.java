package paser_package;

import java.util.ArrayList;

public class ConstInitVal {
    //为0：无{}，且只有一个exp；1:有{},无initval;2：有{}和>=1个initval
    private int type;
    private ConstExp exp;
    private ArrayList<ConstInitVal> initVals;
    public ConstInitVal(ConstExp exp){
        this.type = 0;
        this.exp = exp;
    }
//    public ConstInitVal(int type, ConstExp exp) {
//        this.exps = new ArrayList<>();
//        this.exps.add(exp);
//        this.type = type;
//    }
    public ConstInitVal(){
        this.type = 1;
    }
    public ConstInitVal(ConstInitVal initVal){
        this.initVals = new ArrayList<ConstInitVal>();
        this.initVals.add(initVal);
        this.type = 2;
    }
    public void appendInitVal(ConstInitVal initVal){
        this.initVals.add(initVal);
    }
//    public void appendExp(ConstExp exp) {
//        this.exps.add(exp);
//    }
}
