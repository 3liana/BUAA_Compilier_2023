package frontend.paser_package;

public class FuncDef {
    public int type;//0:无形参 1：有形参
    public FuncType funcType;
    public Ident ident;
    public FuncFParams params;//可有可无
    public Block block;
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
