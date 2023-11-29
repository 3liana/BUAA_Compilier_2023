package middleend.value;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.Type;

public class ConstValue extends Value {
    public String num;

    public ConstValue(String num) {
        this.myType = IntegerType.i32Type;
        this.num = num;
    }

    public String getName() {
        return this.num;
    }
    public int getNum(){
        return Integer.parseInt(this.num);
    }
}
