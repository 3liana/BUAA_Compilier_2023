package frontend.paser_package;

import java.util.ArrayList;

//多个MulExp的加减BNF
public class AddExp {
    public ArrayList<MulExp> mulExps;
    //1表示+ -1表示-
    public ArrayList<Integer> symbols;
    public AddExp(MulExp mulExp,int symbol){
        this.mulExps = new ArrayList<>();
        this.symbols = new ArrayList<>();
        this.mulExps.add(mulExp);
        this.symbols.add(symbol);
    }
    public void appendMulExp(MulExp mulExp,int symbol){
        this.mulExps.add(mulExp);
        this.symbols.add(symbol);
    }
    public boolean isConstExp(){
        //todo 建设中
        return false;
    }


}
