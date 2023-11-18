package middleend;

import middleend.value.GlobalVar;
import middleend.value.user.Function;

import java.util.ArrayList;

public class IRModule {
    //单例模式
    private static final IRModule module = new IRModule();
    private ArrayList<GlobalVar> globalVars;
    private ArrayList<Function> functions;
    private Function mainFunction;
    private IRModule(){
        this.globalVars = new ArrayList<>();
        this.functions = new ArrayList<>();
    }
    public static IRModule getModuleInstance(){
        return IRModule.module;
    }
    public void addGlobalVar(GlobalVar var){
        this.globalVars.add(var);
    }
    public void addFunction(Function function){
        this.functions.add(function);
    }
    public void setMainFunction(Function function){
        this.mainFunction = function;
    }
    public String getPrint(){
        for(Function f:this.functions){
            //不得已之举，要在最后发生
            f.fillBlock();
        }
        this.mainFunction.fillBlock();
        String library = "declare i32 @getint()\n" +
                "declare void @putint(i32)\n" +
                "declare void @putch(i32)\n" +
                "declare void @putstr(i8*)\n";
        StringBuilder sb = new StringBuilder();
        for (GlobalVar var : globalVars) {
            sb.append(var.getPrint());
        }
        for(Function function:functions){
            sb.append(function.getPrint());
        }
        sb.append(mainFunction.getPrint());
        return library + sb;
    }
}
