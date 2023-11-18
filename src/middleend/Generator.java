package middleend;

import frontend.lexer_package.Category;
import frontend.paser_package.*;
import frontend.paser_package.stmt_package.*;
import middleend.refill.RefillFor;
import middleend.refill.RefillIf;
import middleend.refill.RefillUtil;
import middleend.symbol.*;
import middleend.type.*;
import middleend.value.*;
import middleend.value.user.*;
import middleend.value.user.instruction.*;
import middleend.value.user.instruction.terminateInst.BrInst;
import middleend.value.user.instruction.terminateInst.RetInst;


import java.util.ArrayList;

public class Generator {
    public static Generator generator;
    private CompUnit compUnit;//语法分析生成的AST树
    public TableList tableList;
    private Function curFunction = null;//通过curFunction来获取虚拟寄存器的值
    public Factory factory;
    private IRModule irModule;

    // 保留的数
    private boolean isGlobal = true;

    public Generator(CompUnit compUnit) {
        this.compUnit = compUnit;
        this.irModule = IRModule.getModuleInstance();
        this.tableList = new TableList();
        this.factory = new Factory(tableList);
        Generator.generator = this;//可以通过类访问这个实例
    }

    public SymbolTable getCurTable() {
        return this.tableList.getCurSymbolTable();
    }

    //开始visit AST
    public void visitCompUnit() {
        this.tableList.addTable();//0层
        this.initLibraryFunc();//初始化库函数
        for (Decl decl : this.compUnit.decls) {
            visitDecl(decl);
        }
        for (FuncDef funcDef : this.compUnit.defs) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(this.compUnit.mainDef);
    }

    public void initLibraryFunc() {
        //在库里注册函数 getint和printf
        this.tableList.getCurSymbolTable().addValue(new LibraryFunction("getint"));
        this.tableList.getCurSymbolTable().addValue(new LibraryFunction("printf"));
    }

    public void visitFuncDef(FuncDef funcDef) {
        //建立func类型的value
        this.isGlobal = false;
        Type returnType = funcDef.funcType.token.getCategory() == Category.VOIDTK ?
                new VoidType() : new IntegerType(32);
        Function function;
        SymbolTable newSymbolTable = new SymbolTable();
        if (funcDef.type == 0) {
            //无形参
            function = new Function(funcDef.ident.getName(), returnType);
        } else {
            //有形参
            FuncFParams fParams = funcDef.params;
            ArrayList<FuncFParam> params = fParams.params;
            function = new Function(funcDef.ident.getName(), returnType,
                    params, newSymbolTable);
            //在function初始化里面完成对形参的表示，且加入符号表
        }
        //将函数名放入0层符号表，再新建一层符号表
        getCurTable().addValue(function);
        //新建符号表 如果有形参 那么这个符号表已经存过形参的信息了
        this.tableList.addATable(newSymbolTable);
        //切换curFunction
        this.curFunction = function;
        this.visitBlock(funcDef.block);
        //函数编译完成，pop出这一层的符号表
        this.tableList.popTable();
    }

