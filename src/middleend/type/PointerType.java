package middleend.type;

public class PointerType implements Type{
    public Type targetType;
    public PointerType(Type targetType){
        this.targetType = targetType;
    }
    public String toString(){
        return this.targetType + "*";
    }
}
