package middleend.value;

import middleend.Value;

public class ConstValue extends Value {
    private String num;

    public ConstValue(String num) {
        this.num = num;
    }

    public String getName() {
        return this.num;
    }
}