    public void visitMainFuncDef(MainFuncDef mainFuncDef) {
        this.tableList.addTable();
        this.isGlobal = false;
        Function main = new Function("main", new IntegerType(32));
        this.curFunction = main;
        this.visitBlock(mainFuncDef.block);
        this.tableList.popTable();
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
            //visitConstInitVal(constDef.constInitVal); //访问初始化InitVal 获得Const的值
            //将常量的初值存在saveInt里
            if (this.isGlobal) {
                //全局变量
                //对于全局变量 初始值是确定的 可以在factory里面算出具体值
                int tempNum = factory.calConstExp(constDef.constInitVal.exp);
                Value tempValue = new GlobalVar(name, true, tempNum);
                getCurTable().addValue(tempValue);//加入符号表
            } else {
                BasicBlock basicBlock = curFunction.getCurBasicBlock();
                int registerNum = curFunction.assignRegister();
                VarValue var = new VarValue(registerNum, true, name);
                new AllocaInst(basicBlock, var);
                getCurTable().addValue(var);
                //下面是计算初始值的地方
                Value value = this.visitConstInitVal(constDef.constInitVal);
                new StoreInst(basicBlock, value, var);
            }
        } else {
            //确定数组的维度 即type为tempType
            //由于文法限制 Decl的时候维度必须是ConstExp，即可以算出的
            ConstExp exp1 = constDef.exps.get(0);
            int exp1Num = factory.calConstExp(exp1);
            Type tempType = new SureArrayType(exp1Num);
            if (constDef.exps.size() > 1) {
                ConstExp exp2 = constDef.exps.get(1);
                int exp2Num = factory.calConstExp(exp2);
                ((SureArrayType) tempType).setM(exp2Num);
            }
            if (this.isGlobal) {
                //全局变量
                ConstInitVal initVal = constDef.constInitVal;
                ArrayList<ArrayList<Integer>> initValNums = factory.calArrayConstInitVal(initVal);
                //加入符号表
                getCurTable().addValue(new GlobalVar(name, true, initValNums, tempType));
                //new GlobalVar(name, true, initValNums, tempType);
            } else {
                //局部变量
                BasicBlock basicBlock = curFunction.getCurBasicBlock();
                int registerNum = curFunction.assignRegister();
                VarValue var = new VarValue(registerNum, true, name);
                new AllocaInst(basicBlock, var, tempType);
                //加入符号表
                getCurTable().addValue(var);
                //下面是计算初始值的地方
                //1.计算出地址
                //2.store
                if (((SureArrayType) tempType).type == 0) {//一维数组
                    this.visitArrayConstInitVal1(constDef.constInitVal, var, (SureArrayType) tempType);
                } else {//二维数组
                    this.visitArrayConstInitVal2(constDef.constInitVal, var, (SureArrayType) tempType);
                }
            }
        }
    }

    public void visitArrayConstInitVal1(ConstInitVal ci, Value toValue, SureArrayType type) {
        int n = type.n;
        for (int i = 0; i < n; i++) {
            //填充第i
            ConstInitVal tempCi = ci.initVals.get(i);
            ConstExp tempExp = tempCi.exp;
            Value v = this.visitAddExp(tempExp.addExp);
            new StoreInst(curFunction.getCurBasicBlock(),
                    v, toValue, i);
        }
    }

    public void visitArrayConstInitVal2(ConstInitVal ci, Value toValue, SureArrayType type) {
        int n = type.n;
        int m = type.m;
        for (int i = 0; i < n; i++) {
            //填充第i
            ConstInitVal tempCi = ci.initVals.get(i);
            for (int j = 0; j < m; j++) {
                //填充第i，j
                ConstExp tempExp = tempCi.initVals.get(j).exp;
                Value v = this.visitAddExp(tempExp.addExp);
                new StoreInst(curFunction.getCurBasicBlock(),
                        v, toValue, i, j);
            }
        }
    }

    public Value visitConstInitVal(ConstInitVal constInitVal) {
        //0维度
        return this.visitAddExp(constInitVal.exp.addExp);
    }

    public void visitVarDecl(VarDecl decl) {
        // VarDecl → BType VarDef { ',' VarDef } ';'
        for (VarDef item : decl.defs) {
            this.visitVarDef(item);
        }
    }

    public void visitVarDef(VarDef varDef) {
        String name = varDef.getName();
        if (!varDef.isArray) {
            if (this.isGlobal) {
                //全局变量 在factory中计算
                int tempNum = 0;
                if (varDef.type == 1) {
                    tempNum = factory.calAddExp(varDef.initVal.exp.addExp);
                }
                //以上 确定变量名和初始值
                Value tempValue = new GlobalVar(name, false, tempNum);
                getCurTable().addValue(tempValue);
            } else {
                BasicBlock basicBlock = curFunction.getCurBasicBlock();
                int registerNum = curFunction.assignRegister();
                VarValue var = new VarValue(registerNum, false, name);
                getCurTable().addValue(var);
                new AllocaInst(basicBlock, var);
                if (varDef.type == 1) {
                    Value value = this.visitInitVal(varDef.initVal);
                    new StoreInst(basicBlock, value, var);
                }
            }
        } else {
            //vardef定义的是数组
            //获得数组Type
            ConstExp exp1 = varDef.exps.get(0);
            int exp1Num = factory.calConstExp(exp1);
            Type tempType = new SureArrayType(exp1Num);
            if (varDef.exps.size() > 1) {
                ConstExp exp2 = varDef.exps.get(1);
                int exp2Num = factory.calConstExp(exp2);
                ((SureArrayType) tempType).setM(exp2Num);
            }
            if (this.isGlobal) {
                ArrayList<ArrayList<Integer>> initValNums;
                if (varDef.initVal != null) {
                    InitVal initVal = varDef.initVal;
                    initValNums = factory.calArrayInitVal(initVal);
                } else {
                    if (((SureArrayType) tempType).type == 0) {
                        ArrayList<Integer> zeroLevel = new ArrayList<Integer>();
                        for (int i = 0; i < ((SureArrayType) tempType).n; i++) {
                            zeroLevel.add(0);
                        }
                        initValNums = new ArrayList<>();
                        initValNums.add(zeroLevel);
                    } else {
                        int n = ((SureArrayType) tempType).n;
                        int m = ((SureArrayType) tempType).m;
                        initValNums = new ArrayList<>();
                        for (int i = 0; i < n; i++) {
                            ArrayList<Integer> level = new ArrayList<>();
                            for (int j = 0; j < m; j++) {
                                level.add(0);
                            }
                            initValNums.add(level);
                        }
                    }
                }
                getCurTable().addValue(
                        new GlobalVar(name, false, initValNums, tempType)
                );
//                new GlobalVar(name, false, initValNums, tempType);
            } else {
                BasicBlock basicBlock = curFunction.getCurBasicBlock();
                int registerNum = curFunction.assignRegister();
                VarValue var = new VarValue(registerNum, false, name);
                new AllocaInst(basicBlock, var, tempType);
                getCurTable().addValue(var);
                //下面是计算初始值的地方
                //1.计算出地址
                //2.store
                if(varDef.initVal != null){
                    if (((SureArrayType) tempType).type == 0) {//一维数组
                        this.visitArrayInitVal1(varDef.initVal, var, (SureArrayType) tempType);
                    } else {//二维数组
                        this.visitArrayInitVal2(varDef.initVal, var, (SureArrayType) tempType);
                    }
                }
            }
        }

    }

    public void visitArrayInitVal1(InitVal ci, Value toValue, SureArrayType type) {
        int n = type.n;
        for (int i = 0; i < n; i++) {
            //填充第i
            InitVal tempCi = ci.initVals.get(i);
            Exp tempExp = tempCi.exp;
            Value v = this.visitAddExp(tempExp.addExp);
            new StoreInst(curFunction.getCurBasicBlock(),
                    v, toValue, i);
        }
    }

    public void visitArrayInitVal2(InitVal ci, Value toValue, SureArrayType type) {
        int n = type.n;
        int m = type.m;
        for (int i = 0; i < n; i++) {
            //填充第i
            InitVal tempCi = ci.initVals.get(i);
            for (int j = 0; j < m; j++) {
                //填充第i，j
                Exp tempExp = tempCi.initVals.get(j).exp;
                Value v = this.visitAddExp(tempExp.addExp);
                new StoreInst(curFunction.getCurBasicBlock(),
                        v, toValue, i, j);
            }
        }
    }

    public Value visitInitVal(InitVal initVal) {
        //0维
        return this.visitAddExp(initVal.exp.addExp);
    }


    public void visitBlock(Block block) {
        //
        for (BlockItem item : block.blockItems) {
            this.visitBlockItem(item);
        }
    }

    public void visitBlockItem(BlockItem blockItem) {
        if (blockItem.type == 0) {
            this.visitDecl(blockItem.decl);
        } else {
            this.visitStmt(blockItem.stmt);
        }
    }

    public void visitStmt(Stmt stmt) {
        if (stmt instanceof StmtReturn) {
            this.visitStmtReturn((StmtReturn) stmt);
        }
        if (stmt instanceof StmtBC) {
            this.visitStmtBC((StmtBC) stmt);
        }
        if (stmt instanceof StmtBlock) {
            this.visitStmtBlock((StmtBlock) stmt);
        }
        if (stmt instanceof StmtExp) {
            this.visitStmtExp((StmtExp) stmt);
        }
        if (stmt instanceof StmtFor) {
            this.visitStmtFor((StmtFor) stmt);
        }
        if (stmt instanceof StmtIf) {
            this.visitStmtIf((StmtIf) stmt);
        }
        if (stmt instanceof StmtLValExp) {
            this.visitStmtLValExp((StmtLValExp) stmt);
        }
        if (stmt instanceof StmtPrint) {
            this.visitStmtPrint((StmtPrint) stmt);
        }
        if (stmt instanceof StmtRead) {
            this.visitStmtRead((StmtRead) stmt);
        }
    }


    public Value visitAddExp(AddExp exp) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        Value pre = this.visitMulExp(exp.mulExps.get(0));
        ArrayList<Value> values = new ArrayList<>();
        if (exp.mulExps.size() != 1) {
            for (int i = 1; i < exp.mulExps.size(); i++) {
                values.add(this.visitMulExp(exp.mulExps.get(i)));
            }
        }
        //先统一visit了mulExp,然后再加起来
        for (int i = 0; i < values.size(); i++) {
            Value temp = values.get(i);
            int symbol = exp.symbols.get(i + 1);
            //
            int registerNum = curFunction.assignRegister();
            Value result = new VarValue(registerNum, false);
            if (symbol == 1) {
                //1表示加 -1表示-
                new BinaryInst(curBasicBlock, result, pre, temp, Operator.add);
            } else {
                new BinaryInst(curBasicBlock, result, pre, temp, Operator.sub);
            }
            pre = result;
        }
        return pre;
    }

    public Value visitMulExp(MulExp mulExp) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        Value pre = this.visitUnaryExp(mulExp.unaryExps.get(0));
        ArrayList<Value> values = new ArrayList<>();
        if (mulExp.unaryExps.size() != 1) {
            for (int i = 1; i < mulExp.unaryExps.size(); i++) {
                values.add(this.visitUnaryExp(mulExp.unaryExps.get(i)));
            }
        }
        for (int i = 0; i < values.size(); i++) {
            Value temp = values.get(i);
            int symbol = mulExp.symbols.get(i + 1);
            //
            int registerNum = curFunction.assignRegister();
            Value result = new VarValue(registerNum, false);
            if (symbol == 1) {
                new BinaryInst(curBasicBlock, result, pre, temp, Operator.mul);
            } else if (symbol == 2) {
                new BinaryInst(curBasicBlock, result, pre, temp, Operator.sdiv);
            } else {
                new BinaryInst(curBasicBlock, result, pre, temp, Operator.srem);
            }
            pre = result;
        }
        return pre;
    }

    public Value visitUnaryExp(UnaryExp exp) {
        BasicBlock curBlock = curFunction.getCurBasicBlock();
        if (exp.type == 0) {
            //PrimaryExp
            return this.visitPrimaryExp(exp.primaryExp);
        } else if (exp.type == 1) {
            //Ident '(' [FuncRParams] ')'
            String functionName = exp.ident.getName();
            FuncRParams funcRParams = exp.funcRParams;
            Value v = this.tableList.foundFuncDef(functionName);
            if (v instanceof Function) {
                //非库函数
                Function f = (Function) v;
                ArrayList<Value> rParamValue = new ArrayList<>();
                //visit每个实参 获得实参的寄存器
                if (funcRParams != null) {
                    for (Exp tempExp : funcRParams.exps) {
                        rParamValue.add(this.visitAddExp(tempExp.addExp));
                    }
                }
                if (f.returnType instanceof IntegerType) {
                    //有返回值函数
                    //xxx = call()的形式
                    int registerNum = curFunction.assignRegister();
                    VarValue var = new VarValue(registerNum, false);
                    new CallInst(curBlock, f, rParamValue, var);
                    return var;
                } else {
                    //无返回值函数
                    //call()
                    return new CallInst(curBlock, f, rParamValue);
                }
            } else {
                //库函数
                //1 getint() callInst
                if (functionName.equals("getint")) {
                    int registerNum = curFunction.assignRegister();
                    VarValue var = new VarValue(registerNum, false);
                    new LibraryCallInst(curBlock, 1, var);
                    return var;
                }
                //2 printf() callInst
                //不太符合定义 因为funcFParams里是一堆exp 而 printf需要formatString
                return new Value();
            }
        } else {
            //UnaryOp UnaryExp
            // + - 是与常数0做运算
            ConstValue zero = new ConstValue("0");
            Value v = this.visitUnaryExp(exp.unaryExp);
            if (exp.unaryOp.op.getCategory() == Category.PLUS) {
                int registerNum = curFunction.assignRegister();
                VarValue var = new VarValue(registerNum, false);
                new BinaryInst(curBlock, var, zero, v, Operator.add);
                return var;
            } else if (exp.unaryOp.op.getCategory() == Category.MINU) {
                int registerNum = curFunction.assignRegister();
                VarValue var = new VarValue(registerNum, false);
                new BinaryInst(curBlock, var, zero, v, Operator.sub);
                return var;
            } else {
                //!NOT
                int registerNum = curFunction.assignRegister();
                VarValue var = new VarValue(registerNum, false, true);
                new IcmpInst(curBlock, var, v, zero, CondString.eq);
                VarValue var2 = this.assignTempVarValue();
                new ZextInst(curBlock, var2, var);
                return var2;
            }
        }
    }

    public Value visitPrimaryExp(PrimaryExp exp) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        if (exp.type == 0) {
            //exp
            return this.visitAddExp(exp.exp.addExp);
        } else if (exp.type == 2) {
            //number
            return new ConstValue(exp.number.token.getName());
        } else {
            //type = 1 LVal
            //如果是形参不可以load 要 alloc + store + load
            return this.visitLVal(exp.lVal);
        }
        // return new Value();
    }

    public Value visitLVal(LVal lVal) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        String name = lVal.ident.getName();
        Value defedValue = this.tableList.foundDef(name);
        Type fromType = defedValue.getMyType();
        Type targetType;
        targetType = ((PointerType) fromType).targetType;
        if (targetType instanceof IntegerType) {
            //指向零维变量 如a
            //a是Integer
            int registerNum = curFunction.assignRegister();
            Value result = new VarValue(registerNum, false);
            new LoadInst(curBasicBlock, result, defedValue);
            return result;
        } else if (targetType instanceof SureArrayType) {
            //lVal是变量数组
            int len = lVal.exps.size();
            if (len == 0) {
                //要LVal自己 退一层0
                VarValue v0 = this.assignTempVarValue();
                new GetPtrInstSureArray(curBasicBlock, v0, defedValue, 0);
                return v0;
            } else if (len == 1) {
                //一维：退一层n变为i32* + load出i32  a[1]
                //二维：退一层n变为一维，再退一层0变为i32*  b[1]
                Value n = this.visitAddExp(lVal.exps.get(0).addExp);
                if (((SureArrayType) targetType).type == 0) {
                    //一维
                    VarValue v0 = this.assignTempVarValue();
                    new GetPtrInstSureArray(curBasicBlock, v0, defedValue, n);
                    VarValue v1 = this.assignTempVarValue();
                    new LoadInst(curBasicBlock, v1, v0);
                    return v1;
                } else {
                    //二维
                    VarValue v0 = this.assignTempVarValue();
                    new GetPtrInstSureArray(curBasicBlock, v0, defedValue, n);
                    VarValue v1 = this.assignTempVarValue();
                    new GetPtrInstSureArray(curBasicBlock, v1, v0, 0);
                    return v1;
                }
            } else {
                //len = 2;
                Value n = this.visitAddExp(lVal.exps.get(0).addExp);
                Value m = this.visitAddExp(lVal.exps.get(1).addExp);
                VarValue v0 = this.assignTempVarValue();
                new GetPtrInstSureArray(curBasicBlock, v0, defedValue, n, m);
                VarValue v1 = this.assignTempVarValue();
                new LoadInst(curBasicBlock, v1, v0);
                return v1;
            }
        } else {
            //lVal是形参数组
            //targetType是Pointer
            int len = lVal.exps.size();
            Type targetTypeInside = ((PointerType) targetType).targetType;
            if (len == 0) {
                //形参它自己 退掉pointer
                //load
                VarValue v0 = this.assignTempVarValue();
                new LoadInst(curBasicBlock, v0, defedValue);
                return v0;
            } else if (len == 1) {
                //形参的第一维
                //一维数组 load出(Pointer(Integer)) +
                // getPtr n（不是SureArray,没有第一个0*（等于不退层）)
                // + load出i32
                Value n = this.visitAddExp(lVal.exps.get(0).addExp);
                if (targetTypeInside instanceof IntegerType) {
                    VarValue v0 = this.assignTempVarValue();
                    new LoadInst(curBasicBlock, v0, defedValue);
                    VarValue v1 = this.assignTempVarValue();//*i32
                    new GetPtrNormal(curBasicBlock, v1, v0, n);
                    VarValue v2 = this.assignTempVarValue();
                    new LoadInst(curBasicBlock, v2, v1);
                    return v2;
                } else {
                    VarValue v0 = this.assignTempVarValue();
                    new LoadInst(curBasicBlock, v0, defedValue);
                    VarValue v1 = this.assignTempVarValue();//[n*i32]*
                    new GetPtrNormal(curBasicBlock, v1, v0, n);
                    VarValue v2 = this.assignTempVarValue();
                    new GetPtrInstSureArray(curBasicBlock, v2, v1, 0);
                    return v2;
                }
                //二维数组 load出(Pointer(nxi32SureArray)) +
                // getPtr n（不是SureArray,没有第一个0*（等于不退层）)
                //按照一维数组获得自己的方式获得自己
            } else {
                Value n = this.visitAddExp(lVal.exps.get(0).addExp);
                Value m = this.visitAddExp(lVal.exps.get(1).addExp);
                //load出内层[m x i32]*
                VarValue v0 = this.assignTempVarValue();
                new LoadInst(curBasicBlock, v0, defedValue);
                VarValue v1 = this.assignTempVarValue();//[m*i32]*
                new GetPtrNormal(curBasicBlock, v1, v0, n);
                VarValue v2 = this.assignTempVarValue();
                new GetPtrInstSureArray(curBasicBlock, v2, v1, m);
                VarValue v3 = this.assignTempVarValue();
                new LoadInst(curBasicBlock, v3, v2);
                return v3;
            }
        }
