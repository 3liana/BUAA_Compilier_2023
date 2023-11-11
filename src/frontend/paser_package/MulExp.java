package frontend.paser_package;

import java.util.ArrayList;

//UnaryExp的乘除 BNF
public class MulExp {
    public ArrayList<UnaryExp> unaryExps;
    public ArrayList<Integer> symbols;//1 * ;2 /;3:%
    public MulExp(UnaryExp unaryExp,Integer symbol) {
        this.unaryExps = new ArrayList<>();
        this.symbols = new ArrayList<>();
        this.unaryExps.add(unaryExp);
        this.symbols.add(symbol);
    }
    public void appendUnaryExp(UnaryExp unaryExp,Integer symbol){
        this.unaryExps.add(unaryExp);
        this.symbols.add(symbol);
    }
}
