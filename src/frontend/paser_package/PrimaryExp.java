package frontend.paser_package;

public class PrimaryExp {
    private int type;
    //( Exp )
    private Exp exp;
    //LVal
    private LVal lVal;
    //Number
    private Number number;
    public PrimaryExp(){

    }
    public PrimaryExp(Exp exp){
        this.exp = exp;
        this.type = 0;
    }
    public PrimaryExp(LVal lVal){
        this.lVal = lVal;
        this.type = 1;
    }
    public PrimaryExp(Number number){
        this.number = number;
        this.type = 2;
    }
}
