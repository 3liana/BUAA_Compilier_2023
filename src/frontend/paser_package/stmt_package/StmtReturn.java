package frontend.paser_package.stmt_package;

import frontend.paser_package.*;

public class StmtReturn extends Stmt {
    public Exp exp;
    public int type;
    public StmtReturn(){
        this.type = 0;
    }
    public StmtReturn(Exp exp){
        this.exp = exp;
        this.type = 1;
    }
}
