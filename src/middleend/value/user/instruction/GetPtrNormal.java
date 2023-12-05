package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.PointerType;
import middleend.type.Type;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class GetPtrNormal extends Instruction {
    public Value result;
    public Value fromValue;
    //fromValue的getMyType结果为Pointer(SureArrayType)
    private Type targetType;
    private int type;
    public Value n;
    public Value m;
    public GetPtrNormal(BasicBlock b, Value result, Value from, Value n){
        //from肯定是一个PointType
        //只有一个n 不退
        super(b);
        this.result = result;
        PointerType fromType = (PointerType)from.getMyType();
        this.targetType = fromType.targetType;
        this.fromValue = from;

        this.n = n;
        this.type = 0;
        this.result.setMyType(fromType);

    }
    public GetPtrNormal(BasicBlock b, Value result, Value from, Value n, Value m){
        super(b);
        this.result = result;
        PointerType fromType = (PointerType)from.getMyType();
        this.targetType = fromType.targetType;
        this.fromValue = from;

        this.n = n;
        this.m = m;
        this.type = 1;
        //todo 确定result type
        //this.setResultType2();
        //this.result.setMyType(targetType);
    }
    public String getPrint(){
        String s0 = this.result.getName() + " = getelementptr " +
                this.targetType +", "+ fromValue.getMyType() + " " + fromValue.getName() + ", ";
        String  s1 = "i32 " + this.n.getName();
        if(this.type == 1){
            s1 = s1 + ", i32 " + this.m.getName();
        }
        return s0 + s1 + "\n";
        //return "normal" + s0 + s1 + "\n";
    }
}
