package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.PointerType;
import middleend.type.Type;
import middleend.value.ConstValue;
import middleend.value.VarValue;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class StoreInst extends Instruction {
    //store某Type的变量给它的Pointer
    public Value fromValue;
    public Value toValue;
    private Type fromType = new IntegerType(32);
    public StoreInst(BasicBlock basicBlock,Value fromValue,Value toValue){
        super(basicBlock);
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.toValue.setNum(this.fromValue.getNum());
        this.fromType = fromValue.getMyType();
        if(toValue.getMyType() == null){//不太可能有这种情况吧
            toValue.setMyType(new PointerType(this.fromType));
        }
    }
    public StoreInst(BasicBlock basicBlock,Value fromValue,Value toValueArray,int n){
        //一维数组的第n位
        //store fromValue 给 toValue(数组）的第n位
//        super(basicBlock);
        this.fromValue = fromValue;
        //计算toValue
        int registerNum = basicBlock.belongFunction.assignRegister();
        VarValue v = new VarValue(registerNum,false);
        new GetPtrInstSureArray(basicBlock,v,toValueArray,n);
        basicBlock.addInst(this);
        this.toValue = v;
    }
    public StoreInst(BasicBlock basicBlock,Value fromValue,Value toValueArray,int n,int m){
        //在这里面增加求地址的inst
        //二维数组的第n，m位
//        super(basicBlock);
        this.fromValue = fromValue;
        //计算toValue
        int registerNum = basicBlock.belongFunction.assignRegister();
        VarValue v = new VarValue(registerNum,false);
        new GetPtrInstSureArray(basicBlock,v,toValueArray,n,m);
        basicBlock.addInst(this);
        this.toValue = v;

    }
    public String getPrint(){
        return "store " + fromType + " " + fromValue.getName() +", " +
                fromType + "* " + toValue.getName() + "\n";
    }
    public void replaceValueWithConst(Value oldValue, ConstValue newConst){
        if(this.fromValue.equals(oldValue)){
            this.fromValue = newConst;
        }
    }
}
