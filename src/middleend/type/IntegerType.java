package middleend.type;

public class IntegerType implements Type{
    private int bit; //位宽
    public IntegerType(int bit){
        this.bit = bit;
    }
    public String toString(){
        return "i" + this.bit;
    }
}
