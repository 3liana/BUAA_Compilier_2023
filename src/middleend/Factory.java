package middleend;

import middleend.value.Constant;
import middleend.value.user.GlobalVar;

public class Factory {
    //负责辅助Generator 提供一些功能
    private TableList tableList;

    public Factory(TableList tableList) {
        this.tableList = tableList;
    }

    public Constant createConstant(String name, int num) {
        return new Constant(name,num);
    }
    //build 针对llvm结构
    public GlobalVar buildGlobalVar(Value value){
        return new GlobalVar(value);
    }
}
