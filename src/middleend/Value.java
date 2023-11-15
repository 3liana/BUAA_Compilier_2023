package middleend;

import middleend.type.IntegerType;
import middleend.type.Type;

public class Value {
    protected Type myType = null;
    protected int num = 0;
    public Type getMyType(){
        return this.myType;
    }
    public void setMyType(Type myType){
        this.myType = myType;
    }
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
    public int getNum(){
        //符号所代表的真实的值（在符号表中所能查找到的符号）
        //GlobalVar
        //VarValue
        //ParamValue
        return this.num;
    }
    public void setNum(int num){
        this.num = num;
    }
//    public Type getType() {
//        return new IntegerType(32);
//    }
}
