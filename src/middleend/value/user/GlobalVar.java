package middleend.value.user;

import middleend.IRModule;
import middleend.Value;
import middleend.value.Constant;
import middleend.value.User;

public class GlobalVar extends User {
    private boolean isConst;
    private Value value;
    private String name;
    public GlobalVar(Value value) {
        this.name = value.getName();
        this.isConst = value instanceof Constant;
        this.value = value;
        IRModule.getModuleInstance().addGlobalVar(this);
    }
}
