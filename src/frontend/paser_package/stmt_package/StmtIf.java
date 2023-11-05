package frontend.paser_package.stmt_package;

import frontend.paser_package.*;

public class StmtIf extends Stmt {
    private int type;
    private Cond cond;
    private Stmt stmt;
    private Stmt elseStmt;
    public StmtIf(Cond cond,Stmt stmt){
        this.cond = cond;
        this.stmt = stmt;
        this.type = 0;
    }
    public void addElse(Stmt elseStmt){
        this.elseStmt = elseStmt;
        this.type = 1;
    }
}
