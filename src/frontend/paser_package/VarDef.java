package frontend.paser_package;

import java.util.ArrayList;

public class VarDef {
    //0:无赋值部分；1：有赋值部分
    public int type;
    public boolean isArray;
    public Ident ident;
    public ArrayList<ConstExp> exps;
    public InitVal initVal;
    public VarDef(Ident ident){
        this.type = 0;
        this.ident = ident;
        this.exps = new ArrayList<>();
        this.isArray = false;
    }
    public void appendExp(ConstExp exp){
        this.exps.add(exp);
        this.isArray = true;
    }
    public void setInitVal(InitVal initVal){
        this.initVal = initVal;
        this.type = 1;
    }
    public String getName(){return this.ident.getName();}

}
