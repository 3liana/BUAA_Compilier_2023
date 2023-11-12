package middleend.value.user;

import middleend.Value;
import middleend.value.user.Instruction;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private boolean isFirst;//是否是所属函数的第一个basicBlock
    public int registerNum;
    public ArrayList<Instruction> instructions;
    public BasicBlock(int num,boolean isFirst){
        this.isFirst = isFirst;
        this.registerNum = num;
        this.instructions = new ArrayList<>();
    }
    public void addInst(Instruction inst){
        this.instructions.add(inst);
    }
    public String getName(){
        return "%" + this.registerNum;
    }
    public String getPrint(){
        //todo 非0的basicBlock也许还需要打印自己的号码
        //注意 在function定义中第一个basicBlock的号码不一定是0
        StringBuilder sb = new StringBuilder();
        for(Instruction inst:this.instructions){
            sb.append(inst.getPrint());
        }
        return sb.toString();
    }

}
