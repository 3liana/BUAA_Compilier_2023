package frontend.paser_package;

import java.util.ArrayList;

public class FuncFParam {
    public Ident ident;
    public ConstExp constExp;
    public int type;
    //0 0维
    //1 1维
    //2 2维（只有这种情况有constExp)
    public FuncFParam(Ident ident){
        this.ident = ident;
        this.constExp = null;
        this.type = 0;
    }
    public void appendExp(ConstExp exp){
        this.constExp= exp;
    }
}
