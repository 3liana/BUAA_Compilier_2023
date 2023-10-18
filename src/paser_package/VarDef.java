package paser_package;

import java.util.ArrayList;

public class VarDef {
    //0:无赋值部分；1：有赋值部分
    private int type;
    private Ident ident;
    private ArrayList<ConstExp> exps;
    private InitVal initVal;
    public VarDef(Ident ident){
        this.type = 0;
        this.ident = ident;
        this.exps = new ArrayList<>();
    }
    public void appendExp(ConstExp exp){
        this.exps.add(exp);
    }
    public void setInitVal(InitVal initVal){
        this.initVal = initVal;
        this.type = 1;
    }
}
