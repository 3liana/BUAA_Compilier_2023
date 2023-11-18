package middleend.refill;

import middleend.value.user.BasicBlock;
import middleend.value.user.instruction.terminateInst.BrInst;

import java.util.ArrayList;

public class RefillUtil {
    //这些部分是If和For共通的，即Cond部分
    public ArrayList<ArrayList<BasicBlock>> refillBasicBlocks = new ArrayList<>();
    public ArrayList<BasicBlock> tempLevel = new ArrayList<>();
    public BasicBlock realTrueBlock1 = null;//if成立
    public BasicBlock realTrueBlock2 = null;//if成立
    public BasicBlock realFalseBlock = null;//if不成立
    public BasicBlock endBlock = null;
    public boolean hasCond = true;
    public RefillUtil(){
    }
    public void refill() {
        //根据文法，肯定是至少有一层lAndExp存在的
        BasicBlock beforeIf = refillBasicBlocks.get(0).get(0);
        if(hasCond){
            BasicBlock ifBlock = refillBasicBlocks.get(1).get(0);
            new BrInst(beforeIf, ifBlock);//无条件跳cond
        }else{
            //for中无cond的情况
            //即refillBasicBlocks只有第0层
            new BrInst(beforeIf,realTrueBlock1);
            return;
        }
        for (int i = 1; i < refillBasicBlocks.size() - 1; i++) {
            ArrayList<BasicBlock> level = refillBasicBlocks.get(i);//表示一个lAndExp
            for (int j = 0; j < level.size() - 1; j++) {
                //遍历第一个直到倒数第二个EqBlock块
                BasicBlock b = level.get(j);
                new BrInst(b, b.reVar, level.get(j + 1), refillBasicBlocks.get(i + 1).get(0));
                //为True遍历本层的下一个块，为false遍历下一层
            }
            BasicBlock b = level.get(level.size() - 1);
            new BrInst(b, b.reVar, realTrueBlock1, refillBasicBlocks.get(i + 1).get(0));
        }
        //最后一个LOrExp
        ArrayList<BasicBlock> level = refillBasicBlocks.get(refillBasicBlocks.size() - 1);
        for (int j = 0; j < level.size() - 1; j++) {
            //遍历第一个直到倒数第二个EqBlock块
            BasicBlock b = level.get(j);
            BasicBlock temp = this.realFalseBlock != null ? realFalseBlock : endBlock;
            new BrInst(b, b.reVar, level.get(j + 1), temp);
            //为True遍历本层的下一个块，为false遍历下一层
        }
        BasicBlock b = level.get(level.size() - 1);
        BasicBlock temp = this.realFalseBlock != null ? realFalseBlock : endBlock;
        new BrInst(b, b.reVar, realTrueBlock1, temp);
        //以下，还需要填充realTrueBlock 和 realFalseBlock
//        new BrInst(realTrueBlock, endBlock);
//        if (realFalseBlock != null) {
//            new BrInst(realFalseBlock, endBlock);
//        }
    }
}
