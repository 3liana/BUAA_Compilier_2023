package middleend.type;

public class ArrayType implements Type{
    // [n x i32]
    private int n;
    public ArrayType(int n){
        this.n = n;
    }
    public String toString() {
        return "[" + n + " x i32]";
    }
}
