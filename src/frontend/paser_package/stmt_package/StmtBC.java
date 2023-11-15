package frontend.paser_package.stmt_package;

import frontend.lexer_package.Token;
import frontend.paser_package.Stmt;

public class StmtBC extends Stmt {
    public Token token;
    public StmtBC(Token token) {
        this.token = token;
    }
}
