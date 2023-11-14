package frontend.paser_package.stmt_package;

import frontend.paser_package.*;

public class StmtIf extends Stmt {
    public int type;
    public Cond cond;
    public Stmt stmt;
    public Stmt elseStmt;
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
