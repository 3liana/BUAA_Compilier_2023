package middleend.value.user;

import frontend.paser_package.FuncFParam;
import middleend.IRModule;
import middleend.Value;
import middleend.symbol.SymbolTable;
import middleend.type.Type;
import middleend.type.VoidType;
import middleend.value.ParamVarValue;
import middleend.value.User;

import java.util.ArrayList;

public class Function extends User {
    public ArrayList<BasicBlock> basicBlocks;
    public Type returnType;//voidType IntegerType
    public String name;
    public int registerNum = 0;//function本身的registerNum为0
    private String blank = " ";
    private ArrayList<Value> params = new ArrayList<>();

    public Function(String name, Type returnType) {
        this.name = name;
        this.returnType = returnType;
        this.basicBlocks = new ArrayList<>();
        this.addFirstBasicBlock();//第一个basicBlock 即函数入口 是一个registerNum为0的BasicBlock
        boolean isMain = name.equals("main");
        if (isMain) {
            IRModule.getModuleInstance().setMainFunction(this);
        } else {
            IRModule.getModuleInstance().addFunction(this);
        }
    }

    public Function(String name, Type returnType,
                    ArrayList<FuncFParam> params, SymbolTable symbolTable) {
        //先给param分配，然后给第一个basicBlock分配，再给指令之类的分配
        this.name = name;
        this.returnType = returnType;
        this.basicBlocks = new ArrayList<>();
        //
        for (FuncFParam f : params) {
            int registerNum = this.assignRegister();
            ParamVarValue value = new ParamVarValue(registerNum, f.ident.getName());
            symbolTable.addValue(value);//加入符号表
            this.params.add(value);
        }
        this.addFirstBasicBlock();
        //
        boolean isMain = name.equals("main");
        if (isMain) {
            IRModule.getModuleInstance().setMainFunction(this);
        } else {
            IRModule.getModuleInstance().addFunction(this);
        }
    }

    public String getTableName() {
        return this.name;
    }
    public String getName(){
        return "@" + this.name;
    }
    public void addFirstBasicBlock() {
        BasicBlock b = new BasicBlock(this.assignRegister(),true);
        this.basicBlocks.add(b);
        b.belongFunction = this;
    }
    public void addBasicBlock(){
        BasicBlock b = new BasicBlock(this.assignRegister(),false);
        this.basicBlocks.add(b);
        b.belongFunction = this;
    }
    public BasicBlock getCurBasicBlock() {
        return this.basicBlocks.get(this.basicBlocks.size() - 1);
    }

    public int assignRegister() {
        return this.registerNum++;
    }

    public String getBasicBlocksPrint() {
        StringBuilder sb = new StringBuilder();
        for (BasicBlock block : basicBlocks) {
            sb.append(block.getPrint());
        }
        return sb.toString();
    }

    public String getPrint() {
        StringBuilder sParam = new StringBuilder();
        int i;
        for (i = 0; i < this.params.size() - 1; i++) {
            Value v = this.params.get(i);
            sParam.append(v.getMyType() + " " + v.getName());
            sParam.append(" , ");
        }
        if(this.params.size() - 1 >= 0){
            Value v = this.params.get(this.params.size() - 1);
            sParam.append(v.getMyType() + " " +v.getName());
        }
        String s0 = "define" + blank + this.returnType + blank
                + "@" + this.name + "(" + sParam +
                ")" + blank + "{\n";
        String s1 = this.getBasicBlocksPrint();
        if (returnType instanceof VoidType && !s1.endsWith("ret void\n")) {
            s1 = s1 + "ret void\n";
        }
        String s2 = "}\n";
        return s0 + s1 + s2;
    }
}
