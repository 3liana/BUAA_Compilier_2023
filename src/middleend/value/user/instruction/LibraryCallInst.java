package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.value.ConstValue;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class LibraryCallInst extends Instruction {
    public int type;
    //1 read
    //2 putch
    //3 putint
    public Value value;

    public LibraryCallInst(BasicBlock block, int type, Value value) {
        super(block);
        this.type = type;
        this.value = value;
        this.value.setMyType(IntegerType.i32Type);
    }

    public String getPrint() {
        if (this.type == 1) {
            return value.getName() + " = call i32 @getint()\n";
        } else if (this.type == 2) {
            return "call void @putch(i32 " + value.getName() + ")\n";
        } else {
            return "call void @putint(i32 " + value.getName() + ")\n";
        }
    }

    public void replaceValueWithConst(Value oldValue, ConstValue newConst) {
        if (this.value != null && this.value.equals(oldValue)) {
            this.value = newConst;
        }
    }
}
