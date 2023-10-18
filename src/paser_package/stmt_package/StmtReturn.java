package paser_package.stmt_package;

import paser_package.*;

public class StmtReturn extends Stmt {
    private Exp exp;
    private int type;
    public StmtReturn(){
        this.type = 0;
    }
    public StmtReturn(Exp exp){
        this.exp = exp;
        this.type = 1;

    }
}
