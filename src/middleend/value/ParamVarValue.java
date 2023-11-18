package middleend.value;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.PointerType;
import middleend.type.SureArrayType;

public class ParamVarValue extends Value {
    private int registerNum;
    public String tableName;
    public int m = -1;//形参的维度数必须是确定的
    private int type;
    public ParamVarValue(int registerNum,String tableName,int type){
        this.registerNum = registerNum;
        this.tableName = tableName;
        this.type = type;
    }
    public void calMyType(){
        if(this.type == 0){
            this.setMyType(IntegerType.i32Type);
        } else if(this.type == 1){
            this.setMyType(new PointerType(IntegerType.i32Type));
        } else {
            this.setMyType(new PointerType(new SureArrayType(this.m)));
        }
    }
    public String getTableName(){
        return tableName;
    }
    public String getName(){
        return "%" + registerNum;
    }

}
