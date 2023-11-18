package middleend.value;

import middleend.IRModule;
import middleend.type.PointerType;
import middleend.type.SureArrayType;
import middleend.type.IntegerType;
import middleend.type.Type;

import java.util.ArrayList;

public class GlobalVar extends User {
    private boolean isConst;
    private String name;//直接用全局变量的名字命名
//    private int num;
    String blank = " ";
    private int type;//0 0维
    private Type targetType;
    private ArrayList<ArrayList<Integer>> initValNums;

    public GlobalVar(String name, boolean isConst, int num) {
        this.name = name;
        this.isConst = isConst;
        this.num = num;
        IRModule.getModuleInstance().addGlobalVar(this);
        this.setMyType(new PointerType(IntegerType.i32Type));
        this.targetType = IntegerType.i32Type;
        this.type = 0;
        //
    }
    public GlobalVar(String name, boolean isConst, ArrayList<ArrayList<Integer>> initValNums,Type type){
        this.name = name;
        this.isConst = isConst;
        this.initValNums = initValNums;
        IRModule.getModuleInstance().addGlobalVar(this);
        this.setMyType(new PointerType(type));
        this.targetType = type;
        this.type = 1;
    }
    public String getName() {
        // 有 getName 的 value 都是会被存进符号表的
        //eg: global_a
        return "@" + this.name;
    }

    public String getTableName() {
        return this.name;
    }

    public String getPrint() {
        String s0 = isConst ? "constant" : "global";
        if(this.type == 0){
            return this.getName() + blank + "=" + blank + s0 + blank + this.targetType + blank + this.num + "\n";
        } else {
            StringBuilder sb = new StringBuilder();
            //数组  myType必是GlobalArrayType
            SureArrayType targetType = (SureArrayType) this.targetType;
            if(targetType.type == 0){
                //一维数组
                ArrayList<Integer> level= this.initValNums.get(0);
                this.appendLevel(sb,level);
            } else {
                //二维数组
                sb.append("[");
                SureArrayType insideType = new SureArrayType(targetType.m);

                for(int i = 0;i<this.initValNums.size()-1;i++){
                    ArrayList<Integer> level= this.initValNums.get(i);
                    sb.append(insideType + " ");
                    this.appendLevel(sb,level);
                    sb.append(",");
                }
                if(this.initValNums.size()>=1){
                    ArrayList<Integer> level= this.initValNums.get(
                            this.initValNums.size() - 1
                    );
                    sb.append(insideType + " ");
                    this.appendLevel(sb,level);
                }
                sb.append("]");
            }
            return this.getName() + blank + "=" + blank + s0 + blank + this.targetType + blank +
                    sb + "\n";
        }
    }
    private void appendLevel(StringBuilder sb, ArrayList<Integer> level){
        //todo 如果全0则增加 zeroinitializer
        sb.append("[");
        for(int i=0;i<level.size()-1;i++){
            sb.append("i32 " + level.get(i));
            sb.append(",");
        }
        if(level.size() >= 1){
            sb.append("i32 " + level.get(
                    level.size()-1)
            );
        }
        sb.append("]");
    }
}
