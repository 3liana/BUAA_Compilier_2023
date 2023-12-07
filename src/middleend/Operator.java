package middleend;

public enum Operator {
    add,
    sub,
    mul,
    sdiv,
    srem;//mod

    public String toMips() {
        switch (this) {
            case add:
                return "addu";
            case sub:
                return "subu";
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

    public static int cal(int a, int b, Operator op) {
        int ans = 0;
        switch (op) {
            case add:
                ans = a + b;
                break;
            case sub:
                ans = a - b;
                break;
            case mul:
                ans = a * b;
                break;
            case sdiv:
                ans = a / b;
                break;
            case srem:
                ans = a % b;
                break;
        }
        return ans;
    }
}
