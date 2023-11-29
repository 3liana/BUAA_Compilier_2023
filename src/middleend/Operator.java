package middleend;

public enum Operator {
    add,
    sub,
    mul,
    sdiv,
    srem;//mod
    public String toMips(){
        switch (this){
            case add:
                return "add";
            case sub:
                return "sub";
            case mul:
                return "mult";
            case sdiv:
                return "div";
            case srem:
                return "mod";
            default:
                return "";
        }
    }
}
