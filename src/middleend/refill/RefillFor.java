package middleend.refill;

import middleend.value.user.BasicBlock;
import middleend.value.user.instruction.terminateInst.BrInst;

import java.util.ArrayList;

public class RefillFor extends RefillUtil {
    //stmtBlock 即 realTrueBlock
    //realFalseBlock = null;
    public BasicBlock forStmt2Block = null;
    public ArrayList<BrInst> dstAsForStmt2 = new ArrayList<>();//continue
    public ArrayList<BrInst> dstAsEnd = new ArrayList<>();//break
    public RefillFor(boolean hasCond){
        this.hasCond = hasCond;
    }
    //目前有点问题在于Generator里肯定会生成一个forStmt2Block问题不过是
    //这个Block里没有指令
    public void refill(){
        //        回填break和continue
//        for(BasicBlock b:this.continues){
//            new BrInst(b,forStmt2Block);
//        }
//        for(BasicBlock b : this.breaks){
//            new BrInst(b,endBlock);
//        }
        for(BrInst br:dstAsForStmt2){
            br.dest = forStmt2Block;
        }
        for(BrInst br:dstAsEnd){
            br.dest = endBlock;
        }

        super.refill();//cond
        //如果存在forStmt2Block就跳进去
        //如果不存在就直接跳到cond
        if(this.forStmt2Block != null){
            new BrInst(realTrueBlock2,forStmt2Block);
        } else {
            if(this.hasCond){
                new BrInst(realTrueBlock2,refillBasicBlocks.get(1).get(0));
            } else {
                new BrInst(realTrueBlock2,realTrueBlock1);
            }
            return;//也不用考虑后面forStmt2Block怎么跳了
        }
        //如果cond存在跳IfBlock
        //如果cond不存在直接跳realTrueBlock
        if(this.hasCond){
            new BrInst(forStmt2Block,refillBasicBlocks.get(1).get(0));
        } else {
            new BrInst(forStmt2Block,realTrueBlock1);
        }

    }
}
