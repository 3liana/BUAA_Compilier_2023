package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.Type;
import middleend.value.BasicBlock;
import middleend.value.user.Function;
import middleend.value.user.Instruction;

import java.util.ArrayList;

public class CallInst extends Instruction {

    private Function function;
    private Value result = null;
    private ArrayList<Value> rParams;
    public CallInst(BasicBlock basicBlock,Function function, ArrayList<Value> rParams){
        super(basicBlock);
        this.function = function;
        this.rParams = rParams;
    }
    public CallInst(BasicBlock basicBlock,Function function, ArrayList<Value> rParams,
                    Value result){
        super(basicBlock);
        this.function = function;
        this.rParams = rParams;
        this.result = result;
    }
    public String getPrint(){
        String sResult = this.result == null ? "":this.result.getName() + " = ";
        //打印实参
        StringBuilder sParam = new StringBuilder();
        int i;
        for (i = 0; i < this.rParams.size() - 1; i++) {
            Value v = this.rParams.get(i);
            sParam.append(v.getType() + " " + v.getName());
            sParam.append(" , ");
        }
        if(this.rParams.size() - 1 >= 0){
            Value v = this.rParams.get(this.rParams.size() - 1);
            sParam.append(v.getType() + " " +v.getName());
        }
        //打印指令
        return sResult + "call " + function.returnType + " " + function.getName()  + "(" +
                sParam +
                ")\n";
    }

}
