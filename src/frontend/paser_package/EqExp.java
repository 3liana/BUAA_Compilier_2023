package frontend.paser_package;

import frontend.lexer_package.Token;

import java.util.ArrayList;

//改写为右递归
public class EqExp {
    public ArrayList<RelExp> relExps;
    public ArrayList<Token> tokens;//长度为relExps - 14
    //Category 为EQL 或 NEQ
    public EqExp(RelExp relExp){
        this.relExps = new ArrayList<RelExp>();
        this.tokens = new ArrayList<Token>();
        this.relExps.add(relExp);
    }
    public void appendRelExp(RelExp relExp,Token token){
        this.relExps.add(relExp);
        this.tokens.add(token);
    }
}
//    private int type;
//    private RelExp leftExp;
//    private Token token;
//    private EqExp rightExp;
//    public EqExp(RelExp leftExp) {
//        this.leftExp = leftExp;
//        this.type = 0;
//    }
//    public EqExp(RelExp leftExp,Token token,EqExp rightExp){
//        this.leftExp = leftExp;
//        this.token = token;
//        this.rightExp = rightExp;
//        this.type = 1;
//    }