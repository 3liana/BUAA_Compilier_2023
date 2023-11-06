package middleend;

import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Value> symbols;

    //Constant Var Function
    public SymbolTable() {
        this.symbols = new HashMap<>();
    }

    public void addValue(Value value) {
        this.symbols.put(value.getName(), value);
    }
}
