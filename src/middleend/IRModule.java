package middleend;

public class IRModule {
    //单例模式
    private static final IRModule module = new IRModule();
    private IRModule(){

    }
    public static IRModule getModule(){
        return IRModule.module;
    }
}
