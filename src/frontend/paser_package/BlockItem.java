package frontend.paser_package;

public class BlockItem {
    public int type;
    public Decl decl;
    public Stmt stmt;
    public BlockItem(Decl decl){
        this.decl = decl;
        this.type = 0;
    }
    public BlockItem(Stmt stmt){
        this.stmt = stmt;
        this.type = 1;
    }
}
