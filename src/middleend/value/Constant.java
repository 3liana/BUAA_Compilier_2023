package middleend.value;

import middleend.Value;

public class Constant extends Value {
    private int num;
    private String name;

    public Constant(String name, int num) {
        this.name = name;
        this.num = num;
    }
    public String getName() {
        return name;
    }
}
