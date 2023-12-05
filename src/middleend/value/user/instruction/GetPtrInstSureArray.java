package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.PointerType;
import middleend.type.SureArrayType;
import middleend.type.Type;
import middleend.value.ConstValue;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class GetPtrInstSureArray extends Instruction {
    public Value result;
    public Value fromValue;
    //fromValue的getMyType结果为Pointer(SureArrayType)
    private Type targetType;
    public int type;
    public Value n;
    public Value m;
    private void setResultType1(){
        //退一层
        PointerType fromType = (PointerType)this.fromValue.getMyType();
        SureArrayType targetType = (SureArrayType) fromType.targetType;
        if(targetType.type == 0){
            //一维SureArray
            this.result.setMyType(new PointerType(IntegerType.i32Type));
        } else {
            //二维SureArray
            int m = targetType.m;
            this.result.setMyType(new PointerType(new SureArrayType(m)));
        }
    }
    private void setResultType2(){
        //退二层 二维退两层 变成i32指针
        this.result.setMyType(new PointerType(IntegerType.i32Type));
    }
    public GetPtrInstSureArray(BasicBlock b, Value result, Value from, int n){
        //读n位
        //退一层
        super(b);
        this.result = result;
        PointerType fromType = (PointerType)from.getMyType();
        this.targetType = fromType.targetType;
        this.fromValue = from;

        this.n = new ConstValue(String.valueOf(n));
        this.type = 0;
        this.setResultType1();
    }
    public GetPtrInstSureArray(BasicBlock b, Value result, Value from, int n, int m){
        //读n，m位
        //退两层
        super(b);
        this.result = result;
        PointerType fromType = (PointerType)from.getMyType();
        this.targetType = fromType.targetType;
        this.fromValue = from;

        this.n = new ConstValue(String.valueOf(n));
        this.m = new ConstValue(String.valueOf(m));
        this.type = 1;
        this.setResultType2();
    }
    public GetPtrInstSureArray(BasicBlock b, Value result, Value from, Value n){
        //from肯定是一个PointType
        super(b);
        this.result = result;
        PointerType fromType = (PointerType)from.getMyType();
        this.targetType = fromType.targetType;
        this.fromValue = from;

        this.n = n;
        this.type = 0;
        this.setResultType1();
    }
    public GetPtrInstSureArray(BasicBlock b, Value result, Value from, Value n, Value m){
        super(b);
        this.result = result;
        PointerType fromType = (PointerType)from.getMyType();
        this.targetType = fromType.targetType;
        this.fromValue = from;

        this.n = n;
        this.m = m;
        this.type = 1;
        this.setResultType2();
    }
    public String getPrint(){
        String s0 = this.result.getName() + " = getelementptr " +
                this.targetType +", "+ fromValue.getMyType() + " " + fromValue.getName() + ", ";
        String  s1 = "i32 0, i32 " + this.n.getName();
        if(this.type == 1){
            s1 = s1 + ", i32 " + this.m.getName();
        }
        return s0 + s1 + "\n";
    }

}
