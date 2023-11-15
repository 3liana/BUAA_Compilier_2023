package middleend.type;

public class IntegerType implements Type{
    public static IntegerType i32Type = new IntegerType(32);
    public static IntegerType i1Type = new IntegerType(1);
    private int bit; //位宽
    public IntegerType(int bit){
        this.bit = bit;
    }
    public String toString(){
        return "i" + this.bit;
    }
}
