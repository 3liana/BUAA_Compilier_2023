package middleend.value;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.Type;

public class VarValue extends Value {
    private int type;
    private int registerNum;
    public boolean isConst;
    public String tableName;
    public VarValue(int registerNum,boolean isConst){
        this.registerNum = registerNum;
        this.isConst = isConst;
        this.type = 0;//临时变量 在符号表中没有对应的真正的名字 通过指令去确定Type就好了
    }
    public VarValue(int registerNum,boolean isConst,String tableName){
        //只有在decl的时候才会调用这个初始值
        this.registerNum = registerNum;
        this.isConst = isConst;
        this.type = 1;//在符号表中有对应的真正的名字
        this.tableName = tableName;
    }
    public VarValue(int registerNum,boolean isConst,boolean isI1){
        this.registerNum = registerNum;
        this.isConst = isConst;
        this.type = 0;//临时变量 在符号表中没有对应的真正的名字
        if(isI1 ){
            this.setMyType(IntegerType.i1Type);
        }
    }
    public String getTableName(){
        return tableName;
    }
    public String getName(){
        return "%" + "_" + registerNum;
    }


}
