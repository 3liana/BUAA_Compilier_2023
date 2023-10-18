package paser_package.stmt_package;

import lexer_package.Token;
import paser_package.Stmt;

public class StmtBC extends Stmt {
    private Token token;
    public StmtBC(Token token) {
        this.token = token;
    }
}
