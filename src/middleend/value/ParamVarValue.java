package middleend.value;

import middleend.Value;

public class ParamVarValue extends Value {
    private int registerNum;
    public String tableName;
    public ParamVarValue(int registerNum,String tableName){
        this.registerNum = registerNum;
        this.tableName = tableName;
    }
    public String getTableName(){
        return tableName;
    }
    public String getName(){
        return "%" + registerNum;
    }

}
