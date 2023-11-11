package frontend.paser_package.stmt_package;
import frontend.paser_package.*;
public class StmtExp extends Stmt{
    public int type;
    //0 无exp 只有一个;
    //1有exp
    public Exp exp;
    public StmtExp() {
        this.type = 0;
    }
    public StmtExp(Exp exp){
        this.exp = exp;
        this.type = 1;
    }
}
