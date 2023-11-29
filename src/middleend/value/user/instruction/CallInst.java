package middleend.value.user.instruction;

import middleend.Value;
import middleend.value.user.BasicBlock;
import middleend.value.user.Function;
import middleend.value.user.Instruction;

import java.util.ArrayList;

public class CallInst extends Instruction {

    public Function function;
    public Value result = null;
    public ArrayList<Value> rParams;
    public CallInst(BasicBlock basicBlock,Function function, ArrayList<Value> rParams){
        super(basicBlock);
        this.function = function;
        this.rParams = rParams;
        this.runFunction();
    }
    public CallInst(BasicBlock basicBlock,Function function, ArrayList<Value> rParams,
                    Value result){
        super(basicBlock);
        this.function = function;
        this.rParams = rParams;
        this.result = result;
        this.result.setMyType(function.returnType);
        this.runFunction();
    }
    public void runFunction(){
        //深拷贝一个一模一样的function，只不过给形参setNum，然后深拷贝每一条指令，最后得到一个返回的
        //VarValue的getNum()
        //todo 考虑数据流
        //todo
    }

    public String getPrint(){
        String sResult = this.result == null ? "":this.result.getName() + " = ";
        //打印实参
        StringBuilder sParam = new StringBuilder();
        int i;
        for (i = 0; i < this.rParams.size() - 1; i++) {
            Value v = this.rParams.get(i);
            sParam.append(v.getMyType() + " " + v.getName());
            sParam.append(" , ");
        }
        if(this.rParams.size() - 1 >= 0){
            Value v = this.rParams.get(this.rParams.size() - 1);
            sParam.append(v.getMyType() + " " +v.getName());
        }
        //打印指令
        return sResult + "call " + function.returnType + " " + function.getName()  + "(" +
                sParam +
                ")\n";
    }

}
