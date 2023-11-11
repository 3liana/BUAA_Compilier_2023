package middleend.value.user;

import middleend.value.BasicBlock;
import middleend.value.User;

public class Instruction extends User {
    private BasicBlock basicBlock;
    public Instruction(){}
    public Instruction(BasicBlock basicBlock){
        this.basicBlock = basicBlock;
        basicBlock.addInst(this);
    }
    public String getPrint(){
        return "";
    }
}
