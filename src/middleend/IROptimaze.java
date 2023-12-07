package middleend;

import middleend.value.user.BasicBlock;
import middleend.value.user.Function;
import middleend.value.user.Instruction;

public class IROptimaze {
    private IRModule module = IRModule.getModuleInstance();
    public void optimaze(){
        //对module进行更改
        //在compiler中输出更改后的module
        this.optimazeFunction(module.mainFunction);
        for(Function function:module.functions){
            this.optimazeFunction(function);
        }
    }
    public void optimazeFunction(Function function){
        function.startOptimaze();//标记一些function为死
        function.optimazeBlockJump();
        //优化function里的basicblock
        for(BasicBlock block:function.basicBlocks){
            this.optimazeBasicBlock(block);
        }
    }
    public void optimazeBasicBlock(BasicBlock basicBlock){
        for(Instruction instruction:basicBlock.instructions){
            this.optimazeInstruction(instruction);
        }
    }
    public void optimazeInstruction(Instruction instruction){

    }
}
