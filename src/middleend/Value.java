package middleend;

import middleend.type.IntegerType;
import middleend.type.Type;

public class Value {
    public String getName(){
        //在llvm中打印的名字
        //@a %1...
        return "";
    }
    public String getTableName(){
        //在符号表中的名字
        //globalVar VarValue function
        //g1 a
        return "";
    }
    public Type getType() {
        return new IntegerType(32);
    }
}
