package middleend.value.user.instruction.terminateInst;

import middleend.Value;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class BrInst extends Instruction {
    private int type;
    //0 : br i1 <condValue>, label <iftrue>, label <iffalse>
    //1: br label <dest>
    private Value condValue;
    private Value ifTure;//BasicBlock
    private Value ifFalse;//BasicBlock
    private Value dest;//BasicBlock
    public BrInst(BasicBlock b,Value condValue, Value ifTure, Value ifFalse){
        super(b);
        this.type = 0;
        this.condValue = condValue;
        this.ifTure = ifTure;
        this.ifFalse = ifFalse;
    }
    public BrInst(BasicBlock b,Value dest){
        super(b);
        this.type = 1;
        this.dest = dest;
    }
    public String getPrint(){
        if(this.type == 0){
            return "br i1 " + condValue.getName() +", label " + ifTure.getName()
                    +", " + ifFalse.getName() + "\n";
        } else {
            return "br label " + dest.getName() + "\n";
        }
    }

}
