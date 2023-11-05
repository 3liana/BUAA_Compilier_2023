package frontend.paser_package;

import frontend.lexer_package.Token;

import java.util.ArrayList;

//改写为右递归
public class RelExp {
    private ArrayList<AddExp> addExps;
    private ArrayList<Token> tokens;//长度为addExps - 1
    public RelExp(AddExp addExp){
        this.addExps = new ArrayList<AddExp>();
        this.tokens = new ArrayList<Token>();
        this.addExps.add(addExp);
    }
    public void appendAddExp(AddExp addExp,Token token){
        this.addExps.add(addExp);
        this.tokens.add(token);
    }
}
//    private int type;
//    private AddExp leftExp;
//    private RelExp rightExp;
//    private Token token;
//    public RelExp(AddExp leftExp){
//        this.leftExp = leftExp;
//        this.type = 0;
//    }
//    public RelExp(AddExp leftExp,RelExp rightExp,Token token){
//        this.leftExp = leftExp;
//        this.rightExp = rightExp;
//        this.token = token;
//        this.type = 1;
//    }
