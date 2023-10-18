package paser_package;

public class BlockItem {
    private int type;
    private Decl decl;
    private Stmt stmt;
    public BlockItem(Decl decl){
        this.decl = decl;
        this.type = 0;
    }
    public BlockItem(Stmt stmt){
        this.stmt = stmt;
        this.type = 1;
    }
}
