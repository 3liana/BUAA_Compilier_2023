package middleend;

import java.util.ArrayList;

public class TableList {
    private ArrayList<SymbolTable> tables;

    public TableList() {
        this.tables = new ArrayList<>();
    }

    public void addTable() {
        SymbolTable symbolTable = new SymbolTable();
        this.tables.add(symbolTable);
    }

    public SymbolTable getCurSymbolTable() {
        return this.tables.get(this.tables.size() - 1);
    }
}
