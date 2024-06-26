package frontend.paser_package.stmt_package;

import frontend.lexer_package.Token;
import frontend.paser_package.*;

import java.util.ArrayList;

public class StmtPrint extends Stmt {
    public Token token;//STRCON
    public ArrayList<Exp> exps;

    public StmtPrint(Token token){
        this.token = token;
        this.exps = new ArrayList<>();
    }
    public void appendExp(Exp exp){
        this.exps.add(exp);
    }
}
