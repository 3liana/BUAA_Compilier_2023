package middleend.value;

import middleend.Value;
import middleend.value.user.Instruction;

import java.util.ArrayList;

public class BasicBlock extends Value {
    public int registerNum;
    public ArrayList<Instruction> instructions;
    public BasicBlock(int num){
        this.registerNum = num;
        this.instructions = new ArrayList<>();
    }
    public void addInst(Instruction inst){
        this.instructions.add(inst);
    }
    public String getPrint(){
        //todo 非0的basicBlock也许还需要打印自己的号码
        StringBuilder sb = new StringBuilder();
        for(Instruction inst:this.instructions){
            sb.append(inst.getPrint());
        }
        return sb.toString();
    }

}
