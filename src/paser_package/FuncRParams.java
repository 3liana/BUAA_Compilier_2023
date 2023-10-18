package paser_package;

import java.util.ArrayList;

public class FuncRParams {
    private ArrayList<Exp> exps;
    public FuncRParams(Exp exp) {
        this.exps = new ArrayList<>();
        this.exps.add(exp);
    }
    public void appendExp(Exp exp){
        this.exps.add(exp);
    }
}
