package frontend.paser_package;

import java.util.ArrayList;

public class UnaryExp {
    //0:PrimaryExp;1:Ident([FuncRParams]);UnaryOp UnaryExp
    public int type;
    //0:PrimaryExp
    public PrimaryExp primaryExp;
    //1:Ident([FuncRParams])
    public Ident ident;
    public FuncRParams funcRParams;//可能有可能没有要如何处理 暂时：如果没有数组为空
    //UnaryOp UnaryExp
    public UnaryOp unaryOp;
    public UnaryExp unaryExp;
    public UnaryExp(){}
    public UnaryExp(PrimaryExp primaryExp){
        this.primaryExp = primaryExp;
        this.type = 0;
    }
    public UnaryExp(Ident ident,FuncRParams funcRParams){
        this.ident = ident;
        this.funcRParams = funcRParams;
        this.type = 1;
    }
    public UnaryExp(Ident ident){
        this.ident = ident;
        this.funcRParams = null;
        this.type = 1;
    }
    public UnaryExp(UnaryOp unaryOp,UnaryExp unaryExp){
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
        this.type = 2;
    }
}
