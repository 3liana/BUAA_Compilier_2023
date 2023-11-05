package frontend.paser_package.stmt_package;

import frontend.paser_package.*;

public class StmtLValExp extends Stmt {
    private LVal lVal;
    private Exp exp;
    public StmtLValExp(LVal lVal,Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }
}
