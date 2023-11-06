package frontend.paser_package;

import java.util.ArrayList;

public class ConstDef {
    public Ident ident;
    public ArrayList<ConstExp> exps;
    public ConstInitVal constInitVal;
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
    public String getName(){
        return this.ident.getName();
    }
}
