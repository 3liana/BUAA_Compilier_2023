package frontend.paser_package;

import java.util.ArrayList;

public class InitVal {
    //为0：无{}，且只有一个exp；1:有{},无initval;2：有{}和>=1个initval
    private int type;
    private Exp exp;
    private ArrayList<InitVal> initVals;
    public InitVal(Exp exp){
        this.type = 0;
        this.exp = exp;
    }
    public InitVal(){
        this.type = 1;
    }
    public InitVal(InitVal initVal){
        this.initVals = new ArrayList<InitVal>();
        this.initVals.add(initVal);
        this.type = 2;
    }
    public void appendInitVal(InitVal initVal){
        this.initVals.add(initVal);
    }
//    public void appendExp(Exp exp) {
//        this.exps.add(exp);
//    }
}
