package middleend.symbol;

import middleend.Value;

import java.util.HashMap;

public class SymbolTable {
    public HashMap<String, Value> symbols;

    //Constant Var Function
    public SymbolTable() {
        this.symbols = new HashMap<>();
    }

    public void addValue(Value value) {
        this.symbols.put(value.getTableName(), value);
    }
}