//        return new Value();

    }

    private VarValue assignTempVarValue() {
        int registerNum = curFunction.assignRegister();
        return new VarValue(registerNum, false);
    }

    public void visitStmtReturn(StmtReturn stmt) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        if (stmt.type == 0) {
            RetInst retInst = new RetInst(curBasicBlock);
        } else {
            Exp exp = stmt.exp;
            //根据exp构造value
            Value value = this.visitAddExp(exp.addExp);//形成一系列指令 并将最终的寄存器即Var型Value保存到saveValue中
            RetInst retInst = new RetInst(curBasicBlock, value.getMyType(), value);
        }
    }

    public void visitStmtBC(StmtBC stmt) {
        //'break' ';' | 'continue' ';'
        RefillFor refillFor = this.forStacks.get(forStacks.size() - 1);
        if (stmt.token.getCategory() == Category.CONTINUETK) {
//            if(refillFor.forStmt2Block != null){
//                //按照for的设计必有 但是可能在这个时候还没赋值
//                new BrInst(curFunction.getCurBasicBlock(),refillFor.forStmt2Block);
//            }
            BrInst br = new BrInst(curFunction.getCurBasicBlock(), refillFor.forStmt2Block);
            refillFor.dstAsForStmt2.add(br);
            //refillFor.continues.add(curFunction.getCurBasicBlock());
        } else {
            //break;
            BrInst br = new BrInst(curFunction.getCurBasicBlock(), refillFor.endBlock);
            refillFor.dstAsEnd.add(br);
//            refillFor.breaks.add(curFunction.getCurBasicBlock());
        }
    }

    public void visitStmtBlock(StmtBlock stmt) {
        this.tableList.addTable();
        this.visitBlock(stmt.block);
        this.tableList.popTable();
    }

    public void visitStmtExp(StmtExp stmt) {
        if (stmt.type == 1) {
            this.visitAddExp(stmt.exp.addExp);
        }
    }

    private ArrayList<RefillFor> forStacks = new ArrayList<>();

    //通过这个在break和continue的时候找到所属的for语句块
    public void visitStmtFor(StmtFor stmtFor) {
        ForStmt forStmt1 = stmtFor.forStmt1;
        ForStmt forStmt2 = stmtFor.forStmt2;
        Cond cond = stmtFor.cond;
        RefillFor refillFor = new RefillFor(cond != null);
        this.forStacks.add(refillFor);
        //以上都有可能是null
        Stmt stmt = stmtFor.stmt;
        //以上 解析出AST树的部分
        if (forStmt1 != null) {
            this.visitForStmt(forStmt1);
        }
        //refill
        ArrayList<BasicBlock> zeroLevel = new ArrayList<>();
        zeroLevel.add(curFunction.getCurBasicBlock());
        refillFor.refillBasicBlocks.add(zeroLevel);
        //以上 zerolevel
        if (cond != null) {
            this.visitLOrExp(cond.exp, refillFor);
        }
        curFunction.addBasicBlock();
        //决定由cond跳进来的dst
        refillFor.realTrueBlock1 = curFunction.getCurBasicBlock();
        this.visitStmt(stmt);
        //决定跳走到end的from
        refillFor.realTrueBlock2 = curFunction.getCurBasicBlock();//refill

        curFunction.addBasicBlock();
        refillFor.forStmt2Block = curFunction.getCurBasicBlock();//refill
        if (forStmt2 != null) {
            this.visitForStmt(forStmt2);
        } else {
        }
        curFunction.addBasicBlock();
        refillFor.endBlock = curFunction.getCurBasicBlock();//refill
        refillFor.refill();
        this.forStacks.remove(forStacks.size() - 1);//pop
    }

    public void visitForStmt(ForStmt forStmt) {
        LVal lVal = forStmt.lVal;
        Exp exp = forStmt.exp;
        Value v = this.visitAddExp(exp.addExp);
        this.storeLValWithValue(lVal, v);
    }

    public void visitStmtIf(StmtIf stmt) {
//        this.inStmtIf = true;
        RefillIf refillIf = new RefillIf();//获得独特的refill工具
        Cond cond = stmt.cond;
        LOrExp lOrExp = cond.exp;
        //根据cond生成代码放入block0
        //refill
        ArrayList<BasicBlock> zeroLevel = new ArrayList<>();
        zeroLevel.add(curFunction.getCurBasicBlock());
        refillIf.refillBasicBlocks.add(zeroLevel);
        this.visitLOrExp(lOrExp, refillIf);
        //以下是管理Stmt
        curFunction.addBasicBlock();
        BasicBlock block1 = curFunction.getCurBasicBlock();
        refillIf.realTrueBlock1 = block1;
        Stmt trueStmt = stmt.stmt;
        refillIf.realTrueBlock2 = curFunction.getCurBasicBlock();//refill

        this.visitStmt(trueStmt);
        if (stmt.type == 1) {
            curFunction.addBasicBlock();
            BasicBlock block2 = curFunction.getCurBasicBlock();
            refillIf.realFalseBlock = block2;
            Stmt falseStmt = stmt.elseStmt;
            this.visitStmt(falseStmt);
        }
        curFunction.addBasicBlock();
        refillIf.endBlock = curFunction.getCurBasicBlock();
        //之后的代码都放入空新block里
        refillIf.refill(); //回填
//        this.inStmtIf = false;
    }

    public void visitLOrExp(LOrExp exp, RefillUtil refillUtil) {
        for (int i = 0; i < exp.exps.size(); i++) {
            LAndExp lAndExp = exp.exps.get(i);
            //refill
            refillUtil.tempLevel = new ArrayList<>();
            this.visitLAndExp(lAndExp, refillUtil);
            refillUtil.refillBasicBlocks.add(refillUtil.tempLevel);
        }
    }

    public void visitLAndExp(LAndExp exp, RefillUtil refillUtil) {
        for (int i = 0; i < exp.exps.size(); i++) {
            EqExp eqExp = exp.exps.get(i);
            this.visitEqExp(eqExp, refillUtil);
        }
    }

    public void visitEqExp(EqExp exp, RefillUtil refillUtil) {
        //每一个EqExp代表一个新的BasicBlock
        curFunction.addBasicBlock();
        BasicBlock b = curFunction.getCurBasicBlock();
        refillUtil.tempLevel.add(b);
        Value result;//eqExp的结果
        if (exp.relExps.size() >= 2) {
            //有两个以上的RelExp
            Value v1 = this.visitRelExp(exp.relExps.get(0));
            Value v2 = this.visitRelExp(exp.relExps.get(1));
            CondString condString = exp.tokens.get(0).getCategory() == Category.EQL ?
                    CondString.eq : CondString.ne;
            int registerNum = curFunction.assignRegister();
            VarValue var1 = new VarValue(registerNum, false, true);
            new IcmpInst(b, var1, v1, v2, condString);
            registerNum = curFunction.assignRegister();
            VarValue var32 = new VarValue(registerNum, false);
            new ZextInst(b, var32, var1);
            int i = 2, j = 1;
            Value pre = var32;
            while (i < exp.relExps.size() && j < exp.tokens.size()) {
                CondString tempCondString = exp.tokens.get(j).getCategory() == Category.EQL ?
                        CondString.eq : CondString.ne;
                Value v3 = this.visitRelExp(exp.relExps.get(i));
                //分配结果寄存器
                int tempRegisterNum = curFunction.assignRegister();
                VarValue tempVar1 = new VarValue(tempRegisterNum, false, true);
                //生成指令
                new IcmpInst(b, tempVar1, pre, v3, tempCondString);
                tempRegisterNum = curFunction.assignRegister();
                VarValue tempVar32 = new VarValue(tempRegisterNum, false);
                new ZextInst(b, tempVar32, tempVar1);
                pre = tempVar32;
                //
                i++;
                j++;
            }
            result = pre;
        } else {
            //只有一个RelExp
            result = this.visitRelExp(exp.relExps.get(0));
        }
        //将result 与 0 做 ne 比较
        int r = curFunction.assignRegister();
        VarValue reVar = new VarValue(r, false, true);
        new IcmpInst(b, reVar, result, new ConstValue("0"), CondString.ne);
        b.reVar = reVar;//refill
    }

    public Value visitRelExp(RelExp relExp) {
        //< slt
        //<= sle
        //> sgt
        //>= sge
        BasicBlock b = curFunction.getCurBasicBlock();
        if (relExp.addExps.size() >= 2) {
            Value v1 = this.visitAddExp(relExp.addExps.get(0));
            Value v2 = this.visitAddExp(relExp.addExps.get(1));
            CondString condString = CondString.eq;//初始化为一个不可能的
            switch (relExp.tokens.get(0).getCategory()) {
                case LSS:
                    condString = CondString.slt;
                    break;
                case LEQ:
                    condString = CondString.sle;
                    break;
                case GRE:
                    condString = CondString.sgt;
                    break;
                case GEQ:
                    condString = CondString.sge;
                    break;
            }
            int registerNum = curFunction.assignRegister();
            VarValue var1 = new VarValue(registerNum, false, true);
            new IcmpInst(b, var1, v1, v2, condString);
            registerNum = curFunction.assignRegister();
            VarValue var32 = new VarValue(registerNum, false);
            new ZextInst(b, var32, var1);
            int i = 2, j = 1;
            Value pre = var32;
            while (i < relExp.addExps.size() && j < relExp.tokens.size()) {
                Value v3 = this.visitAddExp(relExp.addExps.get(i));
                CondString temp = CondString.eq;
                switch (relExp.tokens.get(j).getCategory()) {
                    case LSS:
                        temp = CondString.slt;
                        break;
                    case LEQ:
                        temp = CondString.sle;
                        break;
                    case GRE:
                        temp = CondString.sgt;
                        break;
                    case GEQ:
                        temp = CondString.sge;
                        break;
                }
                int tempRegisterNum = curFunction.assignRegister();
                VarValue tempVar1 = new VarValue(tempRegisterNum, false, true);
                new IcmpInst(b, tempVar1, pre, v3, temp);
                tempRegisterNum = curFunction.assignRegister();
                VarValue tempVar32 = new VarValue(tempRegisterNum, false);
                new ZextInst(b, tempVar32, tempVar1);
                pre = tempVar32;
                //
                i++;
                j++;
            }
            return pre;
        } else {
            //只有一个AddExp
            return this.visitAddExp(relExp.addExps.get(0));
        }
    }

    public void visitStmtLValExp(StmtLValExp stmt) {
        //store
        Value v = this.visitAddExp(stmt.exp.addExp);
        this.storeLValWithValue(stmt.lVal, v);
    }

    public void visitStmtPrint(StmtPrint stmt) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        String s = stmt.token.getName();
        ArrayList<Exp> exps = stmt.exps;
        int j = 0;
        int i;
        for (i = 1; i < s.length() - 1; ) {
            //第一个和最后一个字符是“
            if (s.charAt(i) == '%') {
                //%d
                Value v = this.visitAddExp(exps.get(j).addExp);
                j++;
                new LibraryCallInst(curBasicBlock, 3, v);
                i++;
            } else if (s.charAt(i) == '\\') {
                //\n
                new LibraryCallInst(curBasicBlock, 2, new ConstValue("10"));
                i++;
            } else {
                int cAscii = s.charAt(i);
                new LibraryCallInst(curBasicBlock, 2, new ConstValue(String.valueOf(cAscii)));
            }
            i++;
        }
    }

    public void visitStmtRead(StmtRead stmt) {
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        LVal lVal = stmt.lVal;
        int registerNum = curFunction.assignRegister();
        VarValue value = new VarValue(registerNum, false);
        new LibraryCallInst(curBasicBlock, 1, value);
        this.storeLValWithValue(lVal, value);
    }

    private void storeLValWithValue(LVal lVal, Value value) {
        //lVal在左边
        //与visitLVal那种lVal在右边需要被读取的感觉不一样
        BasicBlock curBasicBlock = curFunction.getCurBasicBlock();
        Value foundDef = this.tableList.foundDef(lVal.ident.getName());
        Value toValue = foundDef;
        Type targetType = ((PointerType) toValue.getMyType()).targetType;
        if (!(targetType instanceof IntegerType)) {
            //数组
            if(targetType instanceof SureArrayType){
                //实在的
                int len = lVal.exps.size();
                if (len == 1) {
                    //a[1] = value
                    Value n = this.visitAddExp(lVal.exps.get(0).addExp);
                    VarValue v0 = this.assignTempVarValue();
                    new GetPtrInstSureArray(curBasicBlock, v0, foundDef, n);
                    toValue = v0;
                } else {
                    //a[2] = value
                    Value n = this.visitAddExp(lVal.exps.get(0).addExp);
                    Value m = this.visitAddExp(lVal.exps.get(1).addExp);
                    VarValue v0 = this.assignTempVarValue();
                    new GetPtrInstSureArray(curBasicBlock, v0, foundDef, n, m);
                    toValue = v0;
                }
            } else {
                // 形参数组
                int len = lVal.exps.size();
                if(len == 1){
                    //a[n]
                    Value n = this.visitAddExp(lVal.exps.get(0).addExp);
                    VarValue v0 = this.assignTempVarValue();
                    new LoadInst(curBasicBlock,v0,foundDef);
                    VarValue v1 = this.assignTempVarValue();
                    new GetPtrNormal(curBasicBlock,v1,v0,n);
                    toValue = v1;
                } else {
                    //len == 2
                    Value n = this.visitAddExp(lVal.exps.get(0).addExp);
                    Value m = this.visitAddExp(lVal.exps.get(1).addExp);
                    VarValue v0 = this.assignTempVarValue();
                    new LoadInst(curBasicBlock,v0,foundDef);
                    VarValue v1 = this.assignTempVarValue();
                    new GetPtrNormal(curBasicBlock,v1,v0,n);
                    VarValue v2 = this.assignTempVarValue();
                    new GetPtrInstSureArray(curBasicBlock,v2,v1,m);
                    toValue = v2;
                }
            }
            //toValue = this.visitLVal(lVal);
        }
        new StoreInst(curBasicBlock, value, toValue);
    }

}
