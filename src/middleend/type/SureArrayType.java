package middleend.type;

public class SureArrayType implements Type{
    // [n x i32]
    //[n x [m x i32]]
    public int n;
    public int m;
    public int type;
    public SureArrayType(int n){
        this.n = n;
        this.type = 0;
    }
    public SureArrayType(int n, int m){
        this.n = n;
        this.m = m;
        this.type = 1;
    }
    public void setM(int m){
        this.m = m;
        this.type = 1;
    }
    public String toString(){
        if (this.type == 0){
            return "[" + n +" x i32]";
        } else {
            return "[" + n + " x [" + m + "x i32]]";
        }
    }
}
