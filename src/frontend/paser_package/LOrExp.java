package frontend.paser_package;

import java.util.ArrayList;

public class LOrExp {
    public ArrayList<LAndExp> exps;
    public LOrExp(LAndExp exp){
        this.exps = new ArrayList<>();
        this.exps.add(exp);
    }
    public void appendLAndExp(LAndExp exp){
        this.exps.add(exp);
    }
}
//    private int type;
//    private LAndExp leftExp;
//    private Token token;
//    private LOrExp rightExp;
//    public LOrExp(LAndExp leftExp){
//        this.leftExp = leftExp;
//        this.type = 0;
//    }
//    public LOrExp(LAndExp leftExp,Token token,LOrExp rightExp){
//        this.leftExp = leftExp;
//        this.token = token;
//        this.rightExp = rightExp;
//        this.type = 1;
//    }