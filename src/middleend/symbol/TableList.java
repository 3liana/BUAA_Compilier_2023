package middleend.symbol;

import middleend.Value;
import middleend.symbol.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;

public class TableList {
    private ArrayList<SymbolTable> tables;

    public TableList() {
        this.tables = new ArrayList<>();
    }

    public void addTable() {
        SymbolTable symbolTable = new SymbolTable();
        this.tables.add(symbolTable);
    }
    public void addATable(SymbolTable symbolTable){
        this.tables.add(symbolTable);
    }
    public void popTable(){
        this.tables.remove(this.tables.size()-1);
    }
    public SymbolTable getCurSymbolTable() {
        return this.tables.get(this.tables.size() - 1);
    }
    public Value foundDef(String name){
        // 逐层根据变量名查找value
        for(int i = this.tables.size()-1;i>=0;i--){
            Value value = this.tables.get(i).symbols.get(name);
            if (value != null){
                return value;
            }
        }
        return null;
    }
    public Value foundFuncDef(String name){
        SymbolTable tableZero = this.tables.get(0);
        Value value = tableZero.symbols.get(name);
        return value;
    }
    public void debugValueCalculation(){
        for(int i = 0;i < this.tables.size(); i++){
            SymbolTable tb = this.tables.get(i);
            HashMap<String, Value> map = tb.symbols;
            for(Value v:map.values()){
                StringBuilder sb = new StringBuilder();
                sb.append(v.getTableName());
                sb.append(" , ");
                sb.append(v.getNum());
                System.out.println(sb.toString());
            }
        }
    }
}
