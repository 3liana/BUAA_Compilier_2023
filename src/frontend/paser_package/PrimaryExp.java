package frontend.paser_package;

public class PrimaryExp {
    public int type;
    //( Exp )
    public Exp exp;
    //LVal
    public LVal lVal;
    //Number
    public Number number;
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
