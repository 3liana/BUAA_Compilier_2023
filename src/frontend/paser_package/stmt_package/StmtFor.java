package frontend.paser_package.stmt_package;
import frontend.paser_package.*;
public class StmtFor extends Stmt{
    private int type;//OneHot
    public ForStmt forStmt1 = null;
    public ForStmt forStmt2 = null;
    public Cond cond = null;
    public Stmt stmt;
    public StmtFor(){
        this.type = 0;
    }
    public void addStmt(Stmt stmt){
        this.stmt = stmt;
    }
    public void addFor1(ForStmt stmt){
        this.forStmt1 = stmt;
        this.type = this.type | 0b100;
    }
    public void addCond(Cond cond){
        this.cond = cond;
        this.type = this.type | 0b010;
    }
    public void addFor2(ForStmt stmt){
        this.forStmt2 = stmt;
        this.type = this.type | 0b001;
    }
}
