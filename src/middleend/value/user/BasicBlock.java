package middleend.value.user;

import middleend.Value;
import middleend.value.user.Instruction;

import java.util.ArrayList;

public class BasicBlock extends Value {
    public boolean isFirst;//是否是所属函数的第一个basicBlock
    public int registerNum;
    public ArrayList<Instruction> instructions;
    public Value reVar = null;//为回填设计
    public Instruction terInst = null;
    public boolean hasTerInst = false;
    public Function belongFunction;
    public boolean delete = false;
    public BasicBlock() {
        //为callInst里的tempBlock而设置
    }

    public BasicBlock(int num, boolean isFirst) {
        this.isFirst = isFirst;
        this.registerNum = num;
        this.instructions = new ArrayList<>();
    }

    public void addInst(Instruction inst) {
        if (Instruction.isTerInst(inst)) {
            //对终结指令
            if (!this.hasTerInst) {
                //只有本块中无终结指令才可以加入
                this.instructions.add(inst);
                this.hasTerInst = true;
                this.terInst = inst;
            }
        } else {
            //对非终结指令
//            if(this.hasTerInst){
//                int index = this.instructions.indexOf(this.terInst);
//                this.instructions.remove(index);
//                this.instructions.add(index,inst);
//                this.instructions.add(this.terInst);
//            } else {
//                this.instructions.add(inst);
//            }
            if (!this.hasTerInst) {
                this.instructions.add(inst);
            }
        }

    }

    public String getName() {
        return "%" + "_" + this.registerNum;
    }

    public String getPrint() {
        if(delete){//优化
            return "";
        }

        //注意 在function定义中第一个basicBlock的号码不一定是0
        StringBuilder sb = new StringBuilder();
        if (!isFirst) {
            sb.append("_" + this.registerNum + ":\n");
        }
        for (Instruction inst : this.instructions) {
            sb.append(inst.getPrint());
        }
        return sb.toString();
    }

}
