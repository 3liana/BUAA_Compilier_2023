package frontend.paser_package.stmt_package;
import frontend.paser_package.*;
public class StmtExp extends Stmt{
    private int type;
    private Exp exp;
    public StmtExp() {
        this.type = 0;
    }
    public StmtExp(Exp exp){
        this.exp = exp;
        this.type = 1;
    }
}
