package frontend.paser_package.stmt_package;

import frontend.paser_package.*;

public class StmtRead extends Stmt {
    private LVal lVal;
    public StmtRead(LVal lVal) {
        this.lVal = lVal;
    }
}
