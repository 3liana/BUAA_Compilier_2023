package frontend.paser_package;

public class Decl {
    public int type;
    public ConstDecl constDecl;
    public VarDecl varDecl;
    public Decl(ConstDecl constDecl){
        this.type = 0;
        this.constDecl = constDecl;
    }
    public Decl(VarDecl varDecl){
        this.type = 1;
        this.varDecl = varDecl;
    }
}
