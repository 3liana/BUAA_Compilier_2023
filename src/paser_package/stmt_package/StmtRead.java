package paser_package.stmt_package;

import paser_package.*;

public class StmtRead extends Stmt {
    private LVal lVal;
    public StmtRead(LVal lVal) {
        this.lVal = lVal;
    }
}
