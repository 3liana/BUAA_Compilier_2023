package frontend.paser_package;

import java.util.ArrayList;

//改写为右递归
public class LAndExp {
    private ArrayList<EqExp> exps;
    public LAndExp(EqExp exp){
        this.exps = new ArrayList<>();
        this.exps.add(exp);
    }
    public void appendEqExp(EqExp exp){
        this.exps.add(exp);
    }
}
//    private int type;
//    private EqExp leftExp;
//    private Token token;
//    private LAndExp rightExp;
//    public LAndExp(EqExp leftExp){
//        this.leftExp = leftExp;
//        this.type = 0;
//    }
//    public LAndExp(EqExp leftExp,Token token,LAndExp rightExp){
//        this.leftExp = leftExp;
//        this.token = token;
//        this.rightExp = rightExp;
//        this.type = 1;
//    }