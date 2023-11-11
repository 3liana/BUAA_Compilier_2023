package frontend.paser_package;

import java.util.ArrayList;

public class FuncFParam {
    public Ident ident;
    private ArrayList<ConstExp> constExps;
    public FuncFParam(Ident ident){
        this.ident = ident;
        this.constExps = new ArrayList<>();
    }
    public void appendExp(ConstExp exp){
        this.constExps.add(exp);
    }
}
