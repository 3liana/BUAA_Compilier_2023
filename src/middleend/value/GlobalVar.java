package middleend.value;

import middleend.IRModule;
import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.Type;
import middleend.value.VarValue;
import middleend.value.User;

public class GlobalVar extends User {
    private boolean isConst;
    private String name;//直接用全局变量的名字命名
    private Type type = new IntegerType(32);
    private int num;
    String blank = " ";

    public GlobalVar(String name, boolean isConst, int num) {
        this.name = name;
        this.isConst = isConst;
        this.num = num;
        IRModule.getModuleInstance().addGlobalVar(this);
    }

    public String getName() {
        // 有 getName 的 value 都是会被存进符号表的
        //eg: global_a
        return "@" + this.name;
    }

    public String getTableName() {
        return this.name;
    }

    public int getNum() {
        return this.num;
    }

    public String getPrint() {
        String s0 = isConst ? "constant" : "global";
        return this.getName() + blank + "=" + blank + s0 + blank + this.type + blank + this.num + "\n";
    }
}
