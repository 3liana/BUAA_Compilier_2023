package middleend;

import middleend.value.user.GlobalVar;

import java.util.ArrayList;

public class IRModule {
    //单例模式
    private static final IRModule module = new IRModule();
    private ArrayList<GlobalVar> globalVars;
    private IRModule(){
        this.globalVars = new ArrayList<>();
    }
    public static IRModule getModuleInstance(){
        return IRModule.module;
    }
    public void addGlobalVar(GlobalVar var){
        this.globalVars.add(var);
    }
}
