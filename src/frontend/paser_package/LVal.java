package frontend.paser_package;

import java.util.ArrayList;

public class LVal {
    public Ident ident;
    public ArrayList<Exp> exps;
    public LVal(Ident ident){
        this.ident = ident;
        this.exps = new ArrayList<>();
    }
    public void appendExp(Exp Exp){
        this.exps.add(Exp);
    }
}
