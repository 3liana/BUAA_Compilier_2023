package middleend.type;

public class InstType implements Type{
    public static InstType instType = new InstType();
    public InstType() {
    }
    public String toString(){
        return "Inst";
    }
}
