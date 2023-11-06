package middleend;

import frontend.paser_package.*;

public class Generator {
    private CompUnit compUnit;//语法分析生成的AST树
    private TableList tableList;
    private Factory factory;
    private IRModule irModule;

    // 保留的数
    private boolean isGlobal = true;
    private int saveInt = 0;
    private Value tempValue = null;

    public Generator(CompUnit compUnit) {
        this.compUnit = compUnit;
        this.irModule = IRModule.getModuleInstance();
        this.tableList = new TableList();
        this.factory = new Factory(tableList);
    }

    public SymbolTable getCurTable() {
        return this.tableList.getCurSymbolTable();
    }

    //开始visit AST
    public void visitCompUnit() {
        this.tableList.addTable();
        //todo 增加库函数
        for (Decl decl : this.compUnit.decls) {
            visitDecl(decl);
        }
        for (FuncDef funcDef : this.compUnit.defs) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(this.compUnit.mainDef);
    }

    public void visitDecl(Decl decl) {
        if (decl.type == 0) {
            visitConstDecl(decl.constDecl);
        } else {
            visitVarDecl(decl.varDecl);
        }
    }

    public void visitConstDecl(ConstDecl decl) {
        // const int {constDef}
        for (ConstDef constDef : decl.defs) {
            visitConstDef(constDef);
        }
    }

    public void visitConstDef(ConstDef constDef) {
        // ConstDef -> Ident { '[' ConstExp ']' } '=' ConstInitVal
        String name = constDef.getName();
        if (constDef.exps.size() == 0) {
            //0维
            visitConstInitVal(constDef.constInitVal); //访问初始化InitVal 获得Const的值
            tempValue = factory.createConstant(name, saveInt); //创立 Value 的子类 Constant实例
            getCurTable().addValue(tempValue);//加入符号表
            if (this.isGlobal){
                //全局变量
                tempValue = factory.buildGlobalVar(tempValue);
            } else {
                //非全局变量
            }
        } else {
            // todo 数组
        }
    }

    public void visitConstInitVal(ConstInitVal constInitVal) {
    }

    public void visitVarDecl(VarDecl decl) {
    }

    public void visitFuncDef(FuncDef funcDef) {
    }

    public void visitMainFuncDef(MainFuncDef mainFuncDef) {
        this.isGlobal = false;
    }
}
