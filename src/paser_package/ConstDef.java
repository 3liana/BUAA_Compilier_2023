package paser_package;

import java.util.ArrayList;

public class ConstDef {
    private Ident ident;
    private ArrayList<ConstExp> exps;
    private ConstInitVal constInitVal;
    public ConstDef(Ident ident){
        this.ident = ident;
        this.exps = new ArrayList<>();
    }
    public void appendExp(ConstExp exp){
        this.exps.add(exp);
    }
    public void setInitVal(ConstInitVal initVal){
        this.constInitVal = initVal;
    }
}
