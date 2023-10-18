package paser_package;

public class Decl {
    private int type;
    private ConstDecl constDecl;
    private VarDecl varDecl;
    public Decl(ConstDecl constDecl){
        this.type = 0;
        this.constDecl = constDecl;
    }
    public Decl(VarDecl varDecl){
        this.type = 1;
        this.varDecl = varDecl;
    }
}
