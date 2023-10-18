package paser_package.stmt_package;

import lexer_package.Token;
import paser_package.*;

import java.util.ArrayList;

public class StmtPrint extends Stmt {
    private Token token;//STRCON
    private ArrayList<Exp> exps;

    public StmtPrint(Token token){
        this.token = token;
        this.exps = new ArrayList<>();
    }
    public void appendExp(Exp exp){
        this.exps.add(exp);
    }
}
