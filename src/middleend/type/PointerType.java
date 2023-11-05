package middleend.type;

public class PointerType implements Type{
    private Type targetType;
    public PointerType(Type type){
        this.targetType = type;
    }
}
