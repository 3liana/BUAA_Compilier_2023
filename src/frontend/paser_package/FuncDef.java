package frontend.paser_package;

public class FuncDef {
    private int type;
    private FuncType funcType;
    private Ident ident;
    private FuncFParams params;//可有可无
    private Block block;
    //无Param
    public FuncDef(FuncType funcType,Ident ident){
        this.funcType = funcType;
        this.ident = ident;
        this.type = 0;
    }
    public void addParams(FuncFParams params){
        this.params = params;
        this.type = 1;
    }
    public void addBlock(Block block){
        this.block = block;
    }
}
